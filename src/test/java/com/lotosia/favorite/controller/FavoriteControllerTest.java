package com.lotosia.favorite.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lotosia.favorite.client.dto.ProductDto;
import com.lotosia.favorite.dto.request.AddFavoriteRequest;
import com.lotosia.favorite.dto.response.FavoriteCheckResponse;
import com.lotosia.favorite.dto.response.FavoritePageResponse;
import com.lotosia.favorite.dto.response.FavoriteResponse;
import com.lotosia.favorite.dto.response.FavoriteToggleResponse;
import com.lotosia.favorite.exception.GlobalExceptionHandler;
import com.lotosia.favorite.service.FavoriteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author: nijataghayev
 */

@ExtendWith(MockitoExtension.class)
class FavoriteControllerTest {

    @Mock
    private FavoriteService favoriteService;

    @InjectMocks
    private FavoriteController favoriteController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(favoriteController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void addFavoriteByPath_ReturnsCreated() throws Exception {
        Long productId = 101L;
        FavoriteResponse response = FavoriteResponse.builder()
                .id(1L)
                .userId(42L)
                .productId(productId)
                .createdDate(LocalDateTime.now())
                .product(new ProductDto(productId, "SKU1", "Sep", "Qızıl sep", BigDecimal.valueOf(99.0), true, List.of(), null))
                .build();

        when(favoriteService.addFavorite(productId)).thenReturn(response);

        mockMvc.perform(post("/api/v1/favorites/{productId}", productId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.userId").value(42L));
    }

    @Test
    void addFavoriteByBody_ReturnsCreated() throws Exception {
        Long productId = 101L;
        AddFavoriteRequest request = new AddFavoriteRequest(productId);
        FavoriteResponse response = FavoriteResponse.builder()
                .id(1L)
                .userId(42L)
                .productId(productId)
                .createdDate(LocalDateTime.now())
                .build();

        when(favoriteService.addFavorite(productId)).thenReturn(response);

        mockMvc.perform(post("/api/v1/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(productId));
    }

    @Test
    void removeFavorite_ReturnsOk() throws Exception {
        Long productId = 101L;
        doNothing().when(favoriteService).removeFavorite(productId);

        mockMvc.perform(delete("/api/v1/favorites/{productId}", productId))
                .andExpect(status().isOk());

        verify(favoriteService).removeFavorite(productId);
    }

    @Test
    void toggleFavorite_ReturnsOk() throws Exception {
        Long productId = 101L;
        FavoriteToggleResponse response = FavoriteToggleResponse.builder()
                .productId(productId)
                .favorited(true)
                .message("Məhsul sevimlilərə əlavə edildi")
                .build();

        when(favoriteService.toggleFavorite(productId)).thenReturn(response);

        mockMvc.perform(post("/api/v1/favorites/{productId}/toggle", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorited").value(true))
                .andExpect(jsonPath("$.message").value("Məhsul sevimlilərə əlavə edildi"));
    }

    @Test
    void getMyFavorites_ReturnsOk() throws Exception {
        FavoritePageResponse pageResponse = FavoritePageResponse.of(
                List.of(), 0, 0, 0, 12, true, true
        );

        when(favoriteService.getMyFavorites(any(Pageable.class))).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empty").value(true))
                .andExpect(jsonPath("$.emptyMessage").value("❤️ Hələ sevimli məhsulun yoxdur. Bəyəndiyin məhsulları burada saxlaya bilərsən."));
    }

    @Test
    void getMyFavoriteProductIds_ReturnsList() throws Exception {
        when(favoriteService.getMyFavoriteProductIds()).thenReturn(List.of(101L, 102L));

        mockMvc.perform(get("/api/v1/favorites/ids"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(101L))
                .andExpect(jsonPath("$[1]").value(102L));
    }

    @Test
    void checkFavorite_ReturnsOk() throws Exception {
        Long productId = 101L;
        when(favoriteService.isProductFavorite(productId)).thenReturn(new FavoriteCheckResponse(productId, true));

        mockMvc.perform(get("/api/v1/favorites/{productId}/check", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.isFavorite").value(true));
    }

    @Test
    void getFavoritesCount_ReturnsOk() throws Exception {
        when(favoriteService.getMyFavoritesCount()).thenReturn(7L);

        mockMvc.perform(get("/api/v1/favorites/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("7"));
    }

    @Test
    void clearFavorites_ReturnsOk() throws Exception {
        doNothing().when(favoriteService).clearMyFavorites();

        mockMvc.perform(delete("/api/v1/favorites/clear"))
                .andExpect(status().isOk());

        verify(favoriteService).clearMyFavorites();
    }
}
