package com.renukiran.dto;

import com.renukiran.entity.APIStatus;
import lombok.Data;

@Data
public class BaseResponse<T> {

    private APIStatus status;

     private String message;

     private T data;
}
