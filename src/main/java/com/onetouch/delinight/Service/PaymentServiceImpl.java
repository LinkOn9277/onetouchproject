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
    public List<PaymentDTO> paymentByCriteria(String priceMonth, String type, Long storeId, Boolean isPaid) {

        // 1. 쿼리 DSL 조회
        List<PaymentDTO> paymentDTOList = customPaymentRepository.findPaymentByCriteria(priceMonth, type, storeId, isPaid);

        log.info("레포지토리에서 {}개의 PaymentDTO를 조회함.", paymentDTOList.size());

        // 2. 정산 계산식 처리 (예시: 주문별 총합 계산)
        log.info("각 PaymentDTO의 총 정산 금액 계산 시작.");

        return paymentDTOList.stream().map(paymentDTO -> {
            log.info("totalId: {}인 PaymentDTO 처리 중...", paymentDTO.getTotalId());

            // 각 정산 내역에 대해 총액 계산
            long totalAmount = 0;
            long unpaidPriceData = 0;


            for (OrdersDTO ordersDTO : paymentDTO.getOrdersDTOList()) {

                OrdersStatus status = ordersDTO.getOrdersStatus();
                Long orderPrice = ordersDTO.getTotalPrice();

                if (status == OrdersStatus.DELIVERED){
                    totalAmount += orderPrice;
                }else {
                    unpaidPriceData += orderPrice;
                }
            }

            // 계산된 총액을 PaymentDTO에 설정
            log.info("totalId: {}인 PaymentDTO의 최종 총액은 {}원입니다.", paymentDTO.getTotalId(), totalAmount);
            paymentDTO.setTotalPriceData(String.valueOf(totalAmount));          // 정산된 총액
            paymentDTO.setUnpaidPriceData(String.valueOf(unpaidPriceData));     // 미정산 금액

            // 계산된 PaymentDTO 반환
            return paymentDTO;
        }).collect(Collectors.toList());

    }


}
