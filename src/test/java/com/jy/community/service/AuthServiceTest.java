package com.jy.community.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jy.community.domain.user.User;
import com.jy.community.domain.user.UserRepository;
import com.jy.community.dto.JoinDto;
import com.jy.community.dto.LoginDto;
import com.jy.community.type.Role;
import com.jy.community.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;


class AuthServiceTest {

  @InjectMocks
  private AuthService authService;
  @Mock
  private AuthenticationManager authenticationManager;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private UserRepository userRepository;
  @Mock
  private UserDetailsService userDetailsService;
  @Mock
  private JwtUtil jwtUtil;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("회원가입")
  public void testRegister() {
    JoinDto joinDto = new JoinDto();
    joinDto.setUsername("testuser");
    joinDto.setPassword("password");
    joinDto.setNickname("testnickname");

    User user = User.builder()
        .username("testuser")
        .password("encodedPassword")
        .nickname("testnickname")
        .role(Role.USER)
        .build();

    when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class))).thenReturn(user);

    String result = authService.register(joinDto);

    assertEquals("Register Success", result);
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  @DisplayName("중복 아이디 회원가입")
  public void testRegisterDuplicateUsername() {
    JoinDto joinDto = new JoinDto();
    joinDto.setUsername("duplicateuser");
    joinDto.setPassword("password");
    joinDto.setNickname("testnickname");

    when(userRepository.findByUsername(anyString())).thenReturn(new User());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      authService.register(joinDto);
    });

    assertEquals("이미 존재하는 아이디입니다.", exception.getMessage());
  }

  @Test
  @DisplayName("중복 닉네임 회원가입")
  public void testRegisterDuplicateNickname() {
    JoinDto joinDto = new JoinDto();
    joinDto.setUsername("testuser");
    joinDto.setPassword("password");
    joinDto.setNickname("duplicatenickname");

    when(userRepository.findByNickname(anyString())).thenReturn(new User());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      authService.register(joinDto);
    });

    assertEquals("이미 존재하는 닉네임입니다.", exception.getMessage());
  }

  @Test
  @DisplayName("토큰 생성")
  public void testCreateAuthenticationToken() throws Exception {
    LoginDto loginDto = new LoginDto();
    loginDto.setUsername("testuser");
    loginDto.setPassword("password");

    UserDetails userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("testuser");

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(new UsernamePasswordAuthenticationToken("testuser", "password"));
    when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
    when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("token");

    String result = authService.createAuthenticationToken(loginDto);

    assertNotNull(result);
    assertTrue(result.startsWith("Bearer "));
    verify(authenticationManager, times(1)).authenticate(
        any(UsernamePasswordAuthenticationToken.class));
    verify(userDetailsService, times(1)).loadUserByUsername(anyString());
  }

  @Test
  @DisplayName("토큰 생성 실패")
  public void testCreateAuthenticationToken_UserNotFound() {
    LoginDto loginDto = new LoginDto();
    loginDto.setUsername("testuser");
    loginDto.setPassword("password");

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(new UsernamePasswordAuthenticationToken("testuser", "password"));
    when(userDetailsService.loadUserByUsername(anyString())).thenReturn(null);

    Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
      authService.createAuthenticationToken(loginDto);
    });

    assertEquals("사용자를 찾을 수 없습니다: testuser", exception.getMessage());
  }
}