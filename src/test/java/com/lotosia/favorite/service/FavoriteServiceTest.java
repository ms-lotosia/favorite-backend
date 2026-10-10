package com.lotosia.favorite.service;

import com.lotosia.favorite.client.CatalogClient;
import com.lotosia.favorite.client.dto.ProductDto;
import com.lotosia.favorite.config.JwtAuthDetails;
import com.lotosia.favorite.dto.response.FavoriteCheckResponse;
import com.lotosia.favorite.dto.response.FavoritePageResponse;
import com.lotosia.favorite.dto.response.FavoriteResponse;
import com.lotosia.favorite.dto.response.FavoriteToggleResponse;
import com.lotosia.favorite.entity.Favorite;
import com.lotosia.favorite.exception.ConflictException;
import com.lotosia.favorite.exception.ResourceNotFoundException;
import com.lotosia.favorite.repository.FavoriteRepository;
import com.lotosia.favorite.service.impl.FavoriteServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author: nijataghayev
 */

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private CatalogClient catalogClient;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    private final Long testUserId = 42L;
    private final Long testProductId = 101L;

    @BeforeEach
    void setUpSecurityContext() {
        var auth = new UsernamePasswordAuthenticationToken("user@lotosia.com", null, List.of());
        auth.setDetails(new JwtAuthDetails(testUserId, "user@lotosia.com"));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void addFavorite_Success() {
        when(favoriteRepository.existsByUserIdAndProductId(testUserId, testProductId)).thenReturn(false);

        Favorite saved = Favorite.builder()
                .userId(testUserId)
                .productId(testProductId)
                .build();
        saved.setId(1L);
        saved.setCreatedDate(LocalDateTime.now());

        when(favoriteRepository.save(any(Favorite.class))).thenReturn(saved);

        ProductDto mockProduct = new ProductDto(testProductId, "SKU-001", "Gümüş Üzük", "Qəşəng üzük",
                BigDecimal.valueOf(45.00), true, List.of("img1.png"), null);
        when(catalogClient.getProductById(testProductId)).thenReturn(Optional.of(mockProduct));

        FavoriteResponse response = favoriteService.addFavorite(testProductId);

        assertThat(response).isNotNull();
        assertThat(response.userId()).isEqualTo(testUserId);
        assertThat(response.productId()).isEqualTo(testProductId);
        assertThat(response.product()).isNotNull();
        assertThat(response.product().name()).isEqualTo("Gümüş Üzük");

        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    void addFavorite_AlreadyExists_ThrowsConflictException() {
        when(favoriteRepository.existsByUserIdAndProductId(testUserId, testProductId)).thenReturn(true);

        assertThatThrownBy(() -> favoriteService.addFavorite(testProductId))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("artıq sevimlilər siyahısındadır");

        verify(favoriteRepository, never()).save(any());
    }

    @Test
    void removeFavorite_Success() {
        when(favoriteRepository.existsByUserIdAndProductId(testUserId, testProductId)).thenReturn(true);

        favoriteService.removeFavorite(testProductId);

        verify(favoriteRepository).deleteByUserIdAndProductId(testUserId, testProductId);
    }

    @Test
    void removeFavorite_NotFound_ThrowsResourceNotFoundException() {
        when(favoriteRepository.existsByUserIdAndProductId(testUserId, testProductId)).thenReturn(false);

        assertThatThrownBy(() -> favoriteService.removeFavorite(testProductId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("tapılmadı");

        verify(favoriteRepository, never()).deleteByUserIdAndProductId(any(), any());
    }

    @Test
    void toggleFavorite_WhenAbsent_AddsFavorite() {
        when(favoriteRepository.findByUserIdAndProductId(testUserId, testProductId)).thenReturn(Optional.empty());

        FavoriteToggleResponse response = favoriteService.toggleFavorite(testProductId);

        assertThat(response.favorited()).isTrue();
        assertThat(response.productId()).isEqualTo(testProductId);
        assertThat(response.message()).isEqualTo("Məhsul sevimlilərə əlavə edildi");
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    void toggleFavorite_WhenPresent_RemovesFavorite() {
        Favorite existing = Favorite.builder().userId(testUserId).productId(testProductId).build();
        when(favoriteRepository.findByUserIdAndProductId(testUserId, testProductId)).thenReturn(Optional.of(existing));

        FavoriteToggleResponse response = favoriteService.toggleFavorite(testProductId);

        assertThat(response.favorited()).isFalse();
        assertThat(response.productId()).isEqualTo(testProductId);
        assertThat(response.message()).isEqualTo("Məhsul sevimlilərdən silindi");
        verify(favoriteRepository).delete(existing);
    }

    @Test
    void getMyFavorites_ReturnsPageWithProductDetails() {
        Pageable pageable = PageRequest.of(0, 10);
        Favorite fav = Favorite.builder().userId(testUserId).productId(testProductId).build();
        fav.setId(1L);
        fav.setCreatedDate(LocalDateTime.now());

        when(favoriteRepository.findAllByUserId(testUserId, pageable))
                .thenReturn(new PageImpl<>(List.of(fav), pageable, 1));

        ProductDto mockProduct = new ProductDto(testProductId, "SKU-001", "Qolbaq", "Qızıl qolbaq",
                BigDecimal.valueOf(120.00), true, List.of("img2.png"), null);
        when(catalogClient.getProductById(testProductId)).thenReturn(Optional.of(mockProduct));

        FavoritePageResponse response = favoriteService.getMyFavorites(pageable);

        assertThat(response.content()).hasSize(1);
        assertThat(response.totalElements()).isEqualTo(1);
        assertThat(response.empty()).isFalse();
        assertThat(response.emptyMessage()).isNull();
        assertThat(response.content().get(0).product().name()).isEqualTo("Qolbaq");
    }

    @Test
    void getMyFavorites_Empty_ReturnsEmptyMessageFromDoc() {
        Pageable pageable = PageRequest.of(0, 10);
        when(favoriteRepository.findAllByUserId(testUserId, pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        FavoritePageResponse response = favoriteService.getMyFavorites(pageable);

        assertThat(response.content()).isEmpty();
        assertThat(response.empty()).isTrue();
        assertThat(response.emptyMessage())
                .isEqualTo("❤️ Hələ sevimli məhsulun yoxdur. Bəyəndiyin məhsulları burada saxlaya bilərsən.");
    }

    @Test
    void getMyFavoriteProductIds_ReturnsList() {
        when(favoriteRepository.findProductIdsByUserId(testUserId)).thenReturn(List.of(101L, 102L, 103L));

        List<Long> result = favoriteService.getMyFavoriteProductIds();

        assertThat(result).containsExactly(101L, 102L, 103L);
    }

    @Test
    void isProductFavorite_ReturnsCorrectStatus() {
        when(favoriteRepository.existsByUserIdAndProductId(testUserId, testProductId)).thenReturn(true);

        FavoriteCheckResponse response = favoriteService.isProductFavorite(testProductId);

        assertThat(response.productId()).isEqualTo(testProductId);
        assertThat(response.isFavorite()).isTrue();
    }

    @Test
    void getMyFavoritesCount_ReturnsCount() {
        when(favoriteRepository.countByUserId(testUserId)).thenReturn(5L);

        long count = favoriteService.getMyFavoritesCount();

        assertThat(count).isEqualTo(5L);
    }

    @Test
    void clearMyFavorites_Success() {
        favoriteService.clearMyFavorites();

        verify(favoriteRepository).deleteAllByUserId(testUserId);
    }
}
