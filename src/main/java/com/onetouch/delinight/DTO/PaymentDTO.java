/*********************************************************************
 * 클래스명 : MembersDTO
 * 기능 :
 * 작성자 : 이동건
 * 작성일 : 2025-03-30
 * 수정 : 2025-03-30     이동건
 *********************************************************************/
package com.onetouch.delinight.DTO;


import com.onetouch.delinight.Constant.OrderType;
import com.onetouch.delinight.Constant.PaidCheck;
import lombok.*;

import java.util.List;

import com.onetouch.delinight.Constant.PaidCheck;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
public class PaymentDTO {

    private Long id;

    private OrderType orderType;
    private PaidCheck paidCheck;
    private List<OrdersDTO> ordersDTOList;

    public PaymentDTO setOrdersDTOList(List<OrdersDTO> ordersDTOList){
        this.ordersDTOList = ordersDTOList;
        return this;

    private BigDecimal amount;          // 결제 금액
    private LocalDateTime payDateTime;  // 결제 일시
    private String orderType;           // 결제 방식(선결제 / 후결제)
    private String storeName;           // 가맹점명
    private String hotelName;           // 호텔명
    private String branchName;          // 지점
    private String centerName;          // 본사
    private String checkInRoomNumber;   // 객실 번호(룸서비스일 경우 체크인 정보에서 가져옴)
    private PaidCheck paidCheckType;    // 정산 여부

    public PaymentDTO(Long id, BigDecimal amount, LocalDateTime payDateTime, String orderType, String storeName, String hotelName, String branchName, String centerName, String checkInRoomNumber, PaidCheck paidCheckType) {
        this.id = id;
        this.amount = amount;
        this.payDateTime = payDateTime;
        this.orderType = orderType;
        this.storeName = storeName;
        this.hotelName = hotelName;
        this.branchName = branchName;
        this.centerName = centerName;
        this.checkInRoomNumber = checkInRoomNumber;
        this.paidCheckType = paidCheckType;

    }

}
