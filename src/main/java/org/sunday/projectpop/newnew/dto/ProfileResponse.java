package org.sunday.projectpop.newnew.dto;

public record ProfileResponse(
        UserProfileResponseDTO profile,
        UserTagCacheDTO tags
) {}