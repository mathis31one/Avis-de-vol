package org.example.avisdevolss.repository;

import org.example.avisdevolss.entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Integer> {
    List<Response> findByReviewId(Integer reviewId);
    List<Response> findByUserId(Integer userId);
}
