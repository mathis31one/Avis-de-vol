package org.example.avisdevolss.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.avisdevolss.entity.ReviewStatus;

@Getter
@Setter
public class ReviewResponseDto {
    private Integer id;
    private String content;
    private int notation;
    private ReviewStatus status;
    private String accountFirstName;
    private String accountLastName;
    private String flightNumber;
    private String company;
}
