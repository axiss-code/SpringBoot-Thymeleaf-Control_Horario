package com.ilerna.wr.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "registries")
public class Registry implements Serializable {
    
    private static final long serialVersionUID = 12020233L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "clock_in")
    private LocalDateTime timeIn;
    
    //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "clock_out")
    private LocalDateTime timeOut;
    
    @Transient
    @DateTimeFormat(pattern = "HH:mm")
    private Duration timeTotal;
    
    @Column(name = "closed", nullable = false)
    private Boolean isClosed;
    
    @ManyToOne
    @JsonBackReference
    private User user;
    
    public Duration getTimeTotal () {
        if (this.timeIn == null) {
            return null;
        } else if (this.timeOut == null) {
            return this.timeTotal = Duration.between(this.timeIn, LocalDateTime.now());
        } else {
            return this.timeTotal = Duration.between(this.timeIn, this.timeOut);
        }
    }
 
}
