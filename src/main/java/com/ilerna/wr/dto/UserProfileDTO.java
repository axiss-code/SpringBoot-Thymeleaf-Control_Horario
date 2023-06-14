package com.ilerna.wr.dto;

import com.ilerna.wr.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/*
* DTO que copia un obj. user sin password.
*/
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {
    
    private Integer id;
    private String name;
    private String email;
    private String password;
    private String newPassword;
    
    public static UserProfileDTO getUserProfileDTO(User u) {
        UserProfileDTO usrDto = new UserProfileDTO(u.getId().intValue(),
                                    u.getName(),
                                    u.getEmail(),
                                    null,
                                    null);
        return usrDto;
    }    

}
