package com.example.mobile_applications_project_2025.DTO;

import java.util.List;

public class StatsResponseDTO {
    private List<DailyStatPointDTO> points;

    public StatsResponseDTO() {}

    public List<DailyStatPointDTO> getPoints() { return points; }
    public void setPoints(List<DailyStatPointDTO> points) { this.points = points; }
}