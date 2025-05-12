package org.sunday.projectpop.dto;

public record ProfileResponse(
        UserProfileResponseDTO profile,
        UserTagCacheDTO tags
) {}