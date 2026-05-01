package quvoncuz.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import quvoncuz.security.CustomUserDetails;

public class SecurityUtil {

    public static long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails != null ? userDetails.getId() : 0;
    }

}
