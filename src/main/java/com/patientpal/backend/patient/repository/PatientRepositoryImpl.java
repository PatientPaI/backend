package com.patientpal.backend.patient.repository;

import static com.patientpal.backend.caregiver.domain.QCaregiver.caregiver;
import static com.patientpal.backend.member.domain.QMember.member;
import static com.querydsl.core.types.Order.ASC;
import static com.querydsl.core.types.Order.DESC;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.caregiver.dto.response.CaregiverProfileResponse;
import com.patientpal.backend.caregiver.dto.response.QCaregiverProfileResponse;
import com.patientpal.backend.member.domain.Gender;
import com.patientpal.backend.common.querydsl.ProfileSearchCondition;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

@Slf4j
public class PatientRepositoryImpl implements PatientProfileSearchRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PatientRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // TODO
    // 검색 - 지역, 이름, 나이, 성별
    // 정렬 - 최신순(default), 후기순, 인기순(조회순), 별점순
    @Override
    public Slice<CaregiverProfileResponse> searchPageOrderByDefault(ProfileSearchCondition condition, Long lastIndex, LocalDateTime lastProfilePublicTime, Pageable pageable) {
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
                        cursorProfilePublicTimeAndCaregiverId(lastProfilePublicTime, lastIndex)
                )
                .orderBy(member.profilePublicTime.desc(), member.id.asc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<CaregiverProfileResponse> content = queryFactory
                .select(new QCaregiverProfileResponse(
                        caregiver.id,
                        caregiver.name,
                        caregiver.age,
                        caregiver.gender,
                        caregiver.address,
                        caregiver.rating,
                        caregiver.experienceYears,
                        caregiver.specialization,
                        caregiver.profileImageUrl,
                        caregiver.viewCounts))
                .from(caregiver)
                .where(caregiver.id.in(memberIds))
                .orderBy(caregiver.profilePublicTime.desc(), caregiver.id.asc())
                .fetch();
        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private Predicate cursorProfilePublicTimeAndCaregiverId(LocalDateTime lastProfilePublicTime,
                                                            Long lastIndex) {
        if (lastProfilePublicTime == null || lastIndex == null) {
            return null;
        }

        return member.profilePublicTime.eq(lastProfilePublicTime)
                .and(member.id.gt(lastIndex))
                .or(member.profilePublicTime.lt(lastProfilePublicTime));
    }

    @Override
    public Slice<CaregiverProfileResponse> searchCaregiverProfilesByViewCounts(ProfileSearchCondition condition,
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
                        cursorViewCountsAndCaregiverId(lastViewCounts, lastIndex)
                )
                .orderBy(member.viewCounts.desc(), member.id.asc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<CaregiverProfileResponse> content = queryFactory
                .select(new QCaregiverProfileResponse(
                        caregiver.id,
                        caregiver.name,
                        caregiver.age,
                        caregiver.gender,
                        caregiver.address,
                        caregiver.rating,
                        caregiver.experienceYears,
                        caregiver.specialization,
                        caregiver.profileImageUrl,
                        caregiver.viewCounts))
                .from(caregiver)
                .where(caregiver.id.in(memberIds))
                .orderBy(caregiver.viewCounts.desc(), caregiver.id.asc())
                .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    // @Override
    // public Slice<CaregiverProfileResponse> searchCaregiverProfilesByReviewCounts(ProfileSearchCondition condition,
    //                                                                              Long lastIndex, Integer lastReviewCounts,
    //                                                                              Pageable pageable) {
    //     List<CaregiverProfileResponse> content = queryFactory
    //             .select(new QCaregiverProfileResponse(
    //                     caregiver.id,
    //                     caregiver.name,
    //                     caregiver.age,
    //                     caregiver.gender,
    //                     caregiver.address,
    //                     caregiver.rating,
    //                     caregiver.experienceYears,
    //                     caregiver.specialization,
    //                     caregiver.profileImageUrl,
    //                     caregiver.viewCounts))
    //             .from(caregiver)
    //             .where(
    //                     cursorReviewCountsAndCaregiverId(lastReviewCounts, lastIndex),
    //                     nameEq(condition.getName()),
    //                     addressEq(condition.getAddr()),
    //                     genderEq(condition.getGender()),
    //                     experienceYearsGoe(condition.getExperienceYearsGoe()),
    //                     caregiver.isProfilePublic
    //             )
    //             .orderBy(caregiver.reviews.size().desc(), caregiver.id.desc())
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
                        "function('match',{0},{1},{2})", caregiver.address.addr, caregiver.name, word)
                .gt(0);
        BooleanExpression caregiverMatch = Expressions.numberTemplate(Double.class,
                        "function('match',{0},{1},{2})", caregiver.specialization, caregiver.caregiverSignificant, word)
                .gt(0);
        return memberMatch.or(caregiverMatch);
    }
    private BooleanExpression cursorViewCountsAndCaregiverId(Integer lastViewCounts, Long lastIndex) {
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
            PathBuilder orderByExpression = new PathBuilder(Caregiver.class, "caregiver");
            log.info("orderByExpression:{}", orderByExpression);
            list.add(new OrderSpecifier(direction, orderByExpression.get(property)));
        });
        return list;
    }

    private BooleanExpression genderEq(Gender gender) {
        return gender == null ? null : member.gender.eq(gender);
    }

    // private BooleanExpression experienceYearsGoe(Integer experienceYears) {
    //     return experienceYears == null ? null : caregiver.experienceYears.goe(experienceYears);
    // }

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
