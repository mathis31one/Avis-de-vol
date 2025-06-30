package org.example.avisdevolss.repository;

import org.example.avisdevolss.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Integer> {

    Optional<Flight> findByFlightNumber(String flightNumber);

    boolean existsByFlightNumber(String flightNumber);

    List<Flight> findByCompanyContainingIgnoreCase(String company);

    List<Flight> findByDateBetween(Date startDate, Date endDate);

    List<Flight> findByFlightNumberContainingIgnoreCase(String flightNumber);
}
