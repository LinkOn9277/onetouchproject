/*********************************************************************
 * 클래스명 : MemberServiceImpl
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-30
 * 수정 : 2025-03-30
 *********************************************************************/
package com.onetouch.delinight.Service;

import com.onetouch.delinight.Constant.PaidCheck;
import com.onetouch.delinight.Constant.PayType;
import com.onetouch.delinight.DTO.*;
import com.onetouch.delinight.Entity.*;
import com.onetouch.delinight.Repository.CenterRepository;
import com.onetouch.delinight.Repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;
    private final CenterRepository centerRepository;

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
    public boolean isOrderToCompany(OrdersEntity order, Long totalId, PayType payType) {
        log.info("{}, {}, {}",order, totalId, payType);
        StoreEntity storeEntity = order.getStoreEntity();

        if (storeEntity == null) {
            return false;
        }

        return switch (payType) {
            case STORE -> storeEntity.getId().equals(totalId);
            case HOTEL -> {
                HotelEntity hotelEntity = storeEntity.getHotelEntity();
                yield hotelEntity.getId().equals(totalId);
            }
            case BRANCH -> {
                BranchEntity branchEntity = storeEntity.getHotelEntity().getBranchEntity();
                yield branchEntity != null && branchEntity.getId().equals(totalId);
            }
            case CENTER -> {
                CenterEntity centerEntity = storeEntity.getHotelEntity().getBranchEntity().getCenterEntity();
                yield centerEntity != null && centerEntity.getId().equals(totalId);
            }
        };

    }


    @Override
    public TotalPriceDTO settlementCenter(Long centerId) {
        log.info("정산 로직 시작 - centerId : {}",centerId);

        // TEST CENTER_ID 직접 조회
        CenterEntity centerEntity = centerRepository.findById(centerId).orElseThrow(EntityNotFoundException::new);
        log.info("CENTER 조회 성공 - {}", centerEntity);

        // 1. 해당 CENTER_ID에 속하는 결제 내역 조회
        List<PaymentEntity> paymentEntityList = paymentRepository.findCenterForDate(centerId);
        log.info("결제 내역 조회 성공 - 건수 : {}",paymentEntityList.size());

        if (paymentEntityList == null || paymentEntityList.isEmpty()) {
            return TotalPriceDTO.builder()
                    .totalAmount(BigDecimal.ZERO)
                    .paymentCount(0)
                    .unpaidCount(0L)
                    .paymentList(Collections.emptyList())
                    .build();
        }

        // 2. Entity → DTO 변환
        List<PaymentDTO> paymentDTOList =
                paymentEntityList.stream().map(data -> modelMapper.map(data, PaymentDTO.class)).toList();

        // 3. 총 결제 금액
        BigDecimal totalAmount =
                paymentDTOList.stream().map(PaymentDTO::getAmount)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. 미정산 건수
        Long unpaidCount =
                paymentDTOList.stream()
                        .filter(dto -> dto.getPaidCheckType() == PaidCheck.none).count();

        // 5. 결과 리턴
        TotalPriceDTO result = TotalPriceDTO.builder()
                .totalAmount(totalAmount)
                .paymentCount(paymentDTOList.size())
                .unpaidCount(unpaidCount)
                .paymentList(paymentDTOList)
                .build();

        return result;
    }


    @Override
    public List<PaymentDTO> findAllDate(Long totalId, PayType type) {
        log.info("findAllDate 시작 - totalId : {}, type: {}",totalId,type);

        List<PaymentEntity> paymentList = new ArrayList<>();
        List<PaymentEntity> filteredPaymentList = new ArrayList<>();

        if (type == PayType.STORE) {
            paymentList = paymentRepository.findStoreForDate(totalId);
            log.info("store 결제 건수 : {}",paymentList.size());
            paymentList.forEach(payment -> {
                payment.getOrdersEntityList().forEach(order -> {
                    if (order.getStoreEntity().getId().equals(totalId)) {
                        log.info("일치하는 주문 {} , {}", order.getStoreEntity().getId(), order);
                        filteredPaymentList.add(
                                PaymentEntity.builder()
                                        .id(payment.getId())
                                        .orderType(payment.getOrderType())
                                        .paidCheck(payment.getPaidCheck())
                                        .totalAmount(payment.getTotalAmount())
                                        .paymentTime(payment.getRegTime())
                                          .ordersEntityList(List.of(order))
                                        .build()
                        );
                    }
                });
            });
        } else if (type == PayType.HOTEL) {
            paymentList = paymentRepository.findHotelForDate(totalId);
            log.info("Hotel 결제 건수 {}",paymentList.size());
            paymentList.forEach(payment -> {
                payment.getOrdersEntityList().forEach(order -> {
                    if (order.getStoreEntity().getHotelEntity().getId().equals(totalId)) {
                        log.info("Hotel 일치하는 주문 발견 {} {}", order.getStoreEntity().getId(), order);
                        filteredPaymentList.add(
                                PaymentEntity.builder()
                                        .id(payment.getId())
                                        .orderType(payment.getOrderType())
                                        .paidCheck(payment.getPaidCheck())
                                        .totalAmount(payment.getTotalAmount())
                                        .paymentTime(payment.getRegTime())
                                        .ordersEntityList(List.of(order))
                                        .build()
                        );
                    }
                });
            });
        } else if (type == PayType.BRANCH) {
            paymentList = paymentRepository.findBranchForDate(totalId);
            log.info("Branch 결제 건수 : {}",paymentList.size());
            paymentList.forEach(payment -> {
                payment.getOrdersEntityList().forEach(order -> {
                    if (order.getStoreEntity().getHotelEntity().getBranchEntity().getId().equals(totalId)) {
                        log.info("Branch 일치하는 주문 발견 {} {}", order.getStoreEntity().getId(), order);
                        filteredPaymentList.add(
                                PaymentEntity.builder()
                                        .id(payment.getId())
                                        .orderType(payment.getOrderType())
                                        .paidCheck(payment.getPaidCheck())
                                        .totalAmount(payment.getTotalAmount())
                                        .paymentTime(payment.getRegTime())
                                        .ordersEntityList(List.of(order))
                                        .build()
                        );
                    }
                });
            });
        } else if (type == PayType.CENTER) {
            paymentList = paymentRepository.findCenterForDate(totalId);
            log.info("Center 결제 건수 {}",paymentList.size());
            paymentList.forEach(payment -> {
                payment.getOrdersEntityList().forEach(order -> {
                    if (order.getStoreEntity().getHotelEntity().getBranchEntity().getCenterEntity().getId().equals(totalId)) {
                        log.info("Center 일치하는 주문 발견 {} {}", order.getStoreEntity().getId(), order);
                        filteredPaymentList.add(
                                PaymentEntity.builder()
                                        .id(payment.getId())
                                        .orderType(payment.getOrderType())
                                        .paidCheck(payment.getPaidCheck())
                                        .totalAmount(payment.getTotalAmount())
                                        .paymentTime(payment.getRegTime())
                                        .ordersEntityList(List.of(order))
                                        .build()
                        );
                    }
                });
            });
        }
        log.info("필터링 된 결제 건수 {}", filteredPaymentList.size());

        // DTO 변환
        return filteredPaymentList.stream()
                .map(payment -> {
                    OrdersEntity order = payment.getOrdersEntityList().get(0);
                    StoreEntity store = order.getStoreEntity();
                    HotelEntity hotel = store.getHotelEntity();
                    BranchEntity branch = hotel.getBranchEntity();
                    CenterEntity center = branch.getCenterEntity();

                    String roomNumber = order.getCheckInEntity() != null ? String.valueOf(order.getCheckInEntity().getRoomEntity()) : null;

                    return new PaymentDTO(
                            payment.getId(),
                            payment.getTotalAmount(),
                            payment.getRegTime(),
                            order.getOrdersStatus().name(),
                            store.getName(),
                            hotel.getName(),
                            branch.getName(),
                            center.getName(),
                            roomNumber,
                            payment.getPaidCheck()
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public TotalPriceDTO StoreTotalPrice(Long storeId, LocalDateTime startDate, LocalDateTime endDate) {

        return null;
    }


    /*@Override
    public List<PaymentDTO> findAllDate(Long totalId, PayType type) {

        log.info("totalId : {}, payType : {}", totalId, type);

        // 정산 타입에 따라 해당하는 결제 Entity 리스트 가져오기
        List<PaymentEntity> paymentEntityList = switch (type){
            case CENTER -> paymentRepository.findCenterForDate(totalId);            // CENTER 기준 조회
            case BRANCH -> paymentRepository.findBranchForDate(totalId);            // BRANCH 기준 조회
            case HOTEL -> paymentRepository.findHotelForDate(totalId);              // HOTEL 기준 조회
            case STORE -> paymentRepository.findStoreForDate(totalId);              // STORE 기준 조회
            default -> throw new IllegalArgumentException("유효하지 않는 정산 타입");
        };

        log.info("조회된 결제 건수 : {}", paymentEntityList.size());

        // 최종 결과를 담을 새 결제 리스트
        List<PaymentEntity> filterPayment = new ArrayList<>();

        // 가져온 결제 데이터(paymentEntityList) 하나씩 순회
        for (PaymentEntity paymentEntity : paymentEntityList) {
            // 각 결제에 연결된 주문 리스트 순회
            for (OrdersEntity order : paymentEntity.getOrdersEntityList()) {

                // 주문에 연결된 StoreId 확인
                Long storeId = order.getStoreEntity() != null ? order.getStoreEntity().getId() : null;
                log.info("storeId: {}, totalId: {}", storeId, totalId);

                // 정산 타입에 따라 주문이 속한 ID가 totalId와 일치하는지 비교
                boolean isMatch = switch (type){
                    case CENTER -> order.getStoreEntity().getHotelEntity().getBranchEntity().getCenterEntity().getId().equals(totalId); // CENTER ID 비교
                    case BRANCH -> order.getStoreEntity().getHotelEntity().getBranchEntity().getId().equals(totalId);                   // BRANCH ID 비교
                    case HOTEL -> order.getStoreEntity().getHotelEntity().getId().equals(totalId);                                      // HOTEL ID 비교
                    case STORE  -> storeId != null && storeId.equals(totalId);                                                          // STORE ID 비교
                    default -> false;
                };

                log.info("일치 여부: {}", isMatch ? "일치" : "불일치");

                // 일치하는 주문만 추출해 새로운 paymentEntity 생성 및 리스트 추가
                if (isMatch) {
                    filterPayment.add(PaymentEntity.builder()
                            .id(paymentEntity.getId())                      // 기존 결제 ID 복사
                            .orderType(paymentEntity.getOrderType())        // 주문 유형 복사
                            .paidCheck(paymentEntity.getPaidCheck())        // 정산 여부 복사
                            .totalAmount(paymentEntity.getTotalAmount())    // 총 금액 복사.regTime(paymentEntity.getRegTime())            // 등록 시간 복사
                            .ordersEntityList(List.of(order))               // 주문 리스트를 현재 주문 하나만 포함하여 설정
                            .build());                                      // 새 PaymentEntity 객체 생성
                }
            }
        }

        log.info("필터링 된 결제 건수 : {}", filterPayment.size());

        // Step 3. DTO 변환
        return filterPayment.stream()
                .map(payment -> {
                    OrdersEntity order = payment.getOrdersEntityList().get(0); // 첫 번째 주문만 가져옴
                    StoreEntity store = order.getStoreEntity();
                    HotelEntity hotel = store.getHotelEntity();
                    BranchEntity branch = hotel.getBranchEntity();
                    CenterEntity center = branch.getCenterEntity();

                    String roomNumber = order.getCheckInEntity() != null ? String.valueOf(order.getCheckInEntity().getRoomEntity()) : null;

                    // DTO 객체 생성 후 반환
                    return new PaymentDTO(
                            payment.getId(),
                            payment.getTotalAmount(),
                            payment.getRegTime(),
                            order.getOrdersStatus().name(),
                            store.getName(),
                            hotel.getName(),
                            branch.getName(),
                            center.getName(),
                            roomNumber,
                            payment.getPaidCheck()
                    );
                })
                .collect(Collectors.toList());
    }*/ // 이거 지우면 안됨 무조건 지우면 안됨 ㄹㅇ 지우면 안됨
    /*
        List<PaymentEntity> paymentEntityList;
        List<PaymentEntity> paymentEntityList2 =new ArrayList<>();
        List<OrdersEntity> ordersEntityList=new ArrayList<>();

        // payType 값에 따라 해당 Repository 메서드 호출
        if (type.equals(PayType.CENTER)) {
            log.info("센터 조회 시작: totalId {}", totalId);
            paymentEntityList = paymentRepository.findCenterForDate(totalId); // Center 결제 데이터 조회

        }else if (type.equals(PayType.BRANCH)) {
            log.info("지점 조회 시작: totalId {}", totalId);
            paymentEntityList = paymentRepository.findBranchForDate(totalId); // Branch 결제 데이터 조회
        }else if (type.equals(PayType.HOTEL)) {
            log.info("호텔 조회 시작: totalId {}", totalId);
            paymentEntityList = paymentRepository.findHotelForDate(totalId); // Hotel 결제 데이터 조회
        }else if (type.equals(PayType.STORE)) {
            log.info("가맹점 조회 시작: totalId {}", totalId);
            paymentEntityList = paymentRepository.findStoreForDate(totalId); // Store 결제 데이터 조회

            paymentEntityList.forEach(paymentEntity -> {
                paymentEntity.getOrdersEntityList().forEach(ordersEntity -> {
                    if (ordersEntity.getStoreEntity().getId().equals(totalId)) {
                        paymentEntityList2.add(PaymentEntity.builder()
                                .id(paymentEntity.getId()).orderType(paymentEntity.getOrderType()).
                                paidCheck(paymentEntity.getPaidCheck()).ordersEntityList(List.of(ordersEntity))
                                .build());
                    }
                });
            });
            log.info(ordersEntityList.size());


        }else {
            log.info("유효하지 않은 정산 타입입니다. type: {}", type);
            throw new IllegalArgumentException("유효하지 않은 정산 타입입니다.");
        }
*/ // 이거 지우면 안됨 무조건 지우면안됨 ㄹㅇ 지우면 안됨










}
