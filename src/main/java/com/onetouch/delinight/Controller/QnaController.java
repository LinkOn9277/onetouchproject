package com.onetouch.delinight.Controller;

import com.onetouch.delinight.DTO.QnaDTO;
import com.onetouch.delinight.Repository.QnaRepository;
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
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/qna")
public class QnaController {
    private final QnaService qnaService;
    private final QnaRepository qnaRepository;

    //등록get
    @GetMapping("/checkinQna/register")
    public String register(QnaDTO qnaDTO, Model model){
        System.out.println("qnaDTO" + qnaDTO);
        model.addAttribute("qnaDTO" ,qnaDTO);

        return "/qna/checkinQna/register";
    }
    //등록post
    @PostMapping("/checkinQna/register")
    public String registerPost(QnaDTO qnaDTO,Principal principal){

        Long id = 12L;

        qnaService.register(qnaDTO,id);
        return "redirect:/qna/checkinQna/list";
    }
    //목록
    @GetMapping("/checkinQna/list")
    public String list(Model model){
        log.info("list진입");
        List<QnaDTO> qnaDTOList =
                qnaService.findAll();
        log.info(qnaDTOList);
        model.addAttribute("qnaDTOList",qnaDTOList);
        return "/qna/checkinQna/list";
    }



    //상세보기
    @GetMapping("/checkinQna/read")
    public String read(@RequestParam Long id, Model model, Principal principal, RedirectAttributes redirectAttributes){
        QnaDTO qnaDTO = qnaService.read(id);
        model.addAttribute("qnaDTO",qnaDTO);
        return "/qna/checkinQna/read";
    }
    //수정get
    @GetMapping("/checkinQna/update/{id}")
    public String update(@PathVariable(name = "id")Long id,Model model){
        log.info("수정" +id);
        QnaDTO qnaDTO = qnaService.read(id);
        model.addAttribute("qnaDTO",qnaDTO);
        return "/qna/checkinQna/update";
    }
    //수정post
    @PostMapping("/checkinQna/update")
    public String updatePost(QnaDTO qnaDTO){
        log.info(qnaDTO);
        qnaService.update(qnaDTO);
        return "redirect:/qna/checkinQna/read?id="+qnaDTO.getId();
    }
    //삭제
    @GetMapping("/checkinQna/delete")
    public String deleteGET(Long id){
        qnaService.delete(id);
        return "redirect:/qna/checkinQna/list";
    }

    @GetMapping("/checkinQna/test")
    public String TESTGET(){
        return "/qna/checkinQna/test";
    }

    @GetMapping("/hotelQna/list")
    public String listA(Model model){
        log.info("list진입");
        List<QnaDTO> qnaDTOList =
                qnaService.findAll();
        log.info(qnaDTOList);
        model.addAttribute("qnaDTOList",qnaDTOList);
        return "/qna/hotelQna/list";
    }









}
