/*********************************************************************
 * 클래스명 : MembersRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-30
 * 수정 : 2025-03-30
 *********************************************************************/
package com.onetouch.delinight.Repository;

import com.onetouch.delinight.Entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Collection;
import java.util.List;
import java.util.Optional;

import java.util.List;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    PaymentEntity findByOrdersEntityList_Id(Long id);

    // fetch join 사용시 지연로딩 없이 즉시 로딩 및 쿼리문 1번에 다 해결 가능
    // QueryDSL 해보려했으나 아직 실력이 부족해서 해결못함
    @Query("select p from PaymentEntity p JOIN p.ordersEntityList o where o.checkInEntity.usersEntity.email =:email")
    List<PaymentEntity> findPaymentEntitiesByUsersEmail(@Param("email") String email);


    @Query("""
    select distinct p from PaymentEntity p
    join fetch p.ordersEntityList o
    join fetch o.storeEntity s
    join fetch s.hotelEntity h
    join fetch h.branchEntity b
    join fetch b.centerEntity c
    where c.id = :centerId""")
    List<PaymentEntity> findCenterForDate(@Param("centerId") Long centerId);

    @Query("""
    select distinct p from PaymentEntity p
    join fetch p.ordersEntityList o
    join fetch o.storeEntity s
    join fetch s.hotelEntity h
    join fetch h.branchEntity b
    where b.id = :branchId""")
    List<PaymentEntity> findBranchForDate(@Param("branchId") Long branchId);

    @Query("""
    select distinct p from PaymentEntity p
    join fetch p.ordersEntityList o
    join fetch o.storeEntity s
    join fetch s.hotelEntity h
    where h.id = :hotelId""")
    List<PaymentEntity> findHotelForDate(@Param("hotelId") Long hotelId);

    @Query("""
    select distinct p from PaymentEntity p
    join fetch p.ordersEntityList o
    join fetch o.storeEntity s
    where s.id = :storeId""")
    List<PaymentEntity> findStoreForDate(@Param("storeId") Long storeId);




}
