package com.ilerna.wr.dto;

import com.ilerna.wr.entity.Registry;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

/*
* DTO para la vista de registros
* Copia un obj. registro sin su obj. usuario asociado
*/
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistryDTO implements Comparable<RegistryDTO>{
    
    private Long id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime timeIn;
    
    //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime timeOut;
    private Duration timeTotal;
    private Boolean isClosed;
    
    /*
    * Métodos estáticos que facilitan crear copias y 
    * devolver en formato String las fechas y horas  
    */
    public static RegistryDTO getRegistryDTO(Registry r) {
        RegistryDTO rDto = new RegistryDTO(r.getId(),
                                            r.getDate(),
                                            r.getTimeIn(),
                                            r.getTimeOut(),
                                            r.getTimeTotal(),
                                            r.getIsClosed());
        return rDto;
    }
    
    public static String formatTime(LocalDateTime t) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return t.format(formatter);
    }
    
    public static String formatTime(Duration d) {
        //String.format("%d:%02d:%02d", d.toHours(), d.toMinutesPart(), d.toSecondsPart());
        return String.format("%d:%02d", d.toHours(), d.toMinutesPart());
    }
    
    @Override
    public int compareTo(RegistryDTO o) {
        return this.date.compareTo(o.date);
    }
    
    public String getTimeIn() {
        return this.timeIn == null ? null : formatTime(this.timeIn);
    }
    
    public String getTimeOut() {
        return this.timeOut == null ? null :  formatTime(this.timeOut);
    }
    
    public String getTimeTotal() {
        return this.timeTotal == null ? null : formatTime(this.timeTotal);
    }
    
}
