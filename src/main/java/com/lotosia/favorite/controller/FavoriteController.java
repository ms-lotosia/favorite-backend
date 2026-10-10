package com.lotosia.favorite.controller;

import com.lotosia.favorite.dto.ApiResponse;
import com.lotosia.favorite.dto.request.AddFavoriteRequest;
import com.lotosia.favorite.dto.response.FavoriteCheckResponse;
import com.lotosia.favorite.dto.response.FavoritePageResponse;
import com.lotosia.favorite.dto.response.FavoriteResponse;
import com.lotosia.favorite.dto.response.FavoriteToggleResponse;
import com.lotosia.favorite.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: nijataghayev
 */

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
@Tag(name = "Favorites", description = "İstifadəçinin sevimlilər siyahısının idarə olunması")
@SecurityRequirement(name = "bearerAuth")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "Məhsulu sevimlilərə əlavə et (Path Variable ilə)")
    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse<FavoriteResponse>> addFavoriteByPath(
            @PathVariable Long productId) {
        FavoriteResponse response = favoriteService.addFavorite(productId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "Məhsulu sevimlilərə əlavə et (Request Body ilə)")
    @PostMapping
    public ResponseEntity<ApiResponse<FavoriteResponse>> addFavorite(
            @Valid @RequestBody AddFavoriteRequest request) {
        FavoriteResponse response = favoriteService.addFavorite(request.productId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "Məhsulu sevimlilərdən sil")
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(
            @PathVariable Long productId) {
        favoriteService.removeFavorite(productId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "Sevimliləri dəyişdir (Toggle - əlavə et və ya sil)")
    @PostMapping("/{productId}/toggle")
    public ResponseEntity<ApiResponse<FavoriteToggleResponse>> toggleFavorite(
            @PathVariable Long productId) {
        FavoriteToggleResponse response = favoriteService.toggleFavorite(productId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "İstifadəçinin sevimlilər siyahısını əldə et (Səhifələnmiş)")
    @GetMapping
    public ResponseEntity<ApiResponse<FavoritePageResponse>> getMyFavorites(
            @PageableDefault(page = 0, size = 12, sort = "createdDate", direction = Sort.Direction.DESC)
            Pageable pageable) {
        FavoritePageResponse response = favoriteService.getMyFavorites(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "İstifadəçinin sevimlilərindəki bütün məhsul ID-lərini əldə et")
    @GetMapping("/ids")
    public ResponseEntity<ApiResponse<List<Long>>> getMyFavoriteProductIds() {
        List<Long> ids = favoriteService.getMyFavoriteProductIds();
        return ResponseEntity.ok(ApiResponse.success(ids));
    }

    @Operation(summary = "Məhsulun sevimlilərdə olub-olmadığını yoxla")
    @GetMapping("/{productId}/check")
    public ResponseEntity<ApiResponse<FavoriteCheckResponse>> checkFavorite(
            @PathVariable Long productId) {
        FavoriteCheckResponse response = favoriteService.isProductFavorite(productId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "İstifadəçinin cəmi sevimli məhsul sayını əldə et")
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getFavoritesCount() {
        long count = favoriteService.getMyFavoritesCount();
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @Operation(summary = "Bütün sevimliləri təmizlə")
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearFavorites() {
        favoriteService.clearMyFavorites();
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
