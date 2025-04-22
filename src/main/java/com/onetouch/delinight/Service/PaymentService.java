/*********************************************************************
 * 클래스명 : MembersService
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-30
 * 수정 : 2025-03-30
 *********************************************************************/
package com.onetouch.delinight.Service;

import com.onetouch.delinight.DTO.OrdersDTO;
import com.onetouch.delinight.DTO.PaymentDTO;

import java.util.List;

public interface PaymentService {

    public List<OrdersDTO> readOrders(Long paymentId);


    public List<PaymentDTO> paymentByCriteria(String priceMonth, Long storeId, Boolean isPaid);




}
