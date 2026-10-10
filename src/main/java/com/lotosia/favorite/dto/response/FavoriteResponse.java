package com.lotosia.favorite.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lotosia.favorite.client.dto.ProductDto;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * @author: nijataghayev
 */

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FavoriteResponse(
        Long id,
        Long userId,
        Long productId,
        LocalDateTime createdDate,
        ProductDto product
) {
}
