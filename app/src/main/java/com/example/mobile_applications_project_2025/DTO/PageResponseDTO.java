package com.example.mobile_applications_project_2025.DTO;

import java.util.List;

public class PageResponseDTO<T> {
    public List<T> content;
    public int number;       // 0-based page index
    public int totalPages;
    public long totalElements;
    public boolean first;
    public boolean last;
    public int size;
    public int numberOfElements;
}