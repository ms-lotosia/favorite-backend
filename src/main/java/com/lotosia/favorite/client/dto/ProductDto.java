package com.lotosia.favorite.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: nijataghayev
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductDto(
        Long id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        Boolean active,
        List<String> images,
        CategoryDto category
) {
}
