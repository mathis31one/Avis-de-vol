package org.example.avisdevolss.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.avisdevolss.entity.ReviewStatus;

@Getter
@Setter
public class ReviewFilterDto {
    private String company;
    private Integer accountId;
    private Integer notation;
    private ReviewStatus status;
}
