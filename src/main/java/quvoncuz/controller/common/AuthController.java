package quvoncuz.controller.common;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import quvoncuz.dto.ApiResponse;
import quvoncuz.dto.auth.AuthResponse;
import quvoncuz.dto.auth.LoginRequestDTO;
import quvoncuz.dto.auth.RegistrationRequestDTO;
import quvoncuz.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PreAuthorize("permitAll()")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegistrationRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(authService.register(dto)));
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(dto)));
    }
}
