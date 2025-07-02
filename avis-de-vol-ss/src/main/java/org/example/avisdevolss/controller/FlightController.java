package org.example.avisdevolss.controller;

import org.example.avisdevolss.dto.FlightDTO;
import org.example.avisdevolss.entity.Flight;
import org.example.avisdevolss.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService flightService;

    @Autowired
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PostMapping
    public ResponseEntity<?> createFlight(@RequestBody FlightDTO flightDTO) {
        try {
            Flight flight = convertToEntity(flightDTO);
            Flight createdFlight = flightService.createFlight(flight);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(createdFlight));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFlight(@PathVariable Integer id, @RequestBody FlightDTO flightDTO) {
        try {
            Flight flight = convertToEntity(flightDTO);
            Flight updatedFlight = flightService.updateFlight(id, flight);
            return ResponseEntity.ok(convertToDTO(updatedFlight));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Integer id) {
        try {
            flightService.deleteFlight(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<FlightDTO>> findAllFlights(
            @RequestParam(required = false) String company,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        
        List<Flight> flights = flightService.findFlights(company, startDate, endDate);
        List<FlightDTO> flightDTOs = flights.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(flightDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightDTO> findById(@PathVariable Integer id) {
        Optional<Flight> flight = flightService.findById(id);
        return flight.map(f -> ResponseEntity.ok(convertToDTO(f)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/exists/{flightNumber}")
    public ResponseEntity<Boolean> existsByFlightNumber(@PathVariable String flightNumber) {
        boolean exists = flightService.existsByFlightNumber(flightNumber);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/company/{company}")
    public ResponseEntity<List<FlightDTO>> findByCompany(@PathVariable String company) {
        List<Flight> flights = flightService.findByCompany(company);
        List<FlightDTO> flightDTOs = flights.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(flightDTOs);
    }

    @GetMapping("/companies")
    public ResponseEntity<List<String>> findAllCompanies() {
        List<String> companies = flightService.findAllCompanies();
        return ResponseEntity.ok(companies);
    }

    private FlightDTO convertToDTO(Flight flight) {
        FlightDTO dto = new FlightDTO();
        dto.setId(flight.getId());
        dto.setFlightNumber(flight.getFlightNumber());
        dto.setCompany(flight.getCompany());
        dto.setDate(flight.getDate());
        return dto;
    }

    private Flight convertToEntity(FlightDTO dto) {
        Flight flight = new Flight();
        flight.setId(dto.getId());
        flight.setFlightNumber(dto.getFlightNumber());
        flight.setCompany(dto.getCompany());
        flight.setDate(dto.getDate());
        return flight;
    }

    // Error response class
    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
