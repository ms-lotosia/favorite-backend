package com.lotosia.favorite.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * @author: nijataghayev
 */

public record AddFavoriteRequest(
        @NotNull(message = "Məhsul ID-si boş ola bilməz")
        @Positive(message = "Məhsul ID-si müsbət ədəd olmalıdır")
        Long productId
) {
}
