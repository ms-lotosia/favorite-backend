package com.lotosia.favorite.repository;

import com.lotosia.favorite.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author: nijataghayev
 */

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserIdAndProductId(Long userId, Long productId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    Page<Favorite> findAllByUserId(Long userId, Pageable pageable);

    List<Favorite> findAllByUserIdOrderByCreatedDateDesc(Long userId);

    @Query("SELECT f.productId FROM Favorite f WHERE f.userId = :userId ORDER BY f.createdDate DESC")
    List<Long> findProductIdsByUserId(@Param("userId") Long userId);

    long countByUserId(Long userId);

    void deleteByUserIdAndProductId(Long userId, Long productId);

    void deleteAllByUserId(Long userId);
}
