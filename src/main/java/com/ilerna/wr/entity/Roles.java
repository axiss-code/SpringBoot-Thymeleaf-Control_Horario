package com.ilerna.wr.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Roles implements Serializable{
    ROLE_ADMIN,
    ROLE_EDITOR,
    ROLE_USER;
    
    public static final HashMap<Roles,String> getAll() {
        HashMap<Roles,String> tmpMap = new HashMap<>();
        tmpMap.put(ROLE_ADMIN, "ADMIN");
        tmpMap.put(ROLE_EDITOR, "EDITOR");
        tmpMap.put(ROLE_USER, "USER");
        return tmpMap;
    }
    
    public static final HashMap<Roles,String> getNames() {
        HashMap<Roles,String> tmpMap = new HashMap<>();
        tmpMap.put(ROLE_ADMIN, "Administrador");
        tmpMap.put(ROLE_EDITOR, "Editor");
        tmpMap.put(ROLE_USER, "Usuario");
        return tmpMap;
    }
    
//    public static final List<String> getNamesOnly() {
//        List<String> tmpList = Stream.of(Roles.values()).map(Roles::name).collect(Collectors.toList());
//        tmpList.replaceAll(e -> e.replace("ROLE_", "")); 
//        return tmpList;
//    }
}


