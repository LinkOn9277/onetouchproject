/*********************************************************************
 * 클래스명 : MembersController
 * 기능 :
 * 작성자 : 이동건
 * 작성일 : 2025-03-30
 * 수정 : 2025-03-30     이동건
 *********************************************************************/
package com.onetouch.delinight.Controller;

import com.onetouch.delinight.Constant.Menu;
import com.onetouch.delinight.DTO.MenuDTO;
import com.onetouch.delinight.Entity.MenuEntity;
import com.onetouch.delinight.Entity.StoreEntity;
import com.onetouch.delinight.Repository.MembersRepository;
import com.onetouch.delinight.Repository.MenuRepository;
import com.onetouch.delinight.Repository.StoreRepository;
import com.onetouch.delinight.Service.ImageService;
import com.onetouch.delinight.Service.MenuService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.lang.reflect.Member;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/menu")
public class MenuController {
    private final MenuService menuService;
    private final ImageService imageService;
    private final MenuRepository menuRepository;


    //등록get
    @GetMapping("/register")
    public String registerView(MenuDTO menuDTO){



        return "/admin/menu/register";
    }

    @PostMapping("/register")
    public String registerProc(MenuDTO menuDTO,Principal principal){
        log.info("등록 포스트 진입 : " + menuDTO);
        log.info("등록 포스트 진입 : " + menuDTO);

        String email = "hansin@a.a";

        menuService.register(menuDTO, email);
        log.info("저장된 데이터 : " + menuDTO);
        return "redirect:/menu/list";
    }

    @GetMapping("/list")
    public String listView(Model model, @PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,Principal principal){

        Page<MenuDTO> menuDTOList = menuService.menuList(pageable);
        log.info(menuDTOList.getPageable().getPageNumber());
        model.addAttribute("menuDTOList",menuDTOList);
        log.info(menuDTOList.getContent());

        return "admin/menu/list";
    }
    @GetMapping("/read")
    public String readView(@RequestParam Long id, Model model, Principal principal, RedirectAttributes redirectAttributes){
        MenuDTO menuDTO =
                menuService.read(id);
        model.addAttribute("menuDTO", menuDTO);

            return "/admin/menu/read";

    }
    @GetMapping("/update/{id}")
    public String updateView(@PathVariable(name = "id") Long id,Model model){
        log.info("수정" + id);
        MenuDTO menuDTO = menuService.read(id);
        model.addAttribute("menuDTO", menuDTO);
        log.info(menuDTO.getId());
        String imgUrl = imageService.read(id);
        model.addAttribute("imgUrl",imgUrl);
        return "/admin/menu/update";

    }
    @PostMapping("/update")
    public String updateprco(MenuDTO menuDTO){
        log.info(menuDTO);

        menuService.update(menuDTO);

        return "redirect:/menu/read?id="+menuDTO.getId();
    }








}
