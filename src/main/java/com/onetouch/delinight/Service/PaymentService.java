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
import com.onetouch.delinight.DTO.TotalPriceDTO;
import com.onetouch.delinight.Entity.OrdersEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {

    public List<OrdersDTO> readOrders(Long paymentId);

    public boolean isOrderToCompany(OrdersEntity order, Long totalId, PayType payType);

    public TotalPriceDTO settlementCenter(Long centerId);

    // CENTER → BRANCH → HOTEL → STORE 정산 조회
    List<PaymentDTO> findAllDate(Long totalId, PayType type);

    // STORE → MENU 별 정산 조회

    // 총 매출


    // TEST - 정산 시스템 구현
    public TotalPriceDTO StoreTotalPrice(Long storeId, LocalDateTime startDate, LocalDateTime endDate);






}
