package com.jy.community.controller;

import com.jy.community.dto.JoinDto;
import com.jy.community.dto.LoginDto;
import com.jy.community.service.AuthService;
import com.jy.community.service.CustomUserDetailsService;
import com.jy.community.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

  private final AuthService authService;
  private AuthenticationManager authenticationManager;
  private CustomUserDetailsService userDetailsService;
  private JwtUtil jwtUtil;

  @Autowired
  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public String createAuthenticationToken(@RequestBody LoginDto loginDto) throws Exception {
    return authService.createAuthenticationToken(loginDto);
  }

  @PostMapping("/register")
  public String register(@RequestBody JoinDto joinDto) {
    return authService.register(joinDto);
  }
}