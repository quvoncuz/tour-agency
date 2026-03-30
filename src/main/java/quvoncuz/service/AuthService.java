package quvoncuz.service;

import quvoncuz.dto.auth.AuthResponse;
import quvoncuz.dto.auth.LoginRequestDTO;
import quvoncuz.dto.ProfileDTO;
import quvoncuz.dto.auth.RegistrationRequestDTO;


public interface AuthService {
    public AuthResponse register(RegistrationRequestDTO dto);
    public AuthResponse login(LoginRequestDTO dto);
//    public Boolean verification(VerificationRequestDTO dto);
}
