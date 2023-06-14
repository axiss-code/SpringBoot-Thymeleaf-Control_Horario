package com.ilerna.wr.dto;

import com.ilerna.wr.entity.User;
import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

/*
* DTO que copia un obj. user sin su password asociado y
* sin el prefijo ROLE_ que utiliza SpringSecurity, asociada al rol. 
*/
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements Comparable<UserDTO>{
    
    private Integer id;
    @NotBlank(message ="Campo nombre requerido.")
    private String name;
    @NotBlank(message ="Campo email requerido.")
    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Formato de email no válido.")
    private String email;
    private String role;
    private Boolean isActive;
    private String password;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate created;
    
    public static UserDTO getUserDTO(User u) {
        UserDTO usrDto = new UserDTO(u.getId().intValue(),
                                    u.getName(),
                                    u.getEmail(),
                                    u.getRole().toString().replace("ROLE_", ""),
                                    u.getIsActive(),
                                    null,
                                    u.getCreated());
        return usrDto;
    }    

    @Override
    public int compareTo(UserDTO o) {
        return getCreated().compareTo(o.getCreated());
    }
}
