package org.example.avisdevolss.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.avisdevolss.dto.ResponseCreateDto;
import org.example.avisdevolss.dto.ResponseDto;
import org.example.avisdevolss.security.JwtTokenProvider;
import org.example.avisdevolss.service.ResponseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/responses")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ResponseController {

    private final ResponseService responseService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Create a new response to a review
     * @param createDto the response data
     * @param request HTTP request to extract JWT token
     * @return the created response
     */
    @PostMapping
    public ResponseEntity<?> createResponse(@Valid @RequestBody ResponseCreateDto createDto,
                                          HttpServletRequest request) {
        try {
            Integer userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }

            ResponseDto createdResponse = responseService.createResponse(createDto, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdResponse);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to create response: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error creating response: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to create response"));
        }
    }

    /**
     * Get all responses for a specific review
     * @param reviewId the review ID
     * @return list of responses
     */
    @GetMapping("/review/{reviewId}")
    public ResponseEntity<List<ResponseDto>> getResponsesByReview(@PathVariable Integer reviewId) {
        try {
            List<ResponseDto> responses = responseService.getResponsesByReviewId(reviewId);
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error fetching responses for review {}: {}", reviewId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all responses by the authenticated user
     * @param request HTTP request to extract JWT token
     * @return list of user's responses
     */
    @GetMapping("/my-responses")
    public ResponseEntity<?> getMyResponses(HttpServletRequest request) {
        try {
            Integer userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }

            List<ResponseDto> responses = responseService.getResponsesByUserId(userId);
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error fetching user responses: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get a specific response by ID
     * @param id the response ID
     * @return the response if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> getResponse(@PathVariable Integer id) {
        try {
            Optional<ResponseDto> response = responseService.getResponseById(id);
            return response.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error fetching response {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update a response (only by the author)
     * @param id the response ID
     * @param updateDto the updated content
     * @param request HTTP request to extract JWT token
     * @return the updated response
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateResponse(@PathVariable Integer id,
                                          @Valid @RequestBody ResponseCreateDto updateDto,
                                          HttpServletRequest request) {
        try {
            Integer userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }

            ResponseDto updatedResponse = responseService.updateResponse(id, updateDto, userId);
            return ResponseEntity.ok(updatedResponse);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to update response {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error updating response {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to update response"));
        }
    }

    /**
     * Delete a response (only by the author)
     * @param id the response ID
     * @param request HTTP request to extract JWT token
     * @return success or error response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResponse(@PathVariable Integer id, HttpServletRequest request) {
        try {
            Integer userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }

            responseService.deleteResponse(id, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("Failed to delete response {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error deleting response {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to delete response"));
        }
    }

    /**
     * Extract user ID from JWT token in the request
     * @param request the HTTP request
     * @return user ID or null if not authenticated
     */
    private Integer getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                return jwtTokenProvider.getUserIdFromToken(token);
            }
        }
        return null;
    }

    // Error response class
    @Getter
    @Setter
    @AllArgsConstructor
    public static class ErrorResponse {
        private String message;
    }
}
