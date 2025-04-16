/*********************************************************************
 * 클래스명 : MemberServiceImpl
 * 기능 :
 * 작성자 : 이효찬
 * 작성일 : 2025-03-30
 * 수정 : 2025-03-30
 *********************************************************************/
package com.onetouch.delinight.Service;

import com.onetouch.delinight.Constant.Role;
import com.onetouch.delinight.Constant.Status;
import com.onetouch.delinight.DTO.MembersDTO;
import com.onetouch.delinight.Entity.MembersEntity;
import com.onetouch.delinight.Repository.MembersRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class MembersServiceImpl implements MembersService{
    private final MembersRepository membersRepository;
    private final ModelMapper modelMapper;
   private final PasswordEncoder passwordEncoder;
   private final StoreService storeService;
   private final HotelService hotelService;

    @Override
    public void create(MembersDTO membersDTO) {

        MembersEntity membersEntity =
            modelMapper.map(membersDTO, MembersEntity.class);
        String password = passwordEncoder.encode(membersDTO.getPassword());
        membersEntity.setRole(Role.SUPERADMIN);
        membersEntity.setStatus(Status.WAIT);
        membersEntity.setPassword(password);

            membersRepository.save(membersEntity);


    }

    @Override
    public void hoteladcreate(MembersDTO membersDTO) {
        MembersEntity membersEntity =
                modelMapper.map(membersDTO, MembersEntity.class);
        membersEntity.setRole(Role.ADMIN);
        membersEntity.setStatus(Status.WAIT);

        membersRepository.save(membersEntity);
    }

    @Override
    public void storeadcreate(MembersDTO membersDTO) {
        MembersEntity membersEntity =
                modelMapper.map(membersDTO, MembersEntity.class);
        membersEntity.setRole(Role.STOREADMIN);
        membersEntity.setStatus(Status.WAIT);

        membersRepository.save(membersEntity);
    }

    @Override
    public List<MembersDTO> findAll() {
        List<MembersEntity> membersEntityList = membersRepository.findAll();

        List<MembersDTO> membersDTOList =
                membersEntityList.stream().toList().stream().map(
                        membersEntity -> modelMapper.map(membersEntity, MembersDTO.class)
                ).collect(Collectors.toList());

        return membersDTOList;
    }

    @Override
    public String login(String email, String password) {
        MembersEntity membersEntity = membersRepository.selectEmail(email);
        log.info("이메일로 조회한 db 회원정보 : "+membersEntity);



        if(membersEntity == null){
            log.info("db에 회원정보 없음");
            return "회원 정보가 없습니다.";
        }

        if(!membersEntity.getEmail().equals(password)){
            log.info("db에 회원정보는 있으나 비번이 틀림");
            return "비밀번호가 틀립니다.";
        }
        log.info("서비스 수행 완료");
        return null;
    }

    public List<MembersDTO> findSuper() {
        List<MembersEntity> membersEntityList = membersRepository.selectSuperAd();



//    @Override
//    public List<MembersDTO> findSuper() {
//        List<MembersEntity> membersEntityList = membersRepository.selectSuperAd();
//
//        List<MembersDTO> membersDTOList =
//        membersEntityList.stream().toList().stream().map(
//                membersEntity -> modelMapper.map(membersEntity, MembersDTO.class)
//        ).collect(Collectors.toList());
//
//        return membersDTOList;
//    }

    @Override
    public Page<MembersEntity> findHotelAd(Status status, int page) {


        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.membersRepository.selectHotelAdByStatus(status,pageable);

    }



    @Override
    public Page<MembersEntity> findStoreAd(Status status, int page) {


        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.membersRepository.selectStoreAd(pageable);

    }

    @Override
    public Page<MembersEntity> getList(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.membersRepository.selectSuperAd(pageable);
    }

    @Override
    public Page<MembersEntity> getListHotel(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.membersRepository.selectHotelAd(pageable);
    }

    @Override
    public Page<MembersEntity> getListStore(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.membersRepository.selectStoreAd(pageable);
    }

    @Override
    public Page<MembersEntity> getListBystatus(Status status, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.membersRepository.selectSuperAdByStatus(status,pageable);
    }

    @Override
    public MembersDTO approve(Long id) {

        MembersEntity membersEntity =
        membersRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        membersEntity.setStatus(Status.VALID);

        MembersDTO membersDTO =
            modelMapper.map(membersEntity, MembersDTO.class);
        return membersDTO;
    }

    @Override
    public MembersDTO Disapprove(Long id) {

        MembersEntity membersEntity =
                membersRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        membersEntity.setStatus(Status.NOTVALID);

        MembersDTO membersDTO =
                modelMapper.map(membersEntity, MembersDTO.class);
        return membersDTO;
    }

    @Override
    public Map<Role, Long> findRoleByEmail(String email) {

        MembersEntity membersEntity = membersRepository.findByEmail(email);
        if(membersEntity.getRole().equals(Role.STOREADMIN)){
            Long id = storeService.findStoreByEmail(email);
            return Map.of(Role.STOREADMIN, id);
        }
        else if(membersEntity.getRole().equals(Role.ADMIN)){
            Long id = hotelService.findHotelByEmail(email);
            return Map.of(Role.ADMIN, id);
        }


        return Map.of(null, 0L);
    }


}
