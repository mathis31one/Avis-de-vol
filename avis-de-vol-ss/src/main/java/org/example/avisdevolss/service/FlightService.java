package org.example.avisdevolss.service;

import org.example.avisdevolss.entity.Flight;
import org.example.avisdevolss.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    @Autowired
    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
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
}
