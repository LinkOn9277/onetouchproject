/*********************************************************************
 * 클래스명 : MembersController
 * 기능 :
 * 작성자 : 이효찬
 * 작성일 : 2025-03-30
 * 수정 : 2025-03-30     이효찬
 *********************************************************************/
package com.onetouch.delinight.Controller;

import com.onetouch.delinight.Constant.CheckInStatus;
import com.onetouch.delinight.DTO.CheckInDTO;
import com.onetouch.delinight.DTO.RoomDTO;
import com.onetouch.delinight.Entity.CheckInEntity;
import com.onetouch.delinight.Service.CheckInService;
import com.onetouch.delinight.Service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members/checkin")
@Log4j2
public class CheckInController {

    private final CheckInService checkInService;
    private final RoomService roomService;

    @GetMapping("/list")
    public String listView(Model model) {

        List<RoomDTO> roomDTOList =
                roomService.list();




        List<CheckInDTO> checkInDTOList =
                checkInService.list();

        log.info(checkInDTOList);
        log.info(checkInDTOList);


        model.addAttribute("roomDTOList", roomDTOList);
        model.addAttribute("checkInDTOList", checkInDTOList);


        return "checkin/list";

    }

    @GetMapping("/listB")
    public String listA(Model model, @RequestParam(value = "checkinstatus", required = false) String checkinstatus) {

        List<RoomDTO> roomDTOList =
                roomService.list();



        CheckInStatus checkInStatus;
        List<CheckInDTO> checkInDTOList = null;

        if (checkinstatus != null && !checkinstatus.isEmpty()) {
            if (checkinstatus.equals("VACANCY")) {
                checkInStatus = CheckInStatus.VACANCY;
                checkInDTOList =checkInService.getListCheckinByStatus(checkInStatus);


            } else if (checkinstatus.equals("CHECKIN")) {
                checkInStatus = CheckInStatus.CHECKIN;
                checkInDTOList = checkInService.getListCheckinByStatus(checkInStatus);
            }
        } else {
            checkInDTOList =
                checkInService.list2();

        }

        model.addAttribute("roomDTOList", roomDTOList);
        model.addAttribute("checkInDTOList", checkInDTOList);
        model.addAttribute("checkinstatus", checkinstatus);

        return "checkin/listB";

    }
}
