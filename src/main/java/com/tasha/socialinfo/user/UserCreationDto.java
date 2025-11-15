package com.tasha.socialinfo.user;

import com.tasha.socialinfo.security.Role;

public record UserCreationDto(
        String login,
        String name,
        String password,
        String confirmPassword,
        Role role
) {
}
