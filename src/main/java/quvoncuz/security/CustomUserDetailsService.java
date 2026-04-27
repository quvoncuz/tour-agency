package quvoncuz.security;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.repository.ProfileRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final ProfileRepository profileRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        ProfileEntity profile = profileRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));

        return CustomUserDetails
                .builder()
                .id(profile.getId())
                .username(profile.getUsername())
                .password(profile.getPassword())
                .role(profile.getRole())
                .build();
    }
}
