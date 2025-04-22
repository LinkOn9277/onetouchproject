package com.onetouch.delinight.DTO;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TotalPriceDTO {

    private BigDecimal totalAmount;         // 총 결제 금액
    private int paymentCount;               // 전체 결제 건수
    private Long unpaidCount;               // 미정산 건수
    private List<PaymentDTO> paymentList;   // 결제 상세 리스트


}
