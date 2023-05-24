package com.osce.eprocurementmonitorbackend.service.impl;

import com.osce.eprocurementmonitorbackend.security.services.UserDetailsImpl;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityAuditorAware implements AuditorAware<String> {

    private static final String SYSTEM = "System";

    @Override
    public Optional<String> getCurrentAuditor() {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user != null) {
            if (user instanceof String) return Optional.of((String) user); //anonymousUser
            UserDetailsImpl userDetails = (UserDetailsImpl) user;
            return Optional.of(userDetails.getName());
        }
        return Optional.of(SYSTEM);
    }
}