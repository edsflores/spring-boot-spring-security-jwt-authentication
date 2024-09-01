package com.taquigrafia.ai.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.taquigrafia.ai.models.ERole;
import com.taquigrafia.ai.models.Role;
import com.taquigrafia.ai.models.User;
import com.taquigrafia.ai.payload.request.LoginRequest;
import com.taquigrafia.ai.payload.request.SignupRequest;
import com.taquigrafia.ai.payload.response.JwtResponse;
import com.taquigrafia.ai.payload.response.MessageResponse;
import com.taquigrafia.ai.repository.RoleRepository;
import com.taquigrafia.ai.repository.UserRepository;
import com.taquigrafia.ai.security.jwt.JwtUtils;
import com.taquigrafia.ai.security.services.EmailService;
import com.taquigrafia.ai.security.services.UserDetailsImpl;
import com.taquigrafia.ai.utils.MessageUtils;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  private EmailService emailService;

  @Value("${app.activation.url}")
  private String activationUrl;

  @PostMapping("/signin")
  public @Validated @ResponseBody ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

      Authentication authentication =  null;
      try {
        authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
      } catch (Exception e) {
        if(e instanceof org.springframework.security.authentication.BadCredentialsException){
          return MessageUtils.buildErrorMessage("Credenciais Inválidas", MessageUtils.WARNING);
        } else {
          return MessageUtils.buildErrorMessage(e.getMessage(), MessageUtils.ERROR);
        }
      }
       

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);
    
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal(); 
    

    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    return ResponseEntity.ok(new JwtResponse(jwt, 
                         userDetails.getId(), 
                         userDetails.getUsername(), 
                         userDetails.getEmail(), 
                         roles));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Erro: Nome de usuário já está em uso!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Erro: E-mail já está em uso!"));
    }

    // Crie a nova conta de usuário
    User user = new User(signUpRequest.getUsername(), 
               signUpRequest.getEmail(),
               encoder.encode(signUpRequest.getPassword()), 
               UUID.randomUUID().toString(),
               false);

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
        case "admin":
          Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(adminRole);

          break;
        case "mod":
          Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(modRole);

          break;
        default:
          Role userRole = roleRepository.findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(userRole);
        }
      });
    }

    user.setRoles(roles);
    userRepository.save(user);

    String activationLink = activationUrl + "?token=" + user.getActivationToken();
    emailService.sendActivationEmail(user.getEmail(), activationLink);

    return ResponseEntity.ok(new MessageResponse("Usuário registrado com sucesso! Por favor, verifique seu e-mail para ativar a conta."));
  }

  @GetMapping("/activate")
  public ResponseEntity<?> activateAccount(@RequestParam("token") String token) {
    User user = userRepository.findByActivationToken(token)
            .orElseThrow(() -> new RuntimeException("Token de ativação inválido."));

    user.setIsActive(true);
    user.setActivationToken(null);
    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("Conta ativada com sucesso!"));
  }
}
