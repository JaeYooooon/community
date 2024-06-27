package com.jy.community.service;

import com.jy.community.domain.user.User;
import com.jy.community.domain.user.UserRepository;
import com.jy.community.dto.JoinDto;
import com.jy.community.dto.LoginDto;
import com.jy.community.type.Role;
import com.jy.community.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final UserDetailsService userDetailsService;
  private final JwtUtil jwtUtil;

  @Autowired
  public AuthService(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder,
      UserRepository userRepository,
      UserDetailsService userDetailsService, JwtUtil jwtUtil) {
    this.authenticationManager = authenticationManager;
    this.passwordEncoder = passwordEncoder;
    this.userRepository = userRepository;
    this.userDetailsService = userDetailsService;
    this.jwtUtil = jwtUtil;
  }

  public String register(JoinDto joinDto) {
    if (userRepository.findByUsername(joinDto.getUsername()) != null) {
      throw new RuntimeException("이미 존재하는 아이디입니다.");
    }
    if (userRepository.findByNickname(joinDto.getNickname()) != null) {
      throw new RuntimeException("이미 존재하는 닉네임입니다.");
    }

    User user = User.builder()
        .username(joinDto.getUsername())
        .password(passwordEncoder.encode(joinDto.getPassword()))
        .nickname(joinDto.getNickname())
        .role(Role.USER)
        .build();

    userRepository.save(user);
    return "Register Success";
  }

  public String createAuthenticationToken(LoginDto loginDto) throws Exception {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
      );
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }

    final UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getUsername());
    if (userDetails == null) {
      throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + loginDto.getUsername());
    }
    return "Bearer " + jwtUtil.generateToken(userDetails);
  }
}
