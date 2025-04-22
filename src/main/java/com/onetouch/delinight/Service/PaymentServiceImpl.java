/*********************************************************************
 * 클래스명 : MemberServiceImpl
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-30
 * 수정 : 2025-03-30
 *********************************************************************/
package com.onetouch.delinight.Service;

import com.onetouch.delinight.Constant.OrdersStatus;
import com.onetouch.delinight.DTO.CheckInDTO;
import com.onetouch.delinight.DTO.OrdersDTO;
import com.onetouch.delinight.DTO.PaymentDTO;
import com.onetouch.delinight.DTO.StoreDTO;
import com.onetouch.delinight.Entity.OrdersEntity;
import com.onetouch.delinight.Entity.PaymentEntity;
import com.onetouch.delinight.Repository.CustomPaymentRepository;
import com.onetouch.delinight.Repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;
    private final CustomPaymentRepository customPaymentRepository;

    @Override
    public List<OrdersDTO> readOrders(Long paymentId) {

        PaymentEntity paymentEntity = paymentRepository.findById(paymentId).get();
        List<OrdersEntity> ordersEntityList = paymentEntity.getOrdersEntityList();
        List<OrdersDTO> ordersDTOList = ordersEntityList.stream()
                .map(data->modelMapper.map(data,OrdersDTO.class)
                        .setStoreDTO(modelMapper.map(data.getStoreEntity(), StoreDTO.class))
                        .setCheckInDTO(modelMapper.map(data.getCheckInEntity(), CheckInDTO.class))).toList();

        return ordersDTOList;
    }

    @Override
    public List<PaymentDTO> paymentByCriteria(String priceMonth, Long storeId, Boolean isPaid) {

        log.info("priceMonth : {}, storeId : {}, isPaid : {}", priceMonth, storeId, isPaid);

        // 1. 쿼리 DSL 조회
        List<PaymentDTO> paymentDTOList = customPaymentRepository.findPaymentByCriteria(priceMonth, storeId, isPaid);

        log.debug("Fetched {} PaymentDTO(s) from repository.", paymentDTOList.size());

        // 2. 정산 계산식 처리 (예시: 주문별 총합 계산)
        log.info("Starting calculation of total amount for each PaymentDTO.");

        return paymentDTOList.stream().map(paymentDTO -> {
            log.debug("Processing PaymentDTO with totalId: {}", paymentDTO.getTotalId());

            // 각 정산 내역에 대해 총액 계산
            long totalAmount = 0;

            // 주문별 총액 계산 (PaymentDTO에 포함된 OrdersDTO 리스트 순회)
            for (OrdersDTO ordersDTO : paymentDTO.getOrdersDTOList()) {
                log.debug("Processing OrderDTO with id: {} and price: {}", ordersDTO.getId(), ordersDTO.getTotalPrice());

                // 예시: 주문 상태가 '배송완료'인 경우만 총액에 포함
                if (ordersDTO.getOrdersStatus() == OrdersStatus.DELIVERED) {
                    log.debug("OrderDTO with id: {} is DELIVERED. Adding totalPrice to PaymentDTO.", ordersDTO.getId());
                    totalAmount += ordersDTO.getTotalPrice();  // 주문 총액 더하기
                } else {
                    log.debug("OrderDTO with id: {} is not DELIVERED. Skipping this order.", ordersDTO.getId());
                }
            }

            // 계산된 총액을 PaymentDTO에 설정
            log.debug("Calculated total amount for PaymentDTO with totalId: {} is: {}", paymentDTO.getTotalId(), totalAmount);
            paymentDTO.setTotalPriceData(String.valueOf(totalAmount));  // PaymentDTO에 총액 필드를 설정 (String 타입으로 설정)

            // 계산된 PaymentDTO 반환
            return paymentDTO;
        }).collect(Collectors.toList());

    }


}
