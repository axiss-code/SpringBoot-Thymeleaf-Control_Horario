package com.ilerna.wr.controller.website;

import com.ilerna.wr.dto.RegistryDTO;
import com.ilerna.wr.dto.UserProfileDTO;
import com.ilerna.wr.entity.Registry;
import com.ilerna.wr.entity.User;
import com.ilerna.wr.service.IRegistryService;
import com.ilerna.wr.service.IUserService;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/*
* Controlador que maneja las vistas del rol Usuario
*/
@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private IUserService userService;
    
    @Autowired
    private IRegistryService registryService;
  
    @Autowired
    private BCryptPasswordEncoder passEncode;
    
    private HashMap<String, String> breadcrumbs;
    
    //Dirije a la vista de INICIO
    @GetMapping("")
    public String home (Model model, HttpSession session) {
        User u = userService.getById((Long)session.getAttribute("user_id"));
        Optional<Registry> lastEntry = registryService.getLastEntry(u.getId());
        //Alterna entre el botón ENTRAR / SALIR según tenga valor o no la hora de salida del último registro existente
        if (lastEntry.isPresent() && lastEntry.get().getTimeOut() == null) {
            model.addAttribute("working", RegistryDTO.formatTime(lastEntry.get().getTimeIn()));
        }
        List<Registry> r = registryService.getAllByUser(u.getId());
        //Devuelve a la vista los últimos 20 registros creados ordenados por fecha más reciente
        List<RegistryDTO> regs = r.stream().map(x ->RegistryDTO.getRegistryDTO(x))
                                .sorted(Comparator.comparing(i -> ((RegistryDTO) i).getDate())
                                        .thenComparing(Comparator.comparing(i -> ((RegistryDTO) i).getTimeIn()))
                                        .reversed())
                                .limit(30).collect(Collectors.toList());
        model.addAttribute("registries", regs);
        model.addAttribute("breadcrumbs", buildBreadcrumbs());
        return "user/index";
    }
    
    //Genera un registro de entrada con la hora del sistema
    @PostMapping("/enter")                 
    public String processEnter (Registry registry, BindingResult result, RedirectAttributes ra, HttpSession session, Model model) {
        User u = userService.getById((Long)session.getAttribute("user_id"));
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", result.getFieldError().getDefaultMessage());
            return "redirect:/user";
        }
        Optional<Registry> lastEntry = registryService.getLastEntry(u.getId());
        if (lastEntry.isPresent() && lastEntry.get().getTimeOut() == null) {
            ra.addFlashAttribute("error", "No se puede fichar nueva Entrada si haber completado la Salida.");
            model.addAttribute("working", lastEntry.get().getTimeIn());
            return "redirect:/user";
        }
        Registry r = new Registry();
        r.setDate(LocalDate.now());
        r.setTimeIn(LocalDateTime.now());
        r.setIsClosed(Boolean.FALSE);
        r.setUser(u);
        registryService.create(r);
        ra.addFlashAttribute("success", "Entrada registrada.");
        return "redirect:/user";
    }
    
    //Genera un registro de salida con la hora del sistema
    @PostMapping("/exit")                 
    public String processExit (Registry registry, BindingResult result, RedirectAttributes ra, HttpSession session, Model model) {
        User u = userService.getById((Long)session.getAttribute("user_id"));
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", result.getFieldError().getDefaultMessage());
            return "redirect:/user";
        }
        Optional<Registry> lastEntry = registryService.getLastEntry(u.getId());
        if (lastEntry.isPresent() && lastEntry.get().getTimeOut() != null) {
            ra.addFlashAttribute("error", "No se puede fichar Salida sin haber registrado una nueva Entrada.");
            return "redirect:/user";
        }
        Registry r = lastEntry.get();
        r.setTimeOut(LocalDateTime.now());
        registryService.update(r.getId(), r);
        ra.addFlashAttribute("success", "Salida registrada.");
        return "redirect:/user";
    }
    
    //Vista que permite cambiar la password
    @GetMapping("/settings")
    public String userSettings (Model model, HttpSession session) {
        User u = userService.getById((Long)session.getAttribute("user_id"));
        if (u.getEmail().equals("")) {
            return "redirect:/user";
        }
        UserProfileDTO upDto = UserProfileDTO.getUserProfileDTO(u);
        model.addAttribute("user_data", upDto);
        model.addAttribute("breadcrumbs", buildBreadcrumbs());
        return "user/settings";
    }
    
    //Procesa el cambio de password.
    @PostMapping("/settings")                 
    public String processSettings(@Valid UserProfileDTO upDto, BindingResult result, RedirectAttributes ra, HttpSession session, Model model) {
        User u = userService.getById((Long)session.getAttribute("user_id"));
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", result.getFieldError().getDefaultMessage());
            return "redirect:/user/settings";
        }
        if (!passEncode.matches(upDto.getPassword(),u.getPassword())) {
            ra.addFlashAttribute("error", "Password actual introducido no coincide.");
            return "redirect:/user/settings";
        }
        if (upDto.getNewPassword()== null || upDto.getNewPassword().equals("")) {
            ra.addFlashAttribute("error", "Password nuevo no puede estar vacío.");
            return "redirect:/user/settings";
        }
        u.setPassword(passEncode.encode(upDto.getNewPassword()));
        userService.update(u.getId(), u);
        ra.addFlashAttribute("success", "Password cambiado correctamente.");
        return "redirect:/user/settings";
    }
    
    //Dirije a la búsqueda de registros
    @GetMapping("/search")
    public String searchRegistry (Model model) {
        model.addAttribute("registry", new RegistryDTO());
        model.addAttribute("breadcrumbs", buildBreadcrumbs());
        return "user/search";
    }
    
    //Procesa una búsqueda por fecha
    @PostMapping("/search")
    public String searchRegistryByDate (RegistryDTO regDTO, Model model, HttpSession session) {
        User u = userService.getById((Long)session.getAttribute("user_id"));
        List<Registry> regList = registryService.findByDate(regDTO.getDate(), u.getId());
        List<RegistryDTO> rlDto = regList.stream().map(x ->RegistryDTO.getRegistryDTO(x)).collect(Collectors.toList());
        //Calcula el tiempo entre la entrada y la salida
        Duration rsum = regList.stream().map(x -> x.getTimeTotal()).reduce(Duration.ZERO, (partialSum, b) -> partialSum.plus(b));
        model.addAttribute("registry", regDTO);
        model.addAttribute("registry_list", rlDto);
        model.addAttribute("registry_sum", RegistryDTO.formatTime(rsum));
        model.addAttribute("breadcrumbs", buildBreadcrumbs());
        return "user/search";
    }
    
    //Visualiza los detalles de un registro por su id -> view?idx=nnn
    @GetMapping("/view")
    public String viewRegistry (@RequestParam(required = true)@Nullable Long idx, Model model, HttpSession session) {
        if (idx==null) {
            return "redirect:/user/search";
        }
        User u = userService.getById((Long)session.getAttribute("user_id"));
        Optional <Registry> reg = registryService.findById(idx);
        if (reg.isPresent() && (reg.get().getUser() == u)) {
                model.addAttribute("view_reg", reg.get());
                model.addAttribute("view_reg2", RegistryDTO.getRegistryDTO(reg.get()));
                model.addAttribute("registry_sum", RegistryDTO.formatTime(reg.get().getTimeTotal()));
                model.addAttribute("breadcrumbs", buildBreadcrumbs());
                session.setAttribute("temp", reg.get());
                return "user/view";
        }
        return "redirect:/user/search";
    }
    
    //Edita los datos de un registro
    @PostMapping("/update")                 
    public String updateRegistry (Registry reg, BindingResult result, HttpSession session, RedirectAttributes ra, Model model) {
        User u = userService.getById((Long)session.getAttribute("user_id"));
        Registry r = (Registry)session.getAttribute("temp");
        session.removeAttribute("temp");
       
        if (r.getIsClosed()) {
            ra.addFlashAttribute("error", "Imposible modificar un registro Cerrado.");
            return "redirect:/user/view?idx="+r.getId();
        }
        
        if (reg.getTimeOut().isBefore(reg.getTimeIn())) {
            ra.addFlashAttribute("error", "La Salida no puede ser anterior a la Entrada.");
            return "redirect:/user/view?idx="+r.getId();
        }
        
        if (r.getId().equals(reg.getId()) && u.getId().equals(r.getUser().getId())) {
            r.setDate(LocalDate.parse(reg.getTimeIn().toString().substring(0, 10), DateTimeFormatter.ISO_DATE));
            r.setTimeIn(reg.getTimeIn());
            r.setTimeOut(reg.getTimeOut());
            
            registryService.update(r.getId(), r);
            ra.addFlashAttribute("success", "Registro modificado.");
            return "redirect:/user/view?idx="+r.getId();
        }
        
        ra.addFlashAttribute("error", "No ha sido posible modificar el registro "+r.getId());
        return "redirect:/user";
    }
    
    //Borra un registro de la BD
    @PostMapping("/delete")                 
    public String deleteRegistry(RegistryDTO rDto, HttpSession session, RedirectAttributes ra) {
        User u = userService.getById((Long)session.getAttribute("user_id"));
        Registry r = (Registry)session.getAttribute("temp");
        session.removeAttribute("temp");
        if (r.getId().equals(rDto.getId()) && !r.getIsClosed() && u.getId().equals(r.getUser().getId())) {
            registryService.deleteById(rDto.getId());
            ra.addFlashAttribute("success", "Registro eliminado #"+rDto.getId());
            return "redirect:/user";
        }
        ra.addFlashAttribute("error", "No se ha podido eliminar el registro.");
        return "redirect:/user/search";
    }
    
    //Migas de pan
    private HashMap buildBreadcrumbs() {
        this.breadcrumbs = new HashMap<>();
        
        //user          - Inicio
        //  |_ settings - Perfil
        //  |_ search   - Buscar Registros
        //  |_ view     - Ver Registro
        breadcrumbs.put("/user","/user+Inicio");
        breadcrumbs.put("/user/settings","/user+Inicio+/user/settings+Perfil");
        breadcrumbs.put("/user/search","/user+Inicio+/user/search+Buscar Registros");
        breadcrumbs.put("/user/view","/user+Inicio+/user/view+Ver Registro");
        return breadcrumbs;
    }
    
}