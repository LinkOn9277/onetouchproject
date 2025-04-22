package com.onetouch.delinight.Controller;

import com.onetouch.delinight.Constant.PayType;
import com.onetouch.delinight.DTO.PaymentDTO;
import com.onetouch.delinight.DTO.TotalPriceDTO;
import com.onetouch.delinight.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentRestController {

    private final PaymentService paymentService;

    @GetMapping("/allDate")
    public ResponseEntity <List<PaymentDTO>> allDate (@RequestParam("totalId")Long totalId, @RequestParam("type")PayType type){
        log.info("요청 받은 정산 : totalId = {}, type = {}", totalId, type);

        try {
            // Service 호출
            List<PaymentDTO> paymentDTOList = paymentService.findAllDate(totalId, type);

            // 데이터가 있다면
            if (paymentDTOList != null && !paymentDTOList.isEmpty()){
                log.info("정산 데이터 조회 완료!! 결과 {}", paymentDTOList.size());
                return new ResponseEntity<>(paymentDTOList, HttpStatus.OK);
            }else { // 데이터가 없다면
                log.info("정산 데이터가 없습니다.");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        }catch (IllegalArgumentException e){
            // 타입이 유효하지 않으면
            log.info("유효하지 않은 정산 타입 {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            // 그 외 오류
            log.info("정산 데이터 조회 중 오류 발생 {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/centerDate/center/{centerId}")
    public ResponseEntity<TotalPriceDTO> centerDate(@PathVariable Long centerId){
        log.info("{}", centerId);
        TotalPriceDTO totalPriceDTO = paymentService.settlementCenter(centerId);
        return ResponseEntity.ok(totalPriceDTO);
    }

}
