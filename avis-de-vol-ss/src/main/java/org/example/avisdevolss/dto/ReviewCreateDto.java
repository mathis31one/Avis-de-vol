package org.example.avisdevolss.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewCreateDto {
    private String content;
    private int notation;
    private Integer flightId;
}
