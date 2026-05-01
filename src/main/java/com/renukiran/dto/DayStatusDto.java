package com.renukiran.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DayStatusDto {
    private LocalDate date;
    private String status; // PRESENT / ABSENT / NOT_MARKED
}
