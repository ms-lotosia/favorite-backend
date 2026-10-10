package com.lotosia.favorite.service.impl;

import com.lotosia.favorite.client.CatalogClient;
import com.lotosia.favorite.client.dto.ProductDto;
import com.lotosia.favorite.dto.response.FavoriteCheckResponse;
import com.lotosia.favorite.dto.response.FavoritePageResponse;
import com.lotosia.favorite.dto.response.FavoriteResponse;
import com.lotosia.favorite.dto.response.FavoriteToggleResponse;
import com.lotosia.favorite.entity.Favorite;
import com.lotosia.favorite.exception.ConflictException;
import com.lotosia.favorite.exception.ResourceNotFoundException;
import com.lotosia.favorite.repository.FavoriteRepository;
import com.lotosia.favorite.service.FavoriteService;
import com.lotosia.favorite.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author: nijataghayev
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final CatalogClient catalogClient;

    @Override
    @Transactional
    public FavoriteResponse addFavorite(Long productId) {
        Long userId = SecurityUtils.getCurrentUserId();

        if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new ConflictException("Bu məhsul artıq sevimlilər siyahısındadır: productId=" + productId);
        }

        Favorite favorite = Favorite.builder()
                .userId(userId)
                .productId(productId)
                .build();

        Favorite saved = favoriteRepository.save(favorite);
        log.info("Product {} added to favorites for userId {}", productId, userId);

        ProductDto product = catalogClient.getProductById(productId).orElse(null);
        return toResponse(saved, product);
    }

    @Override
    @Transactional
    public void removeFavorite(Long productId) {
        Long userId = SecurityUtils.getCurrentUserId();

        if (!favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new ResourceNotFoundException("Bu məhsul sevimlilər siyahısında tapılmadı: productId=" + productId);
        }

        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
        log.info("Product {} removed from favorites for userId {}", productId, userId);
    }

    @Override
    @Transactional
    public FavoriteToggleResponse toggleFavorite(Long productId) {
        Long userId = SecurityUtils.getCurrentUserId();

        Optional<Favorite> existing = favoriteRepository.findByUserIdAndProductId(userId, productId);
        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            log.info("Product {} toggled off (removed) for userId {}", productId, userId);
            return FavoriteToggleResponse.builder()
                    .productId(productId)
                    .favorited(false)
                    .message("Məhsul sevimlilərdən silindi")
                    .build();
        } else {
            Favorite favorite = Favorite.builder()
                    .userId(userId)
                    .productId(productId)
                    .build();
            favoriteRepository.save(favorite);
            log.info("Product {} toggled on (added) for userId {}", productId, userId);
            return FavoriteToggleResponse.builder()
                    .productId(productId)
                    .favorited(true)
                    .message("Məhsul sevimlilərə əlavə edildi")
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public FavoritePageResponse getMyFavorites(Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserId();

        Page<Favorite> page = favoriteRepository.findAllByUserId(userId, pageable);

        List<FavoriteResponse> responses = page.getContent().stream()
                .map(f -> {
                    ProductDto product = catalogClient.getProductById(f.getProductId()).orElse(null);
                    return toResponse(f, product);
                })
                .toList();

        return FavoritePageResponse.of(
                responses,
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize(),
                page.isFirst(),
                page.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getMyFavoriteProductIds() {
        Long userId = SecurityUtils.getCurrentUserId();
        return favoriteRepository.findProductIdsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public FavoriteCheckResponse isProductFavorite(Long productId) {
        Long userId = SecurityUtils.getCurrentUserId();
        boolean exists = favoriteRepository.existsByUserIdAndProductId(userId, productId);
        return new FavoriteCheckResponse(productId, exists);
    }

    @Override
    @Transactional(readOnly = true)
    public long getMyFavoritesCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        return favoriteRepository.countByUserId(userId);
    }

    @Override
    @Transactional
    public void clearMyFavorites() {
        Long userId = SecurityUtils.getCurrentUserId();
        favoriteRepository.deleteAllByUserId(userId);
        log.info("All favorites cleared for userId {}", userId);
    }

    private FavoriteResponse toResponse(Favorite f, ProductDto product) {
        return FavoriteResponse.builder()
                .id(f.getId())
                .userId(f.getUserId())
                .productId(f.getProductId())
                .createdDate(f.getCreatedDate())
                .product(product)
                .build();
    }
}
