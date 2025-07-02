package org.example.avisdevolss.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto {
    private Integer id;
    private String content;
    private Integer reviewId;
    private Integer userId;
    private String userFirstName;
    private String userLastName;
}
