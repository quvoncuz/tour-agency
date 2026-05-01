package quvoncuz.security;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.repository.ProfileRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final ProfileRepository profileRepository;

    @Override
    public CustomUserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        ProfileEntity profile = profileRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return CustomUserDetails
                .builder()
                .id(profile.getId())
                .profile(profile)
                .username(profile.getUsername())
                .password(profile.getPassword())
                .role(profile.getRole())
                .build();
    }
}
