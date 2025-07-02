package org.example.avisdevolss.service;

import org.example.avisdevolss.dto.ResponseDto;
import org.example.avisdevolss.dto.ReviewCreateDto;
import org.example.avisdevolss.dto.ReviewFilterDto;
import org.example.avisdevolss.dto.ReviewPublicDto;
import org.example.avisdevolss.dto.ReviewResponseDto;
import org.example.avisdevolss.dto.ReviewUpdateDto;
import org.example.avisdevolss.entity.Account;
import org.example.avisdevolss.entity.Flight;
import org.example.avisdevolss.entity.Review;
import org.example.avisdevolss.entity.ReviewStatus;
import org.example.avisdevolss.repository.AccountRepository;
import org.example.avisdevolss.repository.FlightRepository;
import org.example.avisdevolss.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AccountRepository accountRepository;
    private final FlightRepository flightRepository;
    private final ResponseService responseService;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository,
                        AccountRepository accountRepository,
                        FlightRepository flightRepository,
                        ResponseService responseService) {
        this.reviewRepository = reviewRepository;
        this.accountRepository = accountRepository;
        this.flightRepository = flightRepository;
        this.responseService = responseService;
    }

    public ReviewResponseDto createReview(ReviewCreateDto reviewCreateDto, Integer accountId) {
        Optional<Flight> flight = flightRepository.findById(reviewCreateDto.getFlightId());
        if (flight.isEmpty()) {
            throw new IllegalArgumentException("Flight not found with id: " + reviewCreateDto.getFlightId());
        }

        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isEmpty()) {
            throw new IllegalArgumentException("Account not found with id: " + accountId);
        }

        if (reviewCreateDto.getNotation() < 1 || reviewCreateDto.getNotation() > 5) {
            throw new IllegalArgumentException("Notation must be between 1 and 5");
        }

        Review review = new Review();
        review.setContent(reviewCreateDto.getContent());
        review.setNotation(reviewCreateDto.getNotation());
        review.setAccount(account.get());
        review.setFlight(flight.get());
        review.setStatus(ReviewStatus.TRAITE); // Statut par défaut

        Review savedReview = reviewRepository.save(review);
        return convertToResponseDto(savedReview);
    }

    public void deleteReview(Integer reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new IllegalArgumentException("Review not found with id: " + reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }

    public ReviewResponseDto updateReview(Integer reviewId, ReviewUpdateDto reviewUpdateDto) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isEmpty()) {
            throw new IllegalArgumentException("Review not found with id: " + reviewId);
        }

        if (reviewUpdateDto.getNotation() < 1 || reviewUpdateDto.getNotation() > 5) {
            throw new IllegalArgumentException("Notation must be between 1 and 5");
        }

        Review review = optionalReview.get();
        review.setContent(reviewUpdateDto.getContent());
        review.setNotation(reviewUpdateDto.getNotation());

        Review updatedReview = reviewRepository.save(review);
        return convertToResponseDto(updatedReview);
    }

    public ReviewResponseDto publishReview(Integer reviewId) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isEmpty()) {
            throw new IllegalArgumentException("Review not found with id: " + reviewId);
        }

        Review review = optionalReview.get();
        review.setStatus(ReviewStatus.PUBLIE);

        Review publishedReview = reviewRepository.save(review);
        return convertToResponseDto(publishedReview);
    }

    public ReviewResponseDto rejectReview(Integer reviewId) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isEmpty()) {
            throw new IllegalArgumentException("Review not found with id: " + reviewId);
        }

        Review review = optionalReview.get();
        review.setStatus(ReviewStatus.REJETE);

        Review rejectedReview = reviewRepository.save(review);
        return convertToResponseDto(rejectedReview);
    }

    public List<ReviewResponseDto> findReviews(ReviewFilterDto filterDto) {
        Specification<Review> spec = createSpecification(filterDto);
        List<Review> reviews = reviewRepository.findAll(spec);
        return reviews.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public List<ReviewPublicDto> findReviewsForPublic(ReviewFilterDto filterDto) {
        Specification<Review> spec = createSpecification(filterDto);
        List<Review> reviews = reviewRepository.findAll(spec);
        return reviews.stream()
                .map(this::convertToPublicDto)
                .collect(Collectors.toList());
    }

    public long getCountReview() {
        return reviewRepository.countAllReviews();
    }

    private Specification<Review> createSpecification(ReviewFilterDto filterDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filterDto.getCompany() != null && !filterDto.getCompany().trim().isEmpty()) {
                Join<Review, Flight> flightJoin = root.join("flight");
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(flightJoin.get("company")),
                    "%" + filterDto.getCompany().toLowerCase() + "%"
                ));
            }

            if (filterDto.getAccountId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("account").get("id"), filterDto.getAccountId()));
            }

            if (filterDto.getNotation() != null) {
                if (filterDto.getNotation() < 1 || filterDto.getNotation() > 5) {
                    throw new IllegalArgumentException("Notation must be between 1 and 5");
                }
                predicates.add(criteriaBuilder.equal(root.get("notation"), filterDto.getNotation()));
            }

            if (filterDto.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filterDto.getStatus()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private ReviewResponseDto convertToResponseDto(Review review) {
        ReviewResponseDto dto = new ReviewResponseDto();
        dto.setId(review.getId());
        dto.setContent(review.getContent());
        dto.setNotation(review.getNotation());
        dto.setStatus(review.getStatus());
        dto.setAccountFirstName(review.getAccount().getFirstName());
        dto.setAccountLastName(review.getAccount().getLastName());
        dto.setFlightNumber(review.getFlight().getFlightNumber());
        dto.setCompany(review.getFlight().getCompany());

        // Récupérer les réponses associées à cette review
        List<ResponseDto> responses = responseService.getResponsesByReviewId(review.getId());
        dto.setResponses(responses);

        return dto;
    }

    private ReviewPublicDto convertToPublicDto(Review review) {
        ReviewPublicDto dto = new ReviewPublicDto();
        dto.setId(review.getId());
        dto.setContent(review.getContent());
        dto.setNotation(review.getNotation());
        dto.setStatus(review.getStatus());
        dto.setFlightNumber(review.getFlight().getFlightNumber());
        dto.setCompany(review.getFlight().getCompany());

        // Récupérer les réponses associées à cette review
        List<ResponseDto> responses = responseService.getResponsesByReviewId(review.getId());
        dto.setResponses(responses);

        return dto;
    }
}
