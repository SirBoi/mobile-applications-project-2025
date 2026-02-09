package com.project2025.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project2025.model.UserActivitySession;

@Repository
public interface UserActivitySessionRepository extends JpaRepository<UserActivitySession, Long> {

    // Current open session (if any)
    @Query("""
        select s
        from UserActivitySession s
        where s.user.id = :userId and s.endedAt is null
        order by s.startedAt desc
    """)
    Optional<UserActivitySession> findOpenSession(@Param("userId") Long userId);

    // All sessions that overlap [from, to]
    @Query("""
        select s
        from UserActivitySession s
        where s.user.id = :userId
          and s.startedAt <= :to
          and (s.endedAt is null or s.endedAt >= :from)
    """)
    List<UserActivitySession> findOverlapping(@Param("userId") Long userId,
                                             @Param("from") LocalDateTime from,
                                             @Param("to") LocalDateTime to);

    // All open sessions that are stale (no heartbeat since cutoff)
    @Query("""
        select s
        from UserActivitySession s
        where s.endedAt is null
          and s.lastHeartbeatAt < :cutoff
    """)
    List<UserActivitySession> findStaleOpenSessions(@Param("cutoff") LocalDateTime cutoff);
}
