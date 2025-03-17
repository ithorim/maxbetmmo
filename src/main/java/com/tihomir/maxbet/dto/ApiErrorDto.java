package com.tihomir.maxbet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorDto {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private String path;
}
