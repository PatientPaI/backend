package com.patientpal.backend.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.fixtures.member.MemberFixture;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.test.annotation.AutoKoreanDisplayName;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

@AutoKoreanDisplayName
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
class LoginServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private LoginService loginService;

    @Test
    void 유저가_존재하면_성공한다() {
        // given
        Member validMember = MemberFixture.createDefaultMember();

        when(memberRepository.findByUsername(validMember.getUsername())).thenReturn(Optional.of(validMember));

        // when
        UserDetails userDetails = loginService.loadUserByUsername(validMember.getUsername());

        // then
        assertThat(userDetails.getUsername()).isEqualTo(validMember.getUsername());
        assertThat(userDetails.getPassword()).isEqualTo(validMember.getPassword());
    }

    @Test
    void 유저가_존재하지_않으면_예외가_발생한다() {
        // given
        Member invalidMember = MemberFixture.createDefaultMember();

        when(memberRepository.findByUsername(invalidMember.getUsername())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> loginService.loadUserByUsername(invalidMember.getUsername()))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
