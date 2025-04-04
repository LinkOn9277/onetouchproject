/*********************************************************************
 * 클래스명 : MemberServiceImpl
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-30
 * 수정 : 2025-03-30
 *********************************************************************/
package com.onetouch.delinight.Service;

import com.onetouch.delinight.Constant.Menu;
import com.onetouch.delinight.DTO.CartDTO;
import com.onetouch.delinight.DTO.CartItemDTO;
import com.onetouch.delinight.DTO.MenuDTO;
import com.onetouch.delinight.DTO.StoreDTO;
import com.onetouch.delinight.Entity.*;
import com.onetouch.delinight.Repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class CartServiceImpl implements CartService{
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuRepository menuRepository;
    private final ModelMapper modelMapper;
    private final OrdersRepository ordersRepository;
    private final OrdersItemRepository ordersItemRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public Long cartToOrder(Long cartNum) {


        PaymentEntity paymentEntity = new PaymentEntity();
        List<OrdersEntity> ordersEntityList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        PaymentEntity savedPaymentEntity = paymentRepository.save(paymentEntity);

        List<CartItemEntity> cartItemEntities = cartItemRepository.findByCartEntity_Id(cartNum);
        Map<StoreEntity, List<CartItemEntity>> groupedByStore = cartItemEntities.stream()
                .collect(Collectors.groupingBy(cartItem -> cartItem.getMenuEntity().getStoreEntity()));
        for(Map.Entry<StoreEntity, List<CartItemEntity>> data : groupedByStore.entrySet()){
            long totalPrice = 0L;
            StoreEntity store = data.getKey();
            List<CartItemEntity> cartItemEntities1 = data.getValue();

            OrdersEntity newOrder = new OrdersEntity();
            newOrder.setStoreEntity(store);

            OrdersEntity savedOrder = ordersRepository.save(newOrder);


            for (CartItemEntity cartItem : cartItemEntities1){
                totalPrice += cartItem.getMenuEntity().getPrice()*cartItem.getQuantity();
                OrdersItemEntity ordersItemEntity = OrdersItemEntity.builder()
                        .ordersEntity(savedOrder)
                        .menuEntity(cartItem.getMenuEntity())
                        .quantity(cartItem.getQuantity())
                        .build();
                ordersItemRepository.save(ordersItemEntity);
            }
            String formatted = LocalDateTime.now().format(formatter);
            savedOrder.setTotalPrice(totalPrice);
            savedOrder = ordersRepository.save(savedOrder);
            log.info(savedOrder);
            ordersEntityList.add(savedOrder);
        }

        savedPaymentEntity.setOrdersEntityList(ordersEntityList);
        savedPaymentEntity.setKey(formatter+savedPaymentEntity.getKey());
        paymentRepository.save(savedPaymentEntity);

        return savedPaymentEntity.getId();

    }

    @Override
    public String minusQuantity(Long cartItemNum) {
        Optional<CartItemEntity> optionalCartItemEntity = cartItemRepository.findById(cartItemNum);
        if(optionalCartItemEntity.isPresent()){
            CartItemEntity entity = optionalCartItemEntity.get();
            if(entity.getQuantity()==1){
                return "더 이상 수량을 줄일 수 없습니다.";
            }
            else {
                entity.setQuantity(entity.getQuantity() - 1);
                cartItemRepository.save(entity);
                return "수량을 줄였습니다.";
            }
        }
        else{
            return "오류 발생";
        }    }

    @Override
    public String plusQuantity(Long cartItemNum) {
        Optional<CartItemEntity> optionalCartItemEntity = cartItemRepository.findById(cartItemNum);
        if(optionalCartItemEntity.isPresent()){
            CartItemEntity entity = optionalCartItemEntity.get();
            entity.setQuantity(entity.getQuantity()+1);
            cartItemRepository.save(entity);
            return "수량을 늘렸습니다.";
        }
        else{
            return "오류 발생";
        }
    }

    @Override
    public void remove(Long cartItemNum) {
        cartItemRepository.deleteById(cartItemNum);
    }

    @Override
    public String clear(Long cartNum) {
        cartItemRepository.deleteByCartEntity_Id(cartNum);
        return "카트 비우기 성공";
    }

    @Override
    public List<CartItemDTO> list(Long cartNum) {
        List<CartItemEntity> entities = cartItemRepository.findByCartEntity_Id(cartNum);
        List<CartItemDTO> cartItemDTOList = entities.stream().map(data->modelMapper.map(data,CartItemDTO.class)
                .setCartDTO(modelMapper.map(data.getCartEntity(), CartDTO.class))
                .setMenuDTO(modelMapper.map(data.getMenuEntity(),MenuDTO.class).setStoreDTO(modelMapper.map(data.getMenuEntity().getStoreEntity(), StoreDTO.class))
                )).toList();
        return cartItemDTOList;
    }

    @Override
    public Integer add(Long cartNum, Long menuNum) {
        MenuEntity selectedMenu = menuRepository.findById(menuNum).get();
        CartEntity cartEntity = cartRepository.findById(cartNum).get();
        Optional<CartItemEntity> optionalCartItemEntity = cartItemRepository.findByCartEntity_IdAndMenuEntity_Id(cartEntity.getId(), selectedMenu.getId());
        if(optionalCartItemEntity.isPresent()){
            return 2;
        }
        else {
            CartItemEntity cartItemEntity = new CartItemEntity();
            cartItemEntity.setMenuEntity(selectedMenu);
            cartItemEntity.setCartEntity(cartEntity);
            cartItemEntity.setQuantity(1L);
            cartItemRepository.save(cartItemEntity);
            return 1;
        }
    }
}
