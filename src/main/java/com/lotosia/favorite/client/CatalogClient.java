package com.lotosia.favorite.client;

import com.lotosia.favorite.client.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Optional;

/**
 * @author: nijataghayev
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogClient {

    private final RestClient catalogRestClient;

    public Optional<ProductDto> getProductById(Long productId) {
        try {
            ProductDto product = catalogRestClient.get()
                    .uri("/api/v1/public/products/{id}", productId)
                    .retrieve()
                    .body(ProductDto.class);

            return Optional.ofNullable(product);
        } catch (RestClientResponseException ex) {
            log.warn("Catalog service returned status {} for product id {}: {}",
                    ex.getStatusCode(), productId, ex.getMessage());
            return Optional.empty();
        } catch (Exception ex) {
            log.warn("Failed to reach catalog-service for product id {}: {}",
                    productId, ex.getMessage());
            return Optional.empty();
        }
    }
}
