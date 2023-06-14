package com.ilerna.wr.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ilerna.wr.dto.UserDTO;
import com.ilerna.wr.entity.User;
import com.ilerna.wr.service.IUserService;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserApiController {
    
    @Autowired
    private IUserService userService;
    
    //GET /api/v1/users 
    //Para una List de objetos User (id, email, password..)
    @GetMapping("")
    public ResponseEntity<?> getAll(){
        List<?> uList;
        uList = userService.getAll();
        return ResponseEntity.ok(uList);
    }
    
    //GET /api/v1/users/123
    //Devulve un usuario y todos sus campos
    @GetMapping("{id}")
    public ResponseEntity<?> getById (@PathVariable("id") @Valid Long id) {
    	return new ResponseEntity<User>(userService.getById(id), HttpStatus.OK);
    }
    
    // PUT - /api/v1/user/123
    // El RequestBody acepta un objeto User.
    @PutMapping("{id}")
    public ResponseEntity<?> update (@PathVariable("id") @Valid Long id, @RequestBody @Valid User user) {
    	Optional<User> optUser = userService.findById(id);
        if (optUser.isPresent()) {
            User u = userService.update(id, user);
            return new ResponseEntity<UserDTO>(UserDTO.getUserDTO(u), HttpStatus.OK);
    	} else {
            String msg ="{ \"status\": 404, \"message\": \"Elemento no encontrado: " + id + "\" }";
            JsonObject json = new Gson().fromJson(msg, JsonObject.class);
            return new ResponseEntity<String>(json.toString(), HttpStatus.NOT_FOUND);
    	}
    }
    
    //Delete /api/v1/user/123
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteId (@PathVariable("id") @Valid Long id) {
    	if (userService.findById(id).isPresent()) {
            userService.deleteById(id);
            String msg ="{ \"status\": 200, \"message\": \"Uusario borrado con éxito!\" }";
            JsonObject json = new Gson().fromJson(msg, JsonObject.class);
            return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	} else {
            String msg ="{ \"status\": 404, \"message\": \"Elemento no encontrado: " + id + "\" }";
            JsonObject json = new Gson().fromJson(msg, JsonObject.class);
            return new ResponseEntity<String>(json.toString(), HttpStatus.NOT_FOUND);
        }
    }
    
    //POST /api/v1/users
    @PostMapping()
    public ResponseEntity<?> create (@Valid @RequestBody User user) {
    	if (userService.findById(user.getId()).isPresent()) {
            String msg ="{ \"status\": 400, \"message\": \"Ya existe un usuario con el identificador: "+ user.getId()+"\" }";
            JsonObject json = new Gson().fromJson(msg, JsonObject.class);
            return new ResponseEntity<String>(json.toString(), HttpStatus.BAD_REQUEST);
    	} else {
            userService.create(user);
            UserDTO uDto = UserDTO.getUserDTO(user);
            return new ResponseEntity<UserDTO>(uDto, HttpStatus.CREATED);
    	}
    }
}
