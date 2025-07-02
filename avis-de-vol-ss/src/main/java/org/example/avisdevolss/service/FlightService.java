package org.example.avisdevolss.service;

import org.example.avisdevolss.controller.FlightController;
import org.example.avisdevolss.entity.Flight;
import org.example.avisdevolss.repository.FlightRepository;
import org.example.avisdevolss.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public FlightService(FlightRepository flightRepository, ReviewRepository reviewRepository) {
        this.flightRepository = flightRepository;
        this.reviewRepository = reviewRepository;
    }

    public Flight createFlight(Flight flight) {
        if (flightRepository.existsByFlightNumber(flight.getFlightNumber())) {
            throw new IllegalArgumentException("Flight with this number already exists.");
        }
        return flightRepository.save(flight);
    }

    public Flight updateFlight(Integer id, Flight flight) {
        Optional<Flight> existingFlight = flightRepository.findById(id);
        if (existingFlight.isEmpty()) {
            throw new IllegalArgumentException("Flight not found with id: " + id);
        }

        Flight flightToUpdate = existingFlight.get();
        if (!flightToUpdate.getFlightNumber().equals(flight.getFlightNumber()) &&
                flightRepository.existsByFlightNumber(flight.getFlightNumber())) {
            throw new IllegalArgumentException("Flight with this number already exists.");
        }

        flightToUpdate.setFlightNumber(flight.getFlightNumber());
        flightToUpdate.setCompany(flight.getCompany());
        flightToUpdate.setDate(flight.getDate());

        return flightRepository.save(flightToUpdate);
    }

    public void deleteFlight(Integer id) {
        if (!flightRepository.existsById(id)) {
            throw new IllegalArgumentException("Flight not found with id: " + id);
        }

        // Check if flight has associated reviews
        if (reviewRepository.existsByFlightId(id)) {
            throw new IllegalArgumentException("Cannot delete flight with id: " + id + " because it has associated reviews");
        }

        flightRepository.deleteById(id);
    }

    public List<Flight> findAllFlights() {
        return flightRepository.findAll();
    }

    public Optional<Flight> findById(Integer id) {
        return flightRepository.findById(id);
    }

    public boolean existsByFlightNumber(String flightNumber) {
        return flightRepository.existsByFlightNumber(flightNumber);
    }

    public List<Flight> findByCompany(String company) {
        return flightRepository.findByCompanyContainingIgnoreCase(company);
    }

    public List<Flight> findByDate(Date startDate, Date endDate) {
        return flightRepository.findByDateBetween(startDate, endDate);
    }

    public List<Flight> findFlights(String company, Date startDate, Date endDate) {
        List<Flight> flights = flightRepository.findAll();

        // Filter by company if provided
        if (company != null && !company.trim().isEmpty()) {
            flights = flights.stream()
                    .filter(flight -> flight.getCompany().toLowerCase().contains(company.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Filter by date range if provided
        if (startDate != null && endDate != null) {
            flights = flights.stream()
                    .filter(flight -> {
                        Date flightDate = flight.getDate();
                        return !flightDate.before(startDate) && !flightDate.after(endDate);
                    })
                    .collect(Collectors.toList());
        } else if (startDate != null) {
            // Only start date provided
            flights = flights.stream()
                    .filter(flight -> !flight.getDate().before(startDate))
                    .collect(Collectors.toList());
        } else if (endDate != null) {
            // Only end date provided
            flights = flights.stream()
                    .filter(flight -> !flight.getDate().after(endDate))
                    .collect(Collectors.toList());
        }

        return flights;
    }

    public List<String> findAllCompanies() {
        return flightRepository.findAllCompanies();
    }

    public long getCountFlights() {
        return flightRepository.countAllFlights();
    }

    public long getCountCompany() {
        return flightRepository.countDistinctCompanies();
    }
}
