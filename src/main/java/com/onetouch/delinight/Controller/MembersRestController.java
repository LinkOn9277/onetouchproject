package com.onetouch.delinight.Controller;

import com.onetouch.delinight.DTO.MembersDTO;
import com.onetouch.delinight.Service.MembersService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rest")
@Log4j2
public class MembersRestController {
    private final MembersService membersService;

    @GetMapping("/approveBtn/{id}")
    public void approveBtn(@PathVariable("id") Long id) {


        MembersDTO membersDTO =
            membersService.approve(id);
        log.info(membersDTO);
        log.info(membersDTO);
        log.info(membersDTO);
        log.info(membersDTO);


    }
    @GetMapping("/DisapproveBtn/{id}")
    public void DisapproveBtn(@PathVariable("id") Long id) {

        log.info("changestatus 페이지 진입!!");
        log.info("changestatus 페이지 진입!!");
        log.info("changestatus 페이지 진입!!");
        log.info(id);
        log.info(id);
        log.info(id);
        log.info(id);
        MembersDTO membersDTO =
                membersService.Disapprove(id);
        log.info(membersDTO);
        log.info(membersDTO);
        log.info(membersDTO);
        log.info(membersDTO);


    }
    @GetMapping("/searchMembers")
    public ResponseEntity<MembersDTO> searchMembers(@RequestParam(name = "email") String email){

        log.info(email);
        MembersDTO membersDTO = membersService.findByEmail(email);
        return ResponseEntity.ok(membersDTO);
    }






}
