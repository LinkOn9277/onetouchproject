/*********************************************************************
 * 클래스명 : MembersService
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-30
 * 수정 : 2025-03-30
 *********************************************************************/
package com.onetouch.delinight.Service;

import com.onetouch.delinight.Constant.PayType;
import com.onetouch.delinight.DTO.OrdersDTO;
import com.onetouch.delinight.DTO.PaymentDTO;

import java.util.List;

public interface PaymentService {

    public List<OrdersDTO> readOrders(Long paymentId);

    // CENTER → BRANCH → HOTEL → STORE 정산 조회
    List<PaymentDTO> findAllDate(Long totalId, PayType type);

    // STORE → MENU 별 정산 조회

    // 총 매출

}
