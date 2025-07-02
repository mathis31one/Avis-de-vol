package org.example.avisdevolss.repository;

import org.example.avisdevolss.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Integer> {

    Optional<Flight> findByFlightNumber(String flightNumber);

    boolean existsByFlightNumber(String flightNumber);

    List<Flight> findByCompanyContainingIgnoreCase(String company);

    List<Flight> findByDateBetween(Date startDate, Date endDate);

    List<Flight> findByFlightNumberContainingIgnoreCase(String flightNumber);

    @Query("SELECT DISTINCT f.company FROM Flight f ORDER BY f.company")
    List<String> findAllCompanies();

    @Query("SELECT MIN(f.date) FROM Flight f")
    Date findMinDate();

    @Query("SELECT MAX(f.date) FROM Flight f")
    Date findMaxDate();

    @Query("SELECT COUNT(f) FROM Flight f")
    long countAllFlights();

    @Query("SELECT COUNT(DISTINCT f.company) FROM Flight f")
    long countDistinctCompanies();
}
