package com.example.mobile_applications_project_2025.DTO;

public class ActiveTimeResponseDTO {
    private Long userId;
    private long totalSecondsLast24h;

    public ActiveTimeResponseDTO() {}

    public ActiveTimeResponseDTO(Long userId, long totalSecondsLast24h) {
        this.userId = userId;
        this.totalSecondsLast24h = totalSecondsLast24h;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public long getTotalSecondsLast24h() { return totalSecondsLast24h; }
    public void setTotalSecondsLast24h(long totalSecondsLast24h) { this.totalSecondsLast24h = totalSecondsLast24h; }
}