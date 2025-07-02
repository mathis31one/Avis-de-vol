package org.example.avisdevolss.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.avisdevolss.dto.ResponseCreateDto;
import org.example.avisdevolss.dto.ResponseDto;
import org.example.avisdevolss.entity.Account;
import org.example.avisdevolss.entity.Response;
import org.example.avisdevolss.entity.Review;
import org.example.avisdevolss.repository.AccountRepository;
import org.example.avisdevolss.repository.ResponseRepository;
import org.example.avisdevolss.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResponseService {

    private final ResponseRepository responseRepository;
    private final ReviewRepository reviewRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public ResponseDto createResponse(ResponseCreateDto createDto, Integer userId) {
        log.info("Creating response for review {} by user {}", createDto.getReviewId(), userId);

        // Verify review exists
        Review review = reviewRepository.findById(createDto.getReviewId())
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        // Verify user exists
        Account user = accountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Create response entity
        Response response = new Response();
        response.setContent(createDto.getContent());
        response.setReview(review);
        response.setUser(user);

        Response savedResponse = responseRepository.save(response);
        log.info("Response created successfully with ID: {}", savedResponse.getId());

        return convertToDto(savedResponse);
    }

    @Transactional(readOnly = true)
    public List<ResponseDto> getResponsesByReviewId(Integer reviewId) {
        log.info("Fetching responses for review: {}", reviewId);
        List<Response> responses = responseRepository.findByReviewId(reviewId);
        return responses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResponseDto> getResponsesByUserId(Integer userId) {
        log.info("Fetching responses by user: {}", userId);
        List<Response> responses = responseRepository.findByUserId(userId);
        return responses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ResponseDto> getResponseById(Integer id) {
        log.info("Fetching response: {}", id);
        return responseRepository.findById(id)
                .map(this::convertToDto);
    }

    @Transactional
    public void deleteResponse(Integer id, Integer userId) {
        log.info("Attempting to delete response {} by user {}", id, userId);

        Response response = responseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Response not found"));

        // Only allow deletion by the response author
        if (!response.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to delete this response");
        }

        responseRepository.delete(response);
        log.info("Response {} deleted successfully", id);
    }

    @Transactional
    public ResponseDto updateResponse(Integer id, ResponseCreateDto updateDto, Integer userId) {
        log.info("Attempting to update response {} by user {}", id, userId);

        Response response = responseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Response not found"));

        // Only allow update by the response author
        if (!response.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to update this response");
        }

        response.setContent(updateDto.getContent());
        Response updatedResponse = responseRepository.save(response);
        log.info("Response {} updated successfully", id);

        return convertToDto(updatedResponse);
    }

    private ResponseDto convertToDto(Response response) {
        return new ResponseDto(
                response.getId(),
                response.getContent(),
                response.getReview().getId(),
                response.getUser().getId(),
                response.getUser().getFirstName(),
                response.getUser().getLastName()
        );
    }
}
