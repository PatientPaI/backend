package com.patientpal.backend.caregiver.repository;

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
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

@Slf4j
public class CaregiverRepositoryImpl implements CaregiverProfileSearchRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CaregiverRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    //TODO 정렬 방식 추후 추가
    //가까운 순(default)
    //후기 많은 순
    //최신 순

    @Override
    public Page<PatientProfileResponse> searchPatientProfilesOrderBy(ProfileSearchCondition condition, Pageable pageable) {
        log.info("Search Condition Name={}, gender={}, firstAddress={}, secondAddress={}",
                condition.getName(), condition.getGender(), condition.getFirstAddress(), condition.getSecondAddress());

        List<PatientProfileResponse> content = queryFactory
                .select(new QPatientProfileResponse(
                        patient.name,
                        patient.age,
                        patient.gender,
                        patient.address,
                        patient.profileImageUrl))
                .from(patient)
                .where(nameEq(condition.getName()),
                        genderEq(condition.getGender()),
                        firstAddressEq(condition.getFirstAddress()),
                        secondAddressEq(condition.getSecondAddress()))
                .orderBy(getOrderSpecifier(pageable.getSort()).stream().toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(patient.count())
                .from(patient)
                .where(nameEq(condition.getName()));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
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
        return gender == null ? null : patient.gender.eq(gender);
    }

    private BooleanExpression firstAddressEq(String firstAddress) {
        return firstAddress == null ? null : patient.address.addr.contains(firstAddress);
    }

    private BooleanExpression secondAddressEq(String secondAddress) {
        return secondAddress == null ? null : patient.address.addr.contains(secondAddress);
    }

    private BooleanExpression nameEq(String name) {
        return StringUtils.isEmpty(name) ? null : patient.name.eq(name);
    }
}
