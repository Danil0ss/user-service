package com.example.demo.repository;

import com.example.demo.entity.PaymentCard;
import com.example.demo.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {

    //Named Method
    List<PaymentCard> findByUserId(Long userId);
    //JPQL
    @Modifying
    @Transactional
    @Query("UPDATE PaymentCard pc SET " +
            "pc.number=COALESCE(:number, pc.number)," +
            "pc.holder=COALESCE(:holder, pc.holder)," +
            "pc.expirationDate=COALESCE(:expirationDate, pc.expirationDate)," +
            "pc.active=COALESCE(:active, pc.active)" +
            "WHERE pc.id=:id")
    int updateCard(@Param("id") Long id,
                   @Param("number") String number,
                   @Param("holder") String holder,
                   @Param("expirationDate") LocalDate expirationDate,
                   @Param("active") Boolean active);
    //Native SQL
    @Modifying
    @Transactional
    @Query(value = "UPDATE payment_cards SET active=:active WHERE id=:id", nativeQuery = true)
    int updateCardStatus(@Param("id") Long id,@Param("active") Boolean active);
}