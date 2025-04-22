/*********************************************************************
 * 클래스명 : MembersController
 * 기능 :
 * 작성자 : 이효찬
 * 작성일 : 2025-03-30
 * 수정 : 2025-03-30     이효찬
 *********************************************************************/
package com.onetouch.delinight.Controller;

import com.onetouch.delinight.DTO.HotelDTO;
import com.onetouch.delinight.Entity.HotelEntity;
import com.onetouch.delinight.Repository.HotelRepository;
import com.onetouch.delinight.Service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members/hotel")
@Log4j2
public class HotelController {


    private final HotelService hotelService;
    private final HotelRepository hotelRepository;

    @GetMapping("/create")
    public String createView() {


        return "hotel/create";
    }

    @PostMapping("/create")
    public String createProc(HotelDTO hotelDTO) {
        hotelService.create(hotelDTO);

        return "redirect:/members/account/adminhome";
    }

    @GetMapping("/listA")
    public String listView(Model model) {
        List<HotelDTO> hotelDTOList =
                hotelService.list();

        model.addAttribute("hotelDTOList", hotelDTOList);
        return "hotel/listA";
    }

    @GetMapping("/read")
    public String readView(Principal principal, Model model) {


        log.info("principal log !!" + principal.getName());
        log.info("principal log !!" + principal.getName());
        log.info("principal log !!" + principal.getName());


        HotelEntity hotelEntity =
                hotelRepository.findByMembersEntity_Email(principal.getName());

        model.addAttribute("hotel", hotelEntity);

        return "hotel/read";
    }

    @GetMapping("/update")
    public String updateView(Principal principal, Model model) {

        HotelEntity hotelEntity =
                hotelRepository.findByMembersEntity_Email(principal.getName());

        model.addAttribute("hotel", hotelEntity);
        return "hotel/update";
    }

    @PostMapping("/update")
    public String updateProc(@ModelAttribute HotelDTO hotelDTO) {

        log.info("update post 페이지"+hotelDTO);
        log.info("update post 페이지"+hotelDTO);
        log.info("update post 페이지"+hotelDTO);
        log.info("update post 페이지"+hotelDTO);


        hotelService.update(hotelDTO);




        return "redirect:/members/hotel/read";
    }
}
