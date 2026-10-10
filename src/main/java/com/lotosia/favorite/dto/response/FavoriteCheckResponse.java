package com.lotosia.favorite.dto.response;

/**
 * @author: nijataghayev
 */

public record FavoriteCheckResponse(
        Long productId,
        boolean isFavorite
) {
}
