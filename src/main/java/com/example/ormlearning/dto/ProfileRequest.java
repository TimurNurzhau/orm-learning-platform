package com.example.ormlearning.dto;

import jakarta.validation.constraints.Size;

public record ProfileRequest(
        @Size(max = 2000) String bio,
        @Size(max = 500) String avatarUrl,
        @Size(max = 255) String phone,
        @Size(max = 255) String location
) {}