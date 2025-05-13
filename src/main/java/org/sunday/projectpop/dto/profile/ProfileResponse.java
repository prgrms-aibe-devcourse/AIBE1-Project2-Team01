package org.sunday.projectpop.dto.profile;

import org.sunday.projectpop.dto.tags.UserTagCacheDTO;

public record ProfileResponse(
        UserProfileResponseDTO profile,
        UserTagCacheDTO tags
) {}