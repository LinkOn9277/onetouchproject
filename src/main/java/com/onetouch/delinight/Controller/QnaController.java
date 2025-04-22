package com.onetouch.delinight.Controller;

import com.onetouch.delinight.DTO.MembersDTO;
import com.onetouch.delinight.DTO.QnaDTO;
import com.onetouch.delinight.Entity.MembersEntity;
import com.onetouch.delinight.Repository.MembersRepository;
import com.onetouch.delinight.Repository.QnaRepository;
import com.onetouch.delinight.Service.MembersService;
import com.onetouch.delinight.Service.QnaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/members/qna")
public class QnaController {
    private final QnaService qnaService;
    private final QnaRepository qnaRepository;
    private final MembersService membersService;

    //등록get
    @GetMapping("/register")
    public String register(QnaDTO qnaDTO, Model model){
        System.out.println("qnaDTO" + qnaDTO);
        model.addAttribute("qnaDTO" ,qnaDTO);

        return "/qna/register";
    }
    //등록post
    @PostMapping("/register")
    public String registerPost(QnaDTO qnaDTO,Principal principal){
        if (principal == null) {
            // 익명 사용자의 경우 처리
            System.out.println("로그인 안 됨. 익명으로 처리");
        } else {
            String loginId = principal.getName();
            System.out.println("로그인 사용자: " + loginId);
        }
        Long id = 12L;
        qnaService.register(qnaDTO,id);
        return "redirect:/qna/list";
    }
    //목록
    @GetMapping("/list")
    public String list(@PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,Principal principal,
                       Model model){
        if (principal == null) {
            // 로그인 안 된 경우 로그인 페이지로 보내기
            return "redirect:/users/login";
        }
        //로그인한 사장님의 이메일로 Qna리스트 가져오기
        String email = principal.getName();
        Page<QnaDTO> qnaDTOList = qnaService.qnaList(pageable, principal.getName());
        if (qnaDTOList.getPageable().isPaged()){
            log.info("현재 페이지 번호 : {} ",qnaDTOList.getPageable().getPageNumber());
        }else {
            log.info("페이징 정보 없음");
        }

        if (qnaDTOList.isEmpty()){
            model.addAttribute("message", "등록된 Qna가 없습니다.");
        }
        model.addAttribute("qnaDTOList",qnaDTOList);
        log.info(qnaDTOList.getContent());

        return "qna/list";
    }




    //상세보기
    @GetMapping("/read")
    public String read(@RequestParam Long id, Model model, Principal principal, RedirectAttributes redirectAttributes){
        QnaDTO qnaDTO = qnaService.read(id);
        model.addAttribute("qnaDTO",qnaDTO);
        return "/qna/read";
    }
    //수정get
    @GetMapping("/update/{id}")
    public String update(@PathVariable(name = "id")Long id,Model model){
        log.info("수정" +id);
        QnaDTO qnaDTO = qnaService.read(id);
        model.addAttribute("qnaDTO",qnaDTO);
        return "/qna/update";
    }
    //수정post
    @PostMapping("/update")
    public String updatePost(QnaDTO qnaDTO){
        log.info(qnaDTO);
        qnaService.update(qnaDTO);
        return "redirect:/members/qna/read?id="+qnaDTO.getId();
    }
    //삭제
    @PostMapping("/delete")
    public String deleteGET(Long id){

        log.info("히히아이디" + id);
        qnaService.delete(id);
        return "redirect:/members/qna/list";
    }






}
