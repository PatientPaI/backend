package com.patientpal.backend.recommend;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.caregiver.dto.response.CaregiverProfileResponse;
import com.patientpal.backend.caregiver.repository.CaregiverRepository;
import com.patientpal.backend.common.exception.BusinessException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.patient.domain.Patient;
import com.patientpal.backend.patient.dto.response.PatientProfileResponse;
import com.patientpal.backend.patient.repository.PatientRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RecommendService {

    private final CaregiverRepository caregiverRepository;
    private final PatientRepository patientRepository;

    @Transactional(readOnly = true)
    public List<CaregiverProfileResponse> getTopCaregiversByRecommend(String username) {
        Patient findPatient = patientRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PATIENT_NOT_EXIST));
        if (findPatient.getAddress() == null) {
            throw new BusinessException(ErrorCode.PROFILE_NOT_COMPLETED);
        }
        List<Caregiver> caregivers = caregiverRepository.findTop5ByAddressOrderByRatingDescViewCountsDesc(findPatient.getAddress().getAddr());

        return caregivers.stream()
                .map(caregiver -> new CaregiverProfileResponse(caregiver.getId(), caregiver.getName(), caregiver.getAge(), caregiver.getGender(),
                        caregiver.getAddress(), caregiver.getRating(), caregiver.getExperienceYears(), caregiver.getSpecialization(), caregiver.getProfileImageUrl(), caregiver.getViewCounts()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PatientProfileResponse> getTopPatientsByRecommend(String username) {
        Caregiver findCaregiver = caregiverRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CAREGIVER_NOT_EXIST));
        if (findCaregiver.getAddress() == null) {
            throw new BusinessException(ErrorCode.PROFILE_NOT_COMPLETED);
        }
        List<Patient> patients = patientRepository.findTop5ByAddressOrderByViewCountsDesc(findCaregiver.getAddress().getAddr());

        return patients.stream()
                .map(patient -> new PatientProfileResponse(patient.getId(), patient.getName(), patient.getAge(), patient.getGender(),
                        patient.getAddress(), patient.getProfileImageUrl(), patient.getViewCounts()))
                .collect(Collectors.toList());
    }

}
