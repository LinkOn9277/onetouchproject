/*********************************************************************
 * 클래스명 : MembersDTO
 * 기능 :
 * 작성자 : 이동건
 * 작성일 : 2025-03-30
 * 수정 : 2025-03-30     이동건
 *********************************************************************/
package com.onetouch.delinight.DTO;

import com.onetouch.delinight.Constant.Role;
import com.onetouch.delinight.Constant.Status;
import com.onetouch.delinight.Entity.MembersEntity;
import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembersDTO {

    private Long id;
    private String password;

    private String name;

    private String email;

    private String phone;

    private Role role;
    private Status status;

    public MembersDTO(MembersEntity entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.email = entity.getEmail();
        this.phone = entity.getPhone();
        this.password = entity.getPassword();
        this.role = entity.getRole();
        this.status = entity.getStatus();
    }
}
