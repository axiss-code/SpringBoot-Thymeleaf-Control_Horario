package com.ilerna.wr.configuration;

import com.ilerna.wr.entity.Area;
import com.ilerna.wr.entity.Registry;
import com.ilerna.wr.entity.Roles;
import com.ilerna.wr.entity.User;
import com.ilerna.wr.service.IAreaService;
import com.ilerna.wr.service.IRegistryService;
import com.ilerna.wr.service.IUserService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


/*
* Permite añadir datos de ejemplo a una nueva BD
*/

//@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    IUserService userService;
   
    @Autowired
    private BCryptPasswordEncoder passEncode;
    
    @Autowired
    IRegistryService registryService;
    
    @Autowired
    IAreaService areaService;
    
    @Override
    public void run(String... args) throws Exception {

        userService.create(User.builder()
            .name("administrador")
            .email("a@a.com")
            .password(this.passEncode.encode("123"))
            .role(Roles.ROLE_ADMIN)
            .isActive(Boolean.TRUE)
            .build()
        );
       
        userService.create(User.builder()
            .name("usuario0")
            .email("a@b.com")
            .password(this.passEncode.encode("123"))
            .role(Roles.ROLE_USER)
            .isActive(Boolean.TRUE)
            .build()
        );
        
        userService.create(User.builder()
            .name("editor0")
            .email("a@c.com")
            .password(this.passEncode.encode("123"))
            .role(Roles.ROLE_EDITOR)
            .isActive(Boolean.TRUE)
            .build()
        );
        
        registryService.create(Registry.builder()
            .date(LocalDate.parse("2023-03-22"))
            .timeIn(LocalDateTime.parse("2023-03-22T15:51:00"))
            .timeOut(LocalDateTime.parse("2023-03-22T17:53:00"))
            .isClosed(Boolean.FALSE)
            .user(userService.findByEmail("a@b.com").get())
            .build()
        );
        
        areaService.create(Area.builder()
            .name("Finanzas")
            .build()
        );
        
        areaService.associateUser(2L,1L);
        
    }
}
