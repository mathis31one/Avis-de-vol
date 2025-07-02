package org.example.avisdevolss.repository;

import org.example.avisdevolss.entity.Review;
import org.example.avisdevolss.entity.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer>, JpaSpecificationExecutor<Review> {

    // Method to find reviews by flight id
    List<Review> findByFlightId(Integer flightId);

    // Method to find reviews by account ID
    List<Review> findByAccountId(Integer accountId);

    // Method to find reviews by status
    List<Review> findByStatus(ReviewStatus status);

    // Method to count reviews by flight id
    int countByFlightId(Integer flightId);

    // Method to count all reviews
    @Query("SELECT COUNT(r) FROM Review r")
    long countAllReviews();
}
