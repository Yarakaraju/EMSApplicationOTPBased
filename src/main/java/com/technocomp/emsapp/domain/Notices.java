/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.technocomp.emsapp.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author Ravi Varma Yarakaraj
 */
@Entity
@Table(name="notices")
public class Notices  {
    
    @Transient
    SimpleDateFormat dateFormat =  new SimpleDateFormat("dd.MM.yyyy hh:mm");
    

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int Id;
    
    @Column(name = "latitudefornotice")
    @NotEmpty(message = "*Please select latitude/ longitude on map or eneter manually")
    private String latitudefornotice;

    @Column(name = "longitudefornotice")
    @NotEmpty(message = "*Please select latitude/ longitude on map or eneter manually")
    private String longitudefornotice;
    
    @Column(name = "maxRadius")
    @Range(min=1,max = 20, message = "minimum is one mile and max is 10 miles")
    private int maxRadius;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern="dd.MM.yyyy hh:mm")
    @Column(name = "dateAndTime")
    private Date dateAndTime;

    public Date getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(Date dateAndTime) {
        this.dateAndTime = dateAndTime;
    }
    
    public String getLatitudefornotice() {
        return latitudefornotice;
    }

    public void setLatitudefornotice(String latitudefornotice) {
        this.latitudefornotice = latitudefornotice;
    }

    public String getLongitudefornotice() {
        return longitudefornotice;
    }

    public void setLongitudefornotice(String longitudefornotice) {
        this.longitudefornotice = longitudefornotice;
    }

    public int getMaxRadius() {
        return maxRadius;
    }

    public void setMaxRadius(int maxRadius) {
        this.maxRadius = maxRadius;
    }
    
    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }
    
}
