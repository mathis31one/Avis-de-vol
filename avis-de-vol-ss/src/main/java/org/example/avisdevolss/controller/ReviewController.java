package org.example.avisdevolss.controller;

import org.example.avisdevolss.dto.ReviewCreateDto;
import org.example.avisdevolss.dto.ReviewFilterDto;
import org.example.avisdevolss.dto.ReviewPublicDto;
import org.example.avisdevolss.dto.ReviewResponseDto;
import org.example.avisdevolss.dto.ReviewUpdateDto;
import org.example.avisdevolss.entity.Account;
import org.example.avisdevolss.entity.Role;
import org.example.avisdevolss.entity.ReviewStatus;
import org.example.avisdevolss.repository.AccountRepository;
import org.example.avisdevolss.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewService reviewService;
    private final AccountRepository accountRepository;

    @Autowired
    public ReviewController(ReviewService reviewService, AccountRepository accountRepository) {
        this.reviewService = reviewService;
        this.accountRepository = accountRepository;
    }

    /**
     * Create new review
     * User id is retrieved from the request attribute set by the authentication filter.
     */
    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(@Valid @RequestBody ReviewCreateDto reviewCreateDto,
                                                         HttpServletRequest request) {
        try {
            // get user ID by Auth
            Integer userId = (Integer) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            ReviewResponseDto createdReview = reviewService.createReview(reviewCreateDto, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update an existing review
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> updateReview(@PathVariable Integer id,
                                                         @Valid @RequestBody ReviewUpdateDto reviewUpdateDto) {
        try {
            ReviewResponseDto updatedReview = reviewService.updateReview(id, reviewUpdateDto);
            return ResponseEntity.ok(updatedReview);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a review by its ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Integer id) {
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Pubish a review (change status to PUBLIE)
     */
    @PutMapping("/{id}/publish")
    public ResponseEntity<ReviewResponseDto> publishReview(@PathVariable Integer id) {
        try {
            ReviewResponseDto publishedReview = reviewService.publishReview(id);
            return ResponseEntity.ok(publishedReview);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Reject a review (change status to REJETEE)
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<ReviewResponseDto> rejectReview(@PathVariable Integer id) {
        try {
            ReviewResponseDto rejectedReview = reviewService.rejectReview(id);
            return ResponseEntity.ok(rejectedReview);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Search for reviews
     * All parameters are optional:
     * only admin can see all reviews with account information
     */
    @GetMapping
    public ResponseEntity<?> findReviews(
            @RequestParam(required = false) String company,
            @RequestParam(required = false) Integer accountId,
            @RequestParam(required = false) Integer notation,
            @RequestParam(required = false) ReviewStatus status,
            HttpServletRequest request) {

        try {
            ReviewFilterDto filterDto = new ReviewFilterDto();
            filterDto.setCompany(company);
            filterDto.setAccountId(accountId);
            filterDto.setNotation(notation);
            filterDto.setStatus(status);

            // Vérifier si l'utilisateur est admin
            boolean isAdmin = isUserAdmin(request);

            if (isAdmin) {
                // Admin : retourner les informations complètes avec les données de compte
                List<ReviewResponseDto> reviews = reviewService.findReviews(filterDto);
                return ResponseEntity.ok(reviews);
            } else {
                // Utilisateur normal : retourner les avis sans informations de compte
                List<ReviewPublicDto> reviews = reviewService.findReviewsForPublic(filterDto);
                return ResponseEntity.ok(reviews);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Check if the user is an admin
     */
    private boolean isUserAdmin(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return false; // Utilisateur non connecté
        }

        Optional<Account> account = accountRepository.findById(userId);
        return account.isPresent() && account.get().getRole() == Role.ADMIN;
    }

    /**
     * Get a review by its ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> getReviewById(@PathVariable Integer id) {
        try {
            // Cette méthode n'existe pas encore dans le service, on peut l'ajouter si nécessaire
            ReviewFilterDto filterDto = new ReviewFilterDto();
            List<ReviewResponseDto> reviews = reviewService.findReviews(filterDto);

            ReviewResponseDto review = reviews.stream()
                    .filter(r -> r.getId().equals(id))
                    .findFirst()
                    .orElse(null);

            if (review != null) {
                return ResponseEntity.ok(review);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all reviews
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getCountReview() {
        try {
            long count = reviewService.getCountReview();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
