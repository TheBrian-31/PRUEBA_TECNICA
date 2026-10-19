package com.thebrian31.coworking.service;

import com.thebrian31.coworking.dto.CreateUserRequest;
import com.thebrian31.coworking.dto.LoginRequest;
import com.thebrian31.coworking.dto.LoginResponse;
import com.thebrian31.coworking.dto.UserRegistrationRequest;
import com.thebrian31.coworking.dto.UserResponse;
import com.thebrian31.coworking.entity.Role;
import com.thebrian31.coworking.entity.RoleCode;
import com.thebrian31.coworking.entity.User;
import com.thebrian31.coworking.exception.EmailAlreadyRegisteredException;
import com.thebrian31.coworking.mapper.UserMapper;
import com.thebrian31.coworking.repository.RoleRepository;
import com.thebrian31.coworking.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public UserResponse register(UserRegistrationRequest request) {
        return createAccount(request.fullName(), request.email(), request.password(), RoleCode.USER);
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        return createAccount(request.fullName(), request.email(), request.password(), request.role());
    }

    private UserResponse createAccount(String fullName, String email, String password, RoleCode roleCode) {
        String normalizedEmail = normaliseEmial(email);
        if(userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyRegisteredException(normalizedEmail);
        }

        Role role = roleRepository.findByCode(roleCode)
                .orElseThrow(() -> new IllegalStateException("El rol " + roleCode + " no está cargado"));

        User user = userMapper.toEntity(fullName, normalizedEmail, role, passwordEncoder.encode(password));
        try {
            return userMapper.toResponse(userRepository.saveAndFlush(user));
        } catch (DataIntegrityViolationException exception) {
            throw new EmailAlreadyRegisteredException(normalizedEmail);
        }
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String email = normaliseEmial(request.email());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("La cuenta autenticada no existe"));

        return new LoginResponse(jwtService.generateToken(user), "Bearer", jwtService.expiresInSeconds());
    }

    private String normaliseEmial(String email) {
        return email.trim().toLowerCase();
    }
}
