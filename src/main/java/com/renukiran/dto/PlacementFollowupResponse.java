package com.renukiran.dto;

import com.renukiran.entity.PlacementFollowup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PlacementFollowupResponse {
    private Long id;
    private String label;
    private String date;
    private Boolean done;
    private Boolean overdue;
    private String note;
    private String statusAtCheck;
    private Integer salaryAtCheck;

    public static PlacementFollowupResponse from(PlacementFollowup f) {
        return PlacementFollowupResponse.builder()
                .id(f.getId())
                .label(f.getLabel())
                .date(f.getDate())
                .done(f.getDone())
                .overdue(f.getOverdue())
                .note(f.getNote())
                .statusAtCheck(f.getStatusAtCheck())
                .salaryAtCheck(f.getSalaryAtCheck())
                .build();
    }
}
