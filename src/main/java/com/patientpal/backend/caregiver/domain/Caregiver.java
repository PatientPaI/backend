package com.patientpal.backend.caregiver.domain;

import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Gender;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.review.domain.Reviews;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity(name = "caregivers")
@Getter
@DiscriminatorValue("CAREGIVER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Caregiver extends Member {

    private float rating;

    private int reviewCount;

    private String specialization;

    @Lob
    private String caregiverSignificant;

    @OneToMany(mappedBy = "reviewed")
    @Builder.Default
    private List<Reviews> reviews = new ArrayList<>();

    public Caregiver(float rating, String specialization, String caregiverSignificant) {
        this.rating = rating;
        this.specialization = specialization;
        this.caregiverSignificant = caregiverSignificant;
        this.reviews = new ArrayList<>();
    }

    public void updateDetailProfile(final Address address, final float rate, final int experienceYears, final String specialization, final int age, final String caregiverSignificant,
                                    final LocalDateTime wantCareStartDate, final LocalDateTime wantCareEndDate) {
        updateAddress(address);
        updateWantCareStartDate(wantCareStartDate);
        updateWantCareEndDate(wantCareEndDate);
        updateAge(age);
        updateExperienceYears(experienceYears);
        this.rating = rate;
        this.specialization = specialization;
        this.caregiverSignificant = caregiverSignificant;
    }

    public void registerDetailProfile(final String name, final Address address, final String contact, final int age, final Gender gender,
                                      final int experienceYears, final String specialization, final String caregiverSignificant,
                                      final LocalDateTime wantCareStartDate, final LocalDateTime wantCareEndDate, String profileImageUrl) {
        updateName(name);
        updateAddress(address);
        updateContact(contact);
        updateAge(age);
        updateGender(gender);
        updateProfileImage(profileImageUrl);
        updateIsCompleteProfile();
        updateWantCareStartDate(wantCareStartDate);
        updateWantCareEndDate(wantCareEndDate);
        updateExperienceYears(experienceYears);
        this.specialization = specialization;
        this.caregiverSignificant = caregiverSignificant;
    }

    public void addReviewRating(final float newRating) {
        float totalRating = this.rating * this.reviewCount;
        this.reviewCount++;
        this.rating = (totalRating + newRating) / this.reviewCount;
    }

    public void addReview(Reviews review) {
        reviews.add(review);
        review.setReviewed(this);
    }
}
