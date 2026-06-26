package com.amazon.seller.devopsportal.dto.auth;

import com.amazon.seller.devopsportal.entity.UserRole;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        Long userId,
        String name,
        String email,
        UserRole role
) {
}
