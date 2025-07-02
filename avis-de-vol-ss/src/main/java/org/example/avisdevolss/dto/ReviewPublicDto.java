package org.example.avisdevolss.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.avisdevolss.entity.ReviewStatus;

import java.util.List;

@Getter
@Setter
public class ReviewPublicDto {
    private Integer id;
    private String content;
    private int notation;
    private ReviewStatus status;
    private String flightNumber;
    private String company;
    private List<ResponseDto> responses;
    // Pas d'informations sur le compte pour les utilisateurs non-admin
}
