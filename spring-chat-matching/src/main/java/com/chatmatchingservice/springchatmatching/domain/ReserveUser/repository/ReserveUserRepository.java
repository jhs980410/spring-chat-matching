package com.chatmatchingservice.springchatmatching.domain.ReserveUser.repository;


import com.chatmatchingservice.springchatmatching.domain.ReserveUser.entity.ReserveUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReserveUserRepository extends JpaRepository<ReserveUser, Long> {

    /* =========================
       조회
    ========================= */

    List<ReserveUser> findAllByUserIdOrderByIdDesc(Long userId);

    Optional<ReserveUser> findByIdAndUserId(Long id, Long userId);

    Optional<ReserveUser> findByUserIdAndIsDefaultTrue(Long userId);

    boolean existsByUserId(Long userId);

    /* =========================
       기본 예매자 처리
    ========================= */

    @Modifying
    @Query("""
        UPDATE ReserveUser r
        SET r.isDefault = false
        WHERE r.userId = :userId
          AND r.isDefault = true
    """)
    int clearDefaultReserveUser(@Param("userId") Long userId);

    /* =========================
       삭제 가능 여부 검증
    ========================= */

    @Query("""
        SELECT COUNT(o) > 0
        FROM TicketOrder o
        WHERE o.reserveUser.id = :reserveUserId
    """)
    boolean existsOrderByReserveUserId(@Param("reserveUserId") Long reserveUserId);
    // ⭐ default 삭제 보정용
    Optional<ReserveUser> findFirstByUserIdOrderByIdAsc(Long userId);

    List<ReserveUser> findByUserId(Long userId);

    boolean existsByUserIdAndIsDefaultTrue(Long userId);

}
