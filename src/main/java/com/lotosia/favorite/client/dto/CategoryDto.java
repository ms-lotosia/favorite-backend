package com.lotosia.favorite.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author: nijataghayev
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public record CategoryDto(
        Long id,
        String name,
        String description,
        String image,
        String slug,
        Boolean active
) {
}
