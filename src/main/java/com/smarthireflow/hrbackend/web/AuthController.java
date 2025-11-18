package com.smarthireflow.hrbackend.web;

import com.smarthireflow.hrbackend.security.JwtService;
import com.smarthireflow.hrbackend.user.entity.UserEntity;
import com.smarthireflow.hrbackend.user.service.UserService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final UserService userService;
  private final PasswordEncoder encoder;
  private final JwtService jwt;

  public AuthController(UserService userService, PasswordEncoder encoder, JwtService jwt) {
    this.userService = userService;
    this.encoder = encoder;
    this.jwt = jwt;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginReq req) {
    UserEntity user = userService.findByEmail(req.email()).orElse(null);
    if (user == null || !encoder.matches(req.password(), user.getPasswordHash())) {
      return ResponseEntity.status(401).body(new Msg("Invalid credentials"));
    }
    String token = jwt.generate(user.getEmail(), user.getRole());
    return ResponseEntity.ok(new LoginRes(token, user.getFullName(), user.getRole().name()));
  }

  @GetMapping("/me")
  public ResponseEntity<?> me(Authentication auth) {
    if (auth == null) return ResponseEntity.ok(new Me(false, null, null));
    UserEntity user = userService.findByEmail(auth.getName()).orElse(null);
    if (user == null) return ResponseEntity.ok(new Me(false, null, null));
    return ResponseEntity.ok(new Me(true, user.getFullName(), user.getRole().name()));
  }

  public record LoginReq(@NotBlank String email, @NotBlank String password) {}
  public record LoginRes(String token, String name, String role) {}
  public record Me(boolean authenticated, String name, String role) {}
  public record Msg(String message) {}
}
