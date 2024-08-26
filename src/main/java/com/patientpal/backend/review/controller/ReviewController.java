package com.patientpal.backend.review.controller;

import static org.springframework.http.HttpStatus.CREATED;

import com.patientpal.backend.caregiver.dto.response.CaregiverRankingResponse;
import com.patientpal.backend.review.dto.CreateReviewRequest;
import com.patientpal.backend.review.dto.UpdateReviewRequest;
import com.patientpal.backend.review.dto.ReviewResponse;
import com.patientpal.backend.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 생성", description = "새로운 리뷰를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "리뷰가 성공적으로 생성됨",
            content = @Content(schema = @Schema(implementation = ReviewResponse.class)))
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@RequestBody CreateReviewRequest createReviewRequest,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        ReviewResponse reviewResponse = reviewService.createReview(createReviewRequest, userDetails.getUsername());
        return ResponseEntity.status(CREATED).body(reviewResponse);
    }

    @Operation(summary = "리뷰 조회", description = "특정 리뷰를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 조회 성공",
            content = @Content(schema = @Schema(implementation = ReviewResponse.class)))
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable Long id) {
        ReviewResponse reviewResponse = reviewService.getReview(id);
        return ResponseEntity.ok(reviewResponse);
    }

    @Operation(summary = "전체 리뷰 조회", description = "모든 리뷰를 페이징 처리하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 조회 성공",
            content = @Content(schema = @Schema(implementation = ReviewResponse.class)))
    @GetMapping
    public ResponseEntity<Page<ReviewResponse>> getAllReviews(Pageable pageable) {
        Page<ReviewResponse> reviews = reviewService.getAllReviews(pageable);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "내가 작성한 리뷰 조회", description = "현재 로그인한 사용자가 작성한 모든 리뷰를 페이징 처리하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 조회 성공",
            content = @Content(schema = @Schema(implementation = ReviewResponse.class)))
    @GetMapping("/written")
    public ResponseEntity<Page<ReviewResponse>> getReviewsWrittenByUser(@AuthenticationPrincipal UserDetails userDetails, Pageable pageable) {
        Page<ReviewResponse> reviews = reviewService.getReviewsWrittenByUser(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "내가 받은 리뷰 조회", description = "현재 로그인한 사용자가 받은 모든 리뷰를 페이징 처리하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 조회 성공",
            content = @Content(schema = @Schema(implementation = ReviewResponse.class)))
    @GetMapping("/received")
    public ResponseEntity<Page<ReviewResponse>> getReviewsReceivedByUser(@AuthenticationPrincipal UserDetails userDetails, Pageable pageable) {
        Page<ReviewResponse> reviews = reviewService.getReviewsReceivedByUser(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "리뷰 수정", description = "기존 리뷰를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 수정 성공",
            content = @Content(schema = @Schema(implementation = ReviewResponse.class)))
    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable Long id,
                                                       @RequestBody UpdateReviewRequest updateReviewRequest,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        ReviewResponse reviewResponse = reviewService.updateReview(id, updateReviewRequest, userDetails.getUsername());
        return ResponseEntity.ok(reviewResponse);
    }


    @Operation(summary = "리뷰 삭제", description = "기존 리뷰를 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 삭제 성공")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        reviewService.deleteReview(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/top-caregivers")
    @Operation(summary = "상위 간병인 조회", description = "지역별로 상위 10명의 간병인을 조회한다.")
    @ApiResponse(responseCode = "200", description = "상위 간병인 조회 성공",
            content = @Content(schema = @Schema(implementation = CaregiverRankingResponse.class)))
    public ResponseEntity<List<CaregiverRankingResponse>> getTopCaregiversByRating(@RequestParam String region) {
        List<CaregiverRankingResponse> response = reviewService.getTopCaregiversByRating(region);
        return ResponseEntity.ok(response);
    }
}
