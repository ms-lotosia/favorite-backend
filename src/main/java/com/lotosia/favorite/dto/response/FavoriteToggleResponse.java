package com.lotosia.favorite.dto.response;

import lombok.Builder;

/**
 * @author: nijataghayev
 */

@Builder
public record FavoriteToggleResponse(
        Long productId,
        boolean favorited,
        String message
) {
}
