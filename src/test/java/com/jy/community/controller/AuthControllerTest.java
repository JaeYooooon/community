package com.jy.community.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jy.community.dto.JoinDto;
import com.jy.community.dto.LoginDto;
import com.jy.community.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AuthControllerTest {

  @InjectMocks
  private AuthController authController;

  @Mock
  private AuthService authService;

  private MockMvc mockMvc;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
  }

  @Test
  @DisplayName("회원가입 엔드포인트 테스트: 사용자가 올바른 데이터를 제공할 때 회원가입 요청이 성공하는지 확인합니다.")
  public void testRegister() throws Exception {
    JoinDto joinDto = new JoinDto();
    joinDto.setUsername("testuser");
    joinDto.setPassword("password");
    joinDto.setNickname("testnickname");

    when(authService.register(any(JoinDto.class))).thenReturn("Register Success");

    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(joinDto)))
        .andExpect(status().isOk())
        .andExpect(content().string("Register Success"));
  }

  @Test
  @DisplayName("로그인 엔드포인트 테스트: 사용자가 올바른 자격 증명을 제공할 때 로그인 요청이 성공하고 JWT 토큰이 반환되는지 확인합니다.")
  public void testCreateAuthenticationToken() throws Exception {
    LoginDto loginDto = new LoginDto();
    loginDto.setUsername("testuser");
    loginDto.setPassword("password");

    when(authService.createAuthenticationToken(any(LoginDto.class))).thenReturn("Bearer token");

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(loginDto)))
        .andExpect(status().isOk())
        .andExpect(content().string("Bearer token"));
  }
}