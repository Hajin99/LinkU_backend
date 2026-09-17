package com.umc.linkyou.users;

import com.umc.linkyou.apiPayload.code.status.ErrorStatus;
import com.umc.linkyou.apiPayload.exception.handler.UserHandler;
import com.umc.linkyou.domain.Users;
import com.umc.linkyou.domain.enums.Provider;
import com.umc.linkyou.domain.enums.UserStatus;
import com.umc.linkyou.repository.authAccountRepository.AuthAccountRepository;
import com.umc.linkyou.service.users.UserServiceImpl;
import com.umc.linkyou.web.dto.UserRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserTest {
    @Mock
    private AuthAccountRepository authAccountRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("탈퇴(INACTIVE) 상태의 유저가 로그인하면 _USER_INACTIVE 예외가 발생")
    public void LoginTest() {
        Users inactiveUser = Users.builder()
                .id(1L)
                .password("1234")
                .status(UserStatus.INACTIVE)
                .build();

        UserRequestDTO.LoginRequestDTO request = new UserRequestDTO.LoginRequestDTO();
        request.setEmail("test@test.com");
        request.setPassword("test");

        when(authAccountRepository.findUserByEmailAndProvider("test@test.com", Provider.GENERAL))
                .thenReturn(Optional.of(inactiveUser));
        when(authAccountRepository.existsByUserIdAndProvider(1L, Provider.GENERAL))
                .thenReturn(true);
        when(passwordEncoder.matches("test", "1234"))
                .thenReturn(true);

        assertThatThrownBy(() -> userService.loginUser(request))
                .isInstanceOf(UserHandler.class)
                .hasFieldOrPropertyWithValue("errorReasonHttpStatus.code", ErrorStatus._USER_INACTIVE.getCode());
    }

}
