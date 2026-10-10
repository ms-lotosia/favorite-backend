package com.lotosia.favorite.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.OffsetDateTime;

/**
 * @author: nijataghayev
 */

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        String code,
        String message,
        Integer status,
        String path,
        OffsetDateTime timestamp,
        Object details
) {
}
