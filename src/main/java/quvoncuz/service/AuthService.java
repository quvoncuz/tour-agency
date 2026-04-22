package quvoncuz.service;

import quvoncuz.dto.auth.AuthResponse;
import quvoncuz.dto.auth.LoginRequestDTO;
import quvoncuz.dto.auth.RegistrationRequestDTO;


public interface AuthService {
    AuthResponse register(RegistrationRequestDTO dto);

    AuthResponse login(LoginRequestDTO dto);
//    public Boolean verification(VerificationRequestDTO dto);
}
