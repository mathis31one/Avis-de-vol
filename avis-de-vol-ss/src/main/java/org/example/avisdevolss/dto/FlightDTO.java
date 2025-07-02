package org.example.avisdevolss.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class FlightDTO {
    private Integer id;
    private String flightNumber;
    private String company;
    private Date date;
}
