package com.example.demo.repository;

import com.example.demo.entity.PaymentCard;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    //Named Method
    Optional<User> findByEmailAndActiveTrue(String email);
    //JPQL
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.name=COALESCE(:name, u.name)," +
            "u.surname=COALESCE(:surname, u.surname)," +
            "u.birthDate=COALESCE(:birthDate,u.birthDate)," +
            "u.email=COALESCE(:email, u.email)," +
            "u.active=COALESCE(:active, u.active)" +
            "WHERE u.id=:id")
    int updateUser(@Param("id") Long id,
                   @Param("name") String name,
                   @Param("surname") String surname,
                   @Param("birthDate") LocalDate birthDate,
                   @Param("email") String email,
                   @Param("active") Boolean active);
    //Native SQL
    @Query(value = "SELECT * FROM users u WHERE u.surname = :surname", nativeQuery = true)
    List<User> findBySurnameNative(@Param("surname") String surname);
}