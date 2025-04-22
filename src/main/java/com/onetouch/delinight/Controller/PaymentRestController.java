package com.onetouch.delinight.Controller;

import com.onetouch.delinight.DTO.PaymentDTO;
import com.onetouch.delinight.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentRestController {

    private final PaymentService paymentService;

    @GetMapping("/allData")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByCriteria(@RequestParam(required = false) String priceMonth, @RequestParam(required = false) Long storeId, @RequestParam(required = false) Boolean isPaid) {

        try {
            // 서비스 호출
            List<PaymentDTO> paymentDTOList = paymentService.paymentByCriteria(priceMonth, storeId, isPaid);

            // 결과 반환
            if (paymentDTOList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // 데이터가 없으면 204 No Content
            }
            return new ResponseEntity<>(paymentDTOList, HttpStatus.OK);  // 결과가 있으면 200 OK

        } catch (Exception e) {
            // 에러 발생 시 처리 (500 서버 오류)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
