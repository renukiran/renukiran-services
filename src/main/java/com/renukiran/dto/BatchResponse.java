package com.renukiran.dto;

import com.renukiran.entity.Batch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BatchResponse {
    private Long dbId;
    private String id;          // batchCode — matches UI field name
    private String course;
    private String trainer;
    private String location;
    private String dates;
    private String startDate;
    private String endDate;
    private Integer enrolled;
    private Integer max;
    private String status;
    private String notes;

    public static BatchResponse from(Batch b) {
        return BatchResponse.builder()
                .dbId(b.getDbId())
                .id(b.getBatchCode())
                .course(b.getCourse())
                .trainer(b.getTrainer())
                .location(b.getLocation())
                .dates(b.getDates())
                .startDate(b.getStartDate())
                .endDate(b.getEndDate())
                .enrolled(b.getEnrolled())
                .max(b.getMax())
                .status(b.getStatus())
                .notes(b.getNotes())
                .build();
    }
}
