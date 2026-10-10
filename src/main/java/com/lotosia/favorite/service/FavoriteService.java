package com.lotosia.favorite.service;

import com.lotosia.favorite.dto.response.FavoriteCheckResponse;
import com.lotosia.favorite.dto.response.FavoritePageResponse;
import com.lotosia.favorite.dto.response.FavoriteResponse;
import com.lotosia.favorite.dto.response.FavoriteToggleResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author: nijataghayev
 */

public interface FavoriteService {

    FavoriteResponse addFavorite(Long productId);

    void removeFavorite(Long productId);

    FavoriteToggleResponse toggleFavorite(Long productId);

    FavoritePageResponse getMyFavorites(Pageable pageable);

    List<Long> getMyFavoriteProductIds();

    FavoriteCheckResponse isProductFavorite(Long productId);

    long getMyFavoritesCount();

    void clearMyFavorites();
}
