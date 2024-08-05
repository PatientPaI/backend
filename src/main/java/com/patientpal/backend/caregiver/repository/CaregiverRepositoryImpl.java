package com.patientpal.backend.caregiver.repository;

import static com.patientpal.backend.member.domain.QMember.member;
import static com.patientpal.backend.patient.domain.QPatient.patient;
import static com.querydsl.core.types.Order.ASC;
import static com.querydsl.core.types.Order.DESC;

import com.patientpal.backend.member.domain.Gender;
import com.patientpal.backend.patient.domain.Patient;
import com.patientpal.backend.patient.dto.response.PatientProfileResponse;
import com.patientpal.backend.patient.dto.response.QPatientProfileResponse;
import com.patientpal.backend.common.querydsl.ProfileSearchCondition;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

@Slf4j
public class CaregiverRepositoryImpl implements CaregiverProfileSearchRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CaregiverRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Slice<PatientProfileResponse> searchPageOrderByDefault(ProfileSearchCondition condition, Long lastIndex, LocalDateTime lastProfilePublicTime, Pageable pageable) {
        log.info("Search Condition Name={}, age={}, gender={}, address={}",
                condition.getName(), condition.getAgeLoe(),
                condition.getGender(), condition.getAddr());
        log.info("lastIndex={}", lastIndex);

        List<Long> memberIds = queryFactory
                .select(member.id)
                .from(member)
                .where(
                        addressEq(condition.getAddr()),
                        nameEq(condition.getName()),
                        genderEq(condition.getGender()),
                        ageLoe(condition.getAgeLoe()),
                        member.isProfilePublic,
                        cursorProfilePublicTimeAndPatientId(lastProfilePublicTime, lastIndex)
                )
                .orderBy(member.profilePublicTime.desc(), member.id.asc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<PatientProfileResponse> content = queryFactory
                .select(new QPatientProfileResponse(
                        patient.id,
                        patient.name,
                        patient.age,
                        patient.gender,
                        patient.address,
                        patient.profileImageUrl,
                        patient.viewCounts))
                .from(patient)
                .where(patient.id.in(memberIds))
                .orderBy(patient.profilePublicTime.desc(), patient.id.asc())
                .fetch();
        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private Predicate cursorProfilePublicTimeAndPatientId(LocalDateTime lastProfilePublicTime,
                                                            Long lastIndex) {
        if (lastProfilePublicTime == null || lastIndex == null) {
            return null;
        }

        return member.profilePublicTime.eq(lastProfilePublicTime)
                .and(member.id.gt(lastIndex))
                .or(member.profilePublicTime.lt(lastProfilePublicTime));
    }

    @Override
    public Slice<PatientProfileResponse> searchPatientProfilesByViewCounts(ProfileSearchCondition condition,
                                                                               Long lastIndex, Integer lastViewCounts, Pageable pageable) {
        log.info("Search Condition Name={}, age={}, gender={}, address={}",
                condition.getName(), condition.getAgeLoe(),
                condition.getGender(), condition.getAddr());
        log.info("lastIndex={}, lastViewCounts={}", lastIndex, lastViewCounts);

        List<Long> memberIds = queryFactory
                .select(member.id)
                .from(member)
                .where(
                        addressEq(condition.getAddr()),
                        nameEq(condition.getName()),
                        genderEq(condition.getGender()),
                        ageLoe(condition.getAgeLoe()),
                        member.isProfilePublic,
                        cursorViewCountsAndPatientId(lastViewCounts, lastIndex)
                )
                .orderBy(member.viewCounts.desc(), member.id.asc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<PatientProfileResponse> content = queryFactory
                .select(new QPatientProfileResponse(
                        patient.id,
                        patient.name,
                        patient.age,
                        patient.gender,
                        patient.address,
                        patient.profileImageUrl,
                        patient.viewCounts))
                .from(patient)
                .where(patient.id.in(memberIds))
                .orderBy(patient.viewCounts.desc(), patient.id.asc())
                .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    // @Override
    // public Slice<patientProfileResponse> searchPatientProfilesByReviewCounts(ProfileSearchCondition condition,
    //                                                                              Long lastIndex, Integer lastReviewCounts,
    //                                                                              Pageable pageable) {
    //     List<patientProfileResponse> content = queryFactory
    //             .select(new QPatientProfileResponse(
    //                     patient.id,
    //                     patient.name,
    //                     patient.age,
    //                     patient.gender,
    //                     patient.address,
    //                     patient.rating,
    //                     patient.experienceYears,
    //                     patient.specialization,
    //                     patient.profileImageUrl,
    //                     patient.viewCounts))
    //             .from(patient)
    //             .where(
    //                     cursorReviewCountsAndPatientId(lastReviewCounts, lastIndex),
    //                     nameEq(condition.getName()),
    //                     addressEq(condition.getAddr()),
    //                     genderEq(condition.getGender()),
    //                     experienceYearsGoe(condition.getExperienceYearsGoe()),
    //                     patient.isProfilePublic
    //             )
    //             .orderBy(patient.reviews.size().desc(), patient.id.desc())
    //             .limit(pageable.getPageSize() + 1) // 다음 페이지가 있는지 확인하기 위해 하나 더 가져옴
    //             .fetch();
    //     boolean hasNext = content.size() > pageable.getPageSize();
    //
    //     return new SliceImpl<>(content, pageable, hasNext);
    // }

    public BooleanExpression keywordSearch(String word) {
        if (word == null || word.isEmpty()) {
            return null;
        }
        BooleanExpression memberMatch = Expressions.numberTemplate(Double.class,
                        "function('match',{0},{1},{2})", patient.address.addr, patient.name, word)
                .gt(0);
        BooleanExpression patientMatch = Expressions.numberTemplate(Double.class,
                        "function('match',{0},{1},{2})", patient.careRequirements, patient.patientSignificant, word)
                .gt(0);
        return memberMatch.or(patientMatch);
    }


    private BooleanExpression cursorViewCountsAndPatientId(Integer lastViewCounts, Long lastIndex) {
        if (lastViewCounts == null || lastIndex == null) {
            return null;
        }

        return member.viewCounts.eq(lastViewCounts)
                .and(member.id.gt(lastIndex))
                .or(member.viewCounts.lt(lastViewCounts));
    }


    private List<OrderSpecifier> getOrderSpecifier(Sort sort) {
        List<OrderSpecifier> list = new ArrayList<>();
        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? ASC : DESC;
            log.info("order:{}", order);
            log.info("direction:{}", direction);
            String property = order.getProperty();
            log.info("property:{}", property);
            PathBuilder orderByExpression = new PathBuilder(Patient.class, "patient");
            log.info("orderByExpression:{}", orderByExpression);
            list.add(new OrderSpecifier(direction, orderByExpression.get(property)));
        });
        return list;
    }

    private BooleanExpression genderEq(Gender gender) {
        return gender == null ? null : member.gender.eq(gender);
    }

    private BooleanExpression ageLoe(final Integer ageLoe) {
        return ageLoe == null ? null : member.age.loe(ageLoe);
    }

    private BooleanExpression addressEq(String address) {
        return address == null ? null : member.address.addr.like(address + "%");
    }

    private BooleanExpression nameEq(String name) {
        return StringUtils.isEmpty(name) ? null : member.name.like(name + "%");
    }
}
