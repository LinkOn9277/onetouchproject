package com.onetouch.delinight.Repository;

import com.onetouch.delinight.Constant.OrdersStatus;
import com.onetouch.delinight.Constant.PaidCheck;
import com.onetouch.delinight.DTO.*;
import com.onetouch.delinight.Entity.PaymentEntity;
import com.onetouch.delinight.Entity.QOrdersEntity;
import com.onetouch.delinight.Entity.QPaymentEntity;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Log4j2
public class CustomPaymentRepository {

    @PersistenceContext
    private EntityManager entityManager;
    private final ModelMapper modelMapper;

    public List<PaymentDTO> findPaymentByCriteria(String priceMonth, String type, Long storeId, Boolean isPaid){
        QPaymentEntity paymentEntity = QPaymentEntity.paymentEntity;
        QOrdersEntity ordersEntity = QOrdersEntity.ordersEntity;

        JPAQuery<PaymentEntity> query = new JPAQuery<>(entityManager);

        log.info("기준에 맞는 결제 조회 쿼리 시작 - priceMonth: {}, storeId: {}, isPaid: {}", priceMonth, storeId, isPaid);

        // 1. from 절 설정
        query.from(paymentEntity).join(paymentEntity.ordersEntityList, ordersEntity).fetchJoin();

       log.info("from과 join 절이 설정된 쿼리 빌드 완료.");

        // 2. 정산 월 필터링
        if (priceMonth != null) {
            query.where(paymentEntity.priceMonth.eq(priceMonth));
            log.info("정산 월 필터 추가: {}", priceMonth);
        }

        // 타입별 날짜 필터(regTime 기준)
        if (priceMonth != null && type != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            YearMonth yearMonth = YearMonth.parse(priceMonth, formatter);  // priceMonth  yyyy-MM 형태로 받는다고 가정
            LocalDateTime from = null;
            LocalDateTime to = null;

            LocalDate now = LocalDate.now();
            switch (type.toUpperCase()) {
                case "DAY":
                    from = now.atStartOfDay();  // 오늘의 시작
                    to = now.atTime(23, 59, 59);  // 오늘의 끝
                    break;
                case "WEEK":
                    from = now.with(DayOfWeek.MONDAY).atStartOfDay();  // 이번 주의 시작 (월요일)
                    to = now.with(DayOfWeek.SUNDAY).atTime(23, 59, 59);  // 이번 주의 끝 (일요일)
                    break;
                case "MONTH":
                    from = yearMonth.atDay(1).atStartOfDay();  // 해당 월의 시작 (1일)
                    to = yearMonth.atEndOfMonth().atTime(23, 59, 59);  // 해당 월의 끝 (말일)
                    break;
                case "YEAR":
                    from = LocalDate.of(now.getYear(), 1, 1).atStartOfDay();  // 올해의 시작 (1월 1일)
                    to = LocalDate.of(now.getYear(), 12, 31).atTime(23, 59, 59);  // 올해의 끝 (12월 31일)
                    break;
                default:
                    throw new IllegalArgumentException("유효하지 않은 타입: " + type);
            }

            // 날짜 필터 추가
            query.where(paymentEntity.regTime.between(from, to));
            log.info("날짜 범위 필터 추가: {} ~ {}", from, to);
        }

        // 3. 매장 필터링
        if (storeId != null) {
            query.where(ordersEntity.storeEntity.id.eq(storeId));
            log.info("매장 필터 추가: {}", storeId);
        }

        // 4. 정산 상태 필터링
        if (isPaid != null) {
            query.where(paymentEntity.paidCheck.eq(isPaid ? PaidCheck.paid : PaidCheck.none));
            log.info("정산 상태 필터 추가: {}", isPaid ? PaidCheck.paid : PaidCheck.none);
        }

        // 5. 쿼리 실행
        List<PaymentEntity> paymentEntities = query.fetch();
        log.info("쿼리 실행 완료. 조회된 PaymentEntity 개수: {}", paymentEntities.size());

        // 6. PaymentEntity → PaymentDTO 변환
        List<PaymentDTO> paymentDTOList = paymentEntities.stream().map(payment -> {
            // OrdersDTO 변환
            List<OrdersDTO> ordersDTOList = payment.getOrdersEntityList().stream().map(order -> {

                log.info("OrderEntity (id: {})를 OrdersDTO로 변환 중", order.getId());
                return OrdersDTO.builder()
                        .id(order.getId())
                        .totalPrice(order.getTotalPrice())
                        .ordersStatus(order.getOrdersStatus())
                        .pendingTime(order.getPendingTime())
                        .deliveredTime(order.getDeliveredTime())
                        .storeDTO(modelMapper.map(order.getStoreEntity(), StoreDTO.class)
                        .setHotelDTO(modelMapper.map(order.getStoreEntity().getHotelEntity(), HotelDTO.class)
                        .setBranchDTO(modelMapper.map(order.getStoreEntity().getHotelEntity().getBranchEntity(), BranchDTO.class)
                        .setCenterDTO(modelMapper.map(order.getStoreEntity().getHotelEntity().getBranchEntity().getCenterEntity(), CenterDTO.class)))))
                        .build();
            }).collect(Collectors.toList());

            // 미정산 금액 계산
            long unpaidPriceData = 0;
            for (OrdersDTO ordersDTO : ordersDTOList) {
                if (ordersDTO.getOrdersStatus() != OrdersStatus.DELIVERED){
                    unpaidPriceData += ordersDTO.getTotalPrice();
                }
            }
            log.info("미정산 금액 계산 : {}", unpaidPriceData);

            // PaymentDTO 변환
            log.info("PaymentEntity (id: {})를 PaymentDTO로 변환 중", payment.getId());
            return PaymentDTO.builder()
                    .totalId(payment.getId())                           // 정산 ID
                    .priceMonth(payment.getPriceMonth())                // 정산 월
                    .checkPaid(payment.getPaidCheck())                  // 정산 상태
                    .regTime(payment.getRegTime())                      // 등록일
                    .updateTime(payment.getUpdateTime())                // 수정일
                    .ordersDTOList(ordersDTOList)                       // 주문 내역 리스트
                    .unpaidPriceData(String.valueOf(unpaidPriceData))   // 미정산 금액
                    .build();
        }).collect(Collectors.toList());

        log.info("총 {}개의 PaymentEntities를 PaymentDTO로 변환 완료", paymentDTOList.size());

        return paymentDTOList;
    }


}
