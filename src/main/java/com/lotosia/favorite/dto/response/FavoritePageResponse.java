package com.lotosia.favorite.dto.response;

import lombok.Builder;

import java.util.List;

/**
 * @author: nijataghayev
 */

@Builder
public record FavoritePageResponse(
        List<FavoriteResponse> content,
        long totalElements,
        int totalPages,
        int page,
        int size,
        boolean isFirst,
        boolean isLast,
        boolean empty,
        String emptyMessage
) {
    public static final String DEFAULT_EMPTY_MESSAGE = "❤️ Hələ sevimli məhsulun yoxdur. Bəyəndiyin məhsulları burada saxlaya bilərsən.";

    public static FavoritePageResponse of(List<FavoriteResponse> content,
                                          long totalElements,
                                          int totalPages,
                                          int page,
                                          int size,
                                          boolean isFirst,
                                          boolean isLast) {
        boolean isEmpty = content == null || content.isEmpty();
        return FavoritePageResponse.builder()
                .content(content != null ? content : List.of())
                .totalElements(totalElements)
                .totalPages(totalPages)
                .page(page)
                .size(size)
                .isFirst(isFirst)
                .isLast(isLast)
                .empty(isEmpty)
                .emptyMessage(isEmpty ? DEFAULT_EMPTY_MESSAGE : null)
                .build();
    }
}
