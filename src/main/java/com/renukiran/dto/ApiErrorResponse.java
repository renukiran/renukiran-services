package com.renukiran.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorResponse<T> {
    private int status;
    private String errorCode;
    private String message;
    private List<String> details;
    private T payload; // optional additional data
}
