/*********************************************************************
 * 클래스명 : MembersDTO
 * 기능 :
 * 작성자 : 이동건
 * 작성일 : 2025-03-30
 * 수정 : 2025-03-30     이동건
 *********************************************************************/
package com.onetouch.delinight.DTO;


import com.onetouch.delinight.Constant.PaidCheck;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {


    private Long totalId;               // 정산 ID
    private String priceMonth;          // 정산 연월
    private String type;                // 정산타입
    private String totalPriceData;      // 정산일
    private String unpaidPriceData;     // 미정산
    private PaidCheck checkPaid;        // 정산상태
    private LocalDateTime regTime;      // 등록일
    private LocalDateTime updateTime;   // 수정일


    private List<OrdersDTO> ordersDTOList;

    public PaymentDTO setOrdersDTOList(List<OrdersDTO> ordersDTOList) {
        this.ordersDTOList = ordersDTOList;
        return this;

    }



}

