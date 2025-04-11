/*********************************************************************
 * 클래스명 : MembersRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-30
 * 수정 : 2025-03-30
 *********************************************************************/
package com.onetouch.delinight.Repository;

import com.onetouch.delinight.DTO.QnaDTO;
import com.onetouch.delinight.Entity.MembersEntity;
import com.onetouch.delinight.Entity.QnaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QnaRepository extends JpaRepository<QnaEntity, Long> {

//    public QnaEntity findByCheckInEntity_Id(Long id);

    //QnaEntity에서 체크인한 id가 누구인가를 찾는다
    @Query("select c from CheckInEntity c where c.id = :id")
    public QnaEntity selectId(Long id);



    @Query("select h from HotelEntity h where h.id = :id")
    public QnaEntity selectHotelId(Long id);

//    @Query("select q from QnaEntity q where q.hotelEntity.id = :id")
//    Page<QnaEntity> selsctA(Long hotel_id, Pageable pageable);

//    @Query("select q from QnaEntity q where q.checkInEntity.hotelEntity.id = :id")
//    List<QnaEntity> findByHotelId(@Param("hotel_Id") Long hotel_Id);




}
