package org.example.avisdevolss.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ResponseCreateDto {
    @NotNull(message = "Review ID cannot be null")
    private Integer reviewId;

    @NotBlank(message = "Content cannot be blank")
    private String content;
}
