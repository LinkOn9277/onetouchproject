/*********************************************************************
 * 클래스명 : MembersRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-30
 * 수정 : 2025-03-30
 *********************************************************************/
package com.onetouch.delinight.Repository;

import com.onetouch.delinight.DTO.QnaDTO;
import com.onetouch.delinight.Entity.HotelEntity;
import com.onetouch.delinight.Entity.MembersEntity;
import com.onetouch.delinight.Entity.QnaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface QnaRepository extends JpaRepository<QnaEntity, Long> {



    //qna에 속한 호텔 id를 기준으로 찾는다
    public Page<QnaEntity> findByHotelEntity_Id(Long email, Pageable pageable);

    //QnaEntity에서 체크인한 id가 누구인가를 찾는다
    @Query("select c from CheckInEntity c where c.id = :id")
    public QnaEntity selectId(Long id);

    //hotelId가 특정 값인 qna만 전부 찾아서 리스트로 리턴
    public List<QnaEntity> findByHotelEntity_Id(Long hotelId);

    //호텔id로 qna를 하나 찾아줘
    @Query("select q from QnaEntity q where q.hotelEntity.id = :id")
    public QnaEntity selectHotelId(Long id);









}
