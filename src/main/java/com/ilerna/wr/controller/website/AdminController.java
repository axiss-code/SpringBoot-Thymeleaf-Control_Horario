package com.ilerna.wr.controller.website;

import com.ilerna.wr.dto.ReportDTO;
import com.ilerna.wr.dto.UserDTO;
import com.ilerna.wr.entity.Area;
import com.ilerna.wr.entity.Registry;
import com.ilerna.wr.entity.Roles;
import com.ilerna.wr.entity.User;
import com.ilerna.wr.exception.ErrorMessage;
import com.ilerna.wr.service.IAreaService;
import com.ilerna.wr.service.IRegistryService;
import com.ilerna.wr.service.IUserService;
import java.util.Collections;
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
* Controlador que maneja las vistas del rol Administrador
*/
@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private IUserService userService;
    
    @Autowired
    private IAreaService areaService;
    
    @Autowired
    private IRegistryService registryService;
  
    @Autowired
    private BCryptPasswordEncoder passEncode;
    
    private HashMap<String, String> breadcrumbs;
    
    //Dirije a la vista de INICIO o DASHBOARD
    @GetMapping("")
    public String home(Model model) {
        List<User> u = userService.getAll();
        //Devuelve a la vista los últimos 15 usuarios creados ordenados por fecha más reciente
        List<UserDTO> users = u.stream().map(x ->UserDTO.getUserDTO(x)).sorted(Collections.reverseOrder()).limit(15).collect(Collectors.toList());
        model.addAttribute("users", users);
        model.addAttribute("breadcrumbs", buildBreadcrumbs());
        return "admin/index";
    }
    
    //Devuelve los datos al buscar por nombre de usuario -> search?name=xxx
    //o en todos los usuarios en caso de no especificarse ninguno
    @GetMapping("/search")
    public String searchUsers (@RequestParam(defaultValue = "", required = false) String name, Model model) {
        List<UserDTO> users;
        if (name == null || name.equals("")) {
            users = userService.getAll()
                    .stream()
                    .map(x ->UserDTO.getUserDTO(x))
                    .collect(Collectors.toList());
        } else {
            users = userService.getAll()
                    .stream()
                    .map(x ->UserDTO.getUserDTO(x))
                    .filter(n -> n.getName().toLowerCase().contains(name))
                    .collect(Collectors.toList());
        }
        model.addAttribute("search_users", users);
        model.addAttribute("breadcrumbs", buildBreadcrumbs());
        return "admin/search";
    }
    
    //Dirije al la vista de crear usuario
    @GetMapping("/create")
    public String createUser (Model model) {
       model.addAttribute("roles", Roles.getAll());
       model.addAttribute("breadcrumbs", buildBreadcrumbs());
       return "admin/create";
    }
    
    //Crea un usuario, que debe ser @Valid.
    //Si alguna restricción no se cumple, redirije a la vista de crear usuario
    @PostMapping("/create")                 
    public String processCreate(@Valid User u, BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", result.getFieldError().getDefaultMessage());
            return "redirect:/admin/create";
        }
        if(userService.checkEmail(u.getEmail()).isPresent()) {
            ra.addFlashAttribute("error", "Email existente");
            return "redirect:/admin/create";
        }
        u.setPassword(passEncode.encode(u.getPassword()));
        u.setIsActive(Boolean.TRUE);
        userService.create(u);
        ra.addFlashAttribute("success", "Usuario creado con éxito");
        return "redirect:/admin";
    }
    
    //Muestra los datos de un usuario por email view?email=xxxx
    //Si el email no existe o es null redirije a la visat de búsqueda de usuario
    @GetMapping("/view")
    public String viewUser (@RequestParam(required = false)@Nullable String email, Model model, HttpSession session) {
        if (email==null) {
            return "redirect:/admin/search";
        }
        Optional <User> u = userService.findByEmail(email);
        if (u.isPresent()) {
            UserDTO uDto = UserDTO.getUserDTO(u.get());
            model.addAttribute("view_user", uDto);
            model.addAttribute("roles", Roles.getAll());
            model.addAttribute("breadcrumbs", buildBreadcrumbs());
            session.setAttribute("temp", u.get());
            return "admin/view";
        } else {
            return "redirect:/admin/search";
        }
    }
    
    //Actualizará los datos de un usuario. Los datos recibidos deben ser @Valid.
    @PostMapping("/update")                 
    public String processUpdate(@Valid UserDTO uDto, BindingResult result, HttpSession session, Model model, RedirectAttributes ra) {
        User u = (User)session.getAttribute("temp");
        //Comprueba que no se haya enviado un id o fecha de creación distintos al del objeto en memoria de la sesión
        if ( (u.getId() != (uDto.getId().longValue())) || !(u.getCreated().isEqual(uDto.getCreated())) ) {
            ErrorMessage em = new ErrorMessage("400", "Bad Request", "Datos inválidos.", "/admin/view?email="+u.getEmail());
            model.addAttribute("errors", em);
            return "error";
        }
        //Si existen errores del tipo @Valid, devolvemos a la vista el detalle
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", result.getFieldError().getDefaultMessage());
            return "redirect:/admin/view?email="+u.getEmail();
        }
        //Si el email a cambiar ya existe devolvemos el error a la vista
        if (!u.getEmail().equals(uDto.getEmail())) {
            Optional<String> optEmail = userService.checkEmail(uDto.getEmail());
            if (optEmail.isPresent()) {
                ra.addFlashAttribute("error", "El email introducido ya existe en la BD.");
                return "redirect:/admin/view?email="+u.getEmail();
            } else {
                session.removeAttribute("temp");
            }
        }
        u.setEmail(uDto.getEmail());
        u.setName(uDto.getName());
        u.setRole(Roles.valueOf(uDto.getRole()));
        u.setIsActive(uDto.getIsActive());
        //Si no se cambia el password, se mantiene el mismo que ya tiene el objeto user.
        if (uDto.getPassword() != null) {
            u.setPassword(passEncode.encode(uDto.getPassword()));
        }
        userService.update(u.getId(), u);
        ra.addFlashAttribute("success", "Usuario actualizado correctamente.");
        return "redirect:/admin/view?email="+u.getEmail();
    }
    
    //Elimina un usuario de la BD
    @PostMapping("/delete")                 
    public String processDelete(UserDTO uDto, HttpSession session, Model model, RedirectAttributes ra) {
        User u = (User)session.getAttribute("temp");
        session.removeAttribute("temp");
        if (u.getId() != uDto.getId().longValue()) {
            ErrorMessage em = new ErrorMessage("400", "Bad Request", "Datos inválidos.", "/admin/view?email="+u.getEmail());
            model.addAttribute("errors", em);
            return "error";
        }
        userService.deleteById(u.getId());
        ra.addFlashAttribute("success", "Usuario borrado.");
        return "redirect:/admin";
    }
    
    //Envía las areas existentes a la vista
    @GetMapping("/area")
    public String manageAreas(Model model) {
        List<Area> a = areaService.getAll();
        model.addAttribute("areas", a);
        model.addAttribute("area_obj", new Area());
        model.addAttribute("breadcrumbs", buildBreadcrumbs());
        return "admin/area";
    }
    
    //Añade una nueva área
    @PostMapping("/add")
    public String addArea(Area area, BindingResult result, RedirectAttributes ra, Model model) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", result.getFieldError().getDefaultMessage());
            return "admin/area";
        }
        if (area.getName() == null || area.getName() == "") {
            ra.addFlashAttribute("error", "Área debe tener un nombre.");
            return "redirect:/admin/area";
        }
        if(areaService.findByName(area.getName()).isPresent()) {
            ra.addFlashAttribute("error", "Ya existe un área con ese nombre.");
            return "redirect:/admin/area";
        }
        areaService.create(area);
        ra.addFlashAttribute("success", "Area añadida.");
        return "redirect:/admin/area";
    }
    
    //Elimina un área
    @PostMapping("/remove")
    public String removeArea(Area area, BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", result.getFieldError().getDefaultMessage());
            return "admin/area";
        }
        areaService.deleteById(area.getId());
        ra.addFlashAttribute("success", "Area borrada.");
        return "redirect:/admin/area";
    }
    
    //Modifica un área existente
    @PostMapping("/edit")
    public String editArea(Area area, BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", result.getFieldError().getDefaultMessage());
            return "admin/area";
        }
        if(areaService.findByName(area.getName()).isPresent()) {
            ra.addFlashAttribute("error", "Ya existe un área con ese nombre.");
            return "redirect:/admin/area";
        }
        if (area.getName() == null || area.getName() == "") {
            ra.addFlashAttribute("error", "Área debe tener un nombre.");
            return "redirect:/admin/area";
        }
        areaService.update(area.getId(), area);
        ra.addFlashAttribute("success", "Area modificada.");
        return "redirect:/admin/area";
    }
    
    //Envía a la vista las áreas asociadas a un user. team?userid=nn
    @GetMapping("/team")
    public String selectedUserAreas (@RequestParam(required = false)@Nullable Long userid, Model model, HttpSession session) {
        if (userid==null) {
            return "redirect:/admin/search";
        }
        Optional <User> u = userService.findById(userid);
        if (u.isPresent()) {
            UserDTO uDto = UserDTO.getUserDTO(u.get());
            model.addAttribute("user_selected", uDto);
            model.addAttribute("areas", areaService.getAll()); //List<Area>
            model.addAttribute("user_areas", areaService.userAreas(userid)); //Set<String>
            model.addAttribute("area_upd", new Area());
            model.addAttribute("breadcrumbs", buildBreadcrumbs());
            session.setAttribute("temp", userid);
            return "admin/team";
        } else {
            return "redirect:/admin/search";
        }
    }
    
    //Asocia un área a usuario
    @PostMapping("/loadArea")                 
    public String addUserArea(Area area, BindingResult result, Model model, RedirectAttributes ra, HttpSession session) {
        Long usrid = (Long)session.getAttribute("temp");
        session.removeAttribute("temp");
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", result.getFieldError().getDefaultMessage());
            return "redirect:/admin/team?userid="+usrid;
        }
        boolean bok = areaService.associateUser(usrid, area.getId());
        if (bok){
            ra.addFlashAttribute("success", "Area añadida.");
        } else {
            ra.addFlashAttribute("error", "No se ha podido añadir.");
        }    
        return "redirect:/admin/team?userid="+usrid;
    }
    
    //Desasocia un area a un usuario
    @PostMapping("/unloadArea")                 
    public String removeUserArea(Area area, BindingResult result, Model model, RedirectAttributes ra, HttpSession session) {
        Long usrid = (Long)session.getAttribute("temp");
        session.removeAttribute("temp");
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", result.getFieldError().getDefaultMessage());
            return "redirect:/admin/team?userid="+usrid;
        }
        boolean bok = areaService.removeUser(usrid, area.getId());
        if (bok){
            ra.addFlashAttribute("success", "Area quitada.");
        } else {
            ra.addFlashAttribute("error", "No se ha podido quitar.");
        }    
        return "redirect:/admin/team?userid="+usrid;
    }
    
    //Devuelve a la lista todos los reportes y los cierra o abre por ref
    @GetMapping("/reports")
    public String proccessRegistries (@RequestParam(required = false)@Nullable Long ref, Model model) {
        //Cambia el estado de un registro. Si se cierra no puede ser editado por el user que lo creó
        if (ref!=null) {
            Optional<Registry> reg = registryService.findById(ref);
            if (reg.isPresent()) {
                Registry r = reg.get();
                if (r.getIsClosed()) { r.setIsClosed(Boolean.FALSE); }
                else { r.setIsClosed(Boolean.TRUE); }
                registryService.update(ref, r);
            }
        }
        List<Registry> regList = registryService.getAll();
        List<ReportDTO> rlDto = regList.stream().map(x ->ReportDTO.getReportDTO(x)).collect(Collectors.toList());
        model.addAttribute("registry_list", rlDto);
        model.addAttribute("breadcrumbs", buildBreadcrumbs());
        return "admin/reports";
    }
    
    //Genera un MAP con los endpoints y su nombre. Migas de pan.
    private HashMap buildBreadcrumbs() {
        this.breadcrumbs = new HashMap<>();
        //admin         - Dashboard
        //  |_ search   - Buscar/Editar
        //  |_ create   - Nuevo Usuario
        //  |_ view     - Editar Usuario
        //  |_ team     - Asociar Areas
        //  |_ area     - Areas
        //  |_ reports  - Registros
        breadcrumbs.put("/admin","/admin+Dashboard");
        breadcrumbs.put("/admin/search","/admin+Dashboard+/admin/search+Buscar/Editar");
        breadcrumbs.put("/admin/create","/admin+Dashboard+/admin/create+Crear usuario");
        breadcrumbs.put("/admin/view","/admin+Dashboard+/admin/view+Editar usuario");
        breadcrumbs.put("/admin/area","/admin+Dashboard+/admin/area+Areas");
        breadcrumbs.put("/admin/team","/admin+Dashboard+/admin/team+Asociar Areas");
        breadcrumbs.put("/admin/reports","/admin+Dashboard+/admin/reports+Registros");
        return breadcrumbs;
    }
    
}