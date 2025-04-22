package com.onetouch.delinight.Repository;

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

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Log4j2
public class CustomPaymentRepository {

    @PersistenceContext
    private EntityManager entityManager;
    private final ModelMapper modelMapper;

    public List<PaymentDTO> findPaymentByCriteria(String priceMonth, Long storeId, Boolean isPaid){
        QPaymentEntity paymentEntity = QPaymentEntity.paymentEntity;
        QOrdersEntity ordersEntity = QOrdersEntity.ordersEntity;

        JPAQuery<PaymentEntity> query = new JPAQuery<>(entityManager);

        log.info("Starting query to find payments with criteria - priceMonth: {}, storeId: {}, isPaid: {}", priceMonth, storeId, isPaid);

        // 1. from 절 설정
        query.from(paymentEntity).join(paymentEntity.ordersEntityList, ordersEntity).fetchJoin();

        log.debug("Query built with from and join clauses.");

        // 2. 정산 월 필터링
        if (priceMonth != null) {
            query.where(paymentEntity.priceMonth.eq(priceMonth));
            log.debug("Added priceMonth filter: {}", priceMonth);
        }

        // 3. 매장 필터링
        if (storeId != null) {
            query.where(ordersEntity.storeEntity.id.eq(storeId));
            log.debug("Added storeId filter: {}", storeId);
        }

        // 4. 정산 상태 필터링
        if (isPaid != null) {
            query.where(paymentEntity.paidCheck.eq(isPaid ? PaidCheck.paid : PaidCheck.none));
            log.debug("Added paid status filter: {}", isPaid ? PaidCheck.paid : PaidCheck.none);
        }

        // 5. 쿼리 실행
        List<PaymentEntity> paymentEntities = query.fetch();
        log.info("Executed query. Number of PaymentEntities fetched: {}", paymentEntities.size());

        // 6. PaymentEntity → PaymentDTO 변환
        List<PaymentDTO> paymentDTOList = paymentEntities.stream().map(payment -> {
            // OrdersDTO 변환
            List<OrdersDTO> ordersDTOList = payment.getOrdersEntityList().stream().map(order -> {
                log.debug("Converting OrderEntity (id: {}) to OrdersDTO", order.getId());
                return OrdersDTO.builder()
                        .id(order.getId())
                        .totalPrice(order.getTotalPrice())
                        .ordersStatus(order.getOrdersStatus())
                        .pendingTime(order.getPendingTime())
                        .deliveredTime(order.getDeliveredTime())
                        .storeDTO(modelMapper.map(order.getStoreEntity(), StoreDTO.class).setHotelDTO(modelMapper.map(order.getStoreEntity().getHotelEntity(), HotelDTO.class).setBranchDTO(modelMapper.map(order.getStoreEntity().getHotelEntity().getBranchEntity(), BranchDTO.class).setCenterDTO(modelMapper.map(order.getStoreEntity().getHotelEntity().getBranchEntity().getCenterEntity(), CenterDTO.class)))))
                        .build();
            }).collect(Collectors.toList());

            // PaymentDTO 변환
            log.debug("Converting PaymentEntity (id: {}) to PaymentDTO", payment.getId());
            return PaymentDTO.builder()
                    .totalId(payment.getId())               // 정산 ID
                    .priceMonth(payment.getPriceMonth())    // 정산 월
                    .checkPaid(payment.getPaidCheck())      // 정산 상태
                    .regTime(payment.getRegTime())          // 등록일
                    .updateTime(payment.getUpdateTime())    // 수정일
                    .ordersDTOList(ordersDTOList)           // 주문 내역 리스트
                    .build();
        }).collect(Collectors.toList());

        log.info("Converted {} PaymentEntities to PaymentDTOs", paymentDTOList.size());

        return paymentDTOList;
    }


}
