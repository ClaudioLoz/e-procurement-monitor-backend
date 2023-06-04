package com.osce.eprocurementmonitorbackend.service.impl;

import com.osce.eprocurementmonitorbackend.security.services.UserDetailsImpl;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityAuditorAware implements AuditorAware<String> {

    private static final String SYSTEM = "System";

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) principal;
                return Optional.of(userDetails.getName());
            }
            if (principal instanceof String) {
                return Optional.of((String) principal);
            }
        }
        return Optional.of(SYSTEM);
    }
}