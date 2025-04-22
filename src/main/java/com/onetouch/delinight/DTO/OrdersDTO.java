/*********************************************************************
 * 클래스명 : MembersDTO
 * 기능 :
 * 작성자 : 이동건
 * 작성일 : 2025-03-30
 * 수정 : 2025-03-30     이동건
 *********************************************************************/
package com.onetouch.delinight.DTO;

import com.onetouch.delinight.Constant.OrderType;
import com.onetouch.delinight.Constant.OrdersStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdersDTO {

    private Long id;
    OrderType orderType;                            // 주문유형
    private  String key;                            // 주문 키
    private String memo;                            // 요청사항
    private StoreDTO storeDTO;                      // 매장 정보
    private CheckInDTO checkInDTO;                  // 체크인 정보
    private Long totalPrice;                        // 총 가격
    private List<OrdersItemDTO> ordersItemDTOList;  // 주문 항목 리스트
    private OrdersStatus ordersStatus;              // 주문 상태

    private LocalDateTime pendingTime;              // 주문 대기시간
    private LocalDateTime awaitingTime;             // 주문 준비시간
    private LocalDateTime preparingTime;            // 주문 배송시간
    private LocalDateTime deliveringTime;           // 주문 배송중시간
    private LocalDateTime deliveredTime;            // 주문 완료시간


    public OrdersDTO setStoreDTO(StoreDTO storeDTO){
        this.storeDTO = storeDTO;
        return this;
    }
    public OrdersDTO setCheckInDTO(CheckInDTO checkInDTO){
        this.checkInDTO = checkInDTO;
        return this;
    }

    public OrdersDTO setOrdersItemDTOList(List<OrdersItemDTO> ordersItemDTOList){
        this.ordersItemDTOList = ordersItemDTOList;
        return this;
    }

    public OrderType orderType(){
        return orderType;
    }

}
