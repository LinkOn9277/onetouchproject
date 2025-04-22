/*********************************************************************
 * 클래스명 : PaymentEntity
 * 기능 : 정산
 * 작성자 : 이동건
 * 작성일 : 2025-04-15
 * 수정 : 2025-04-15
 *********************************************************************/
package com.onetouch.delinight.Entity;

import com.onetouch.delinight.Constant.OrderType;
import com.onetouch.delinight.Constant.PaidCheck;
import com.onetouch.delinight.Constant.PayType;
import com.onetouch.delinight.Entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment")
public class PaymentEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @OneToMany(mappedBy = "paymentEntity", fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    private List<OrdersEntity> ordersEntityList;

    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type")
    OrderType orderType;        // 선결제(PAYNOW) or 후결제(PAYLATER)

    @Enumerated(EnumType.STRING)
    @Column(name = "paid_check")
    private PaidCheck paidCheck;        // 정산상태

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_type")
    private PayType payType;

    @Column(name = "total_amount")
    private BigDecimal totalAmount; // 주문 총 금액

    // 추가 필드
    @Column(name = "price_month")
    private String priceMonth;   // 정산 연월


}
