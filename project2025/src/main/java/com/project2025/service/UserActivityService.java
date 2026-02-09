package com.project2025.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project2025.model.RegisteredUser;
import com.project2025.model.UserActivitySession;
import com.project2025.repository.RegisteredUserRepository;
import com.project2025.repository.UserActivitySessionRepository;

@Service
public class UserActivityService {

    // heartbeat every 20s, session ends if missing for 60s
    private static final Duration HEARTBEAT_TIMEOUT = Duration.ofMinutes(1);

    private final UserActivitySessionRepository sessionRepository;
    private final RegisteredUserRepository registeredUserRepository;

    public UserActivityService(UserActivitySessionRepository sessionRepository,
                               RegisteredUserRepository registeredUserRepository) {
        this.sessionRepository = sessionRepository;
        this.registeredUserRepository = registeredUserRepository;
    }

    /**
     * Called every ~20s by the app while "Active".
     * If no open session exists, this starts a new one.
     * If open session exists, it updates lastHeartbeatAt.
     */
    @Transactional
    public void heartbeat(Long userId) {
        LocalDateTime now = LocalDateTime.now();

        UserActivitySession session = sessionRepository.findOpenSession(userId)
                .orElseGet(() -> startNewSession(userId, now));

        session.setLastHeartbeatAt(now);
        sessionRepository.save(session);
    }

    /**
     * Manually stop the current open session (if present).
     * End time is "now".
     */
    @Transactional
    public void stopSession(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        sessionRepository.findOpenSession(userId).ifPresent(s -> {
            s.setEndedAt(now);
            // Keep lastHeartbeatAt as-is (or set to now; both OK)
            sessionRepository.save(s);
        });
    }

    /**
     * Close stale sessions (no heartbeat for 1 minute).
     * End time is lastHeartbeatAt + timeout (grace period).
     */
    @Transactional
    public int closeStaleSessions() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoff = now.minus(HEARTBEAT_TIMEOUT);

        List<UserActivitySession> stale = sessionRepository.findStaleOpenSessions(cutoff);
        for (UserActivitySession s : stale) {
            LocalDateTime effectiveEnd = s.getLastHeartbeatAt().plus(HEARTBEAT_TIMEOUT);
            // don't end in the future
            if (effectiveEnd.isAfter(now)) effectiveEnd = now;
            s.setEndedAt(effectiveEnd);
            sessionRepository.save(s);
        }
        return stale.size();
    }

    /**
     * Total active seconds in the last 24 hours.
     * For open sessions, counts only until min(now, lastHeartbeatAt+timeout).
     */
    @Transactional(readOnly = true)
    public long getActiveSecondsLast24h(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusHours(24);

        List<UserActivitySession> sessions = sessionRepository.findOverlapping(userId, from, now);

        long total = 0;
        for (UserActivitySession s : sessions) {
            LocalDateTime sessionStart = s.getStartedAt().isBefore(from) ? from : s.getStartedAt();

            LocalDateTime sessionEnd;
            if (s.getEndedAt() != null) {
                sessionEnd = s.getEndedAt();
            } else {
                // open session: end is bounded by heartbeat timeout
                LocalDateTime effectiveEnd = s.getLastHeartbeatAt().plus(HEARTBEAT_TIMEOUT);
                sessionEnd = effectiveEnd.isAfter(now) ? now : effectiveEnd;
            }

            if (sessionEnd.isAfter(now)) sessionEnd = now;

            if (sessionEnd.isAfter(sessionStart)) {
                total += Duration.between(sessionStart, sessionEnd).getSeconds();
            }
        }
        return total;
    }

    private UserActivitySession startNewSession(Long userId, LocalDateTime now) {
        RegisteredUser userRef = registeredUserRepository.getReferenceById(userId);

        UserActivitySession s = new UserActivitySession();
        s.setUser(userRef);
        s.setStartedAt(now);
        s.setEndedAt(null);
        s.setLastHeartbeatAt(now);
        return sessionRepository.save(s);
    }
}
