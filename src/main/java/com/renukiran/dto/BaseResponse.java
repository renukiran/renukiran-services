package com.renukiran.dto;

import com.renukiran.entity.APIStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BaseResponse<T> {

    private APIStatus status;

     private String message;

     private T data;
}
