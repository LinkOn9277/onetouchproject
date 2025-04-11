/*********************************************************************
 * 클래스명 : MembersController
 * 기능 :
 * 작성자 : 이동건
 * 작성일 : 2025-03-30
 * 수정 : 2025-03-30     이동건
 *********************************************************************/
package com.onetouch.delinight.Controller;

import com.onetouch.delinight.DTO.MenuDTO;
import com.onetouch.delinight.DTO.OrdersDTO;
import com.onetouch.delinight.Service.MenuService;
import com.onetouch.delinight.Service.OrdersService;
import com.onetouch.delinight.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/roomService")
public class RoomServiceController {
    private final MenuService menuService;
    private final PaymentService paymentService;
    private final OrdersService ordersService;

    @GetMapping("/order/main")
    public String main(Model model){
        Long hotelNum = 1L;// 체크인 서비스에서 findHotelNum 메소드 만들어서 findbyCheckInNum로 룸찾고 룸에서 호텔 찾아서 넘겨줄 예정
        List<MenuDTO> menuDTOList = menuService.menuListByHotel(1L);
        model.addAttribute("menuDTOList", menuDTOList);
        log.info(menuDTOList);
        return "roomService/order/main";
    }

    @GetMapping("/order/read")
    public String read(Long paymentId, Model model){
        List<OrdersDTO> ordersDTOList = ordersService.read(paymentId);
        boolean pendingCheck = ordersService.pendingCheck(paymentId);
        log.info(ordersDTOList);
        model.addAttribute("ordersDTOList", ordersDTOList);
        model.addAttribute("pendingCheck", pendingCheck);
        return "roomService/order/read";
    }




}
