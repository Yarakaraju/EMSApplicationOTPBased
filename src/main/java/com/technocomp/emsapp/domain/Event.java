package com.technocomp.emsapp.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Temporal;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="event")
public class Event  {

    
    //static final DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    @Transient
    SimpleDateFormat dateFormat =  new SimpleDateFormat("dd.MM.yyyy hh:mm");
    

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "event_no")
    private int eventNo;
    
    @Column(name = "event_name")
    @NotEmpty(message = "*Please provide your name")
    private String eventName;
    
    @Column(name = "email")
    private String email;

    @Column(name = "guests_emails")
    private String guestsEmails;

    @Column(name = "co_host_name")
    @NotEmpty(message = "*Please provide your name")
    private String coHostName;
    
    @Column(name = "co_host_email")
    @Email(message = "*Please provide a valid Email")
    @NotEmpty(message = "*Please provide an email")
    private String coHostEmail;
     
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern="dd.MM.yyyy hh:mm")
    @Column(name = "date_of_event")
    @NotNull(message = "*Please provide a date & time")
    private Date dateOfEvent;
    
    @Column(name = "user_mobile")
    @NotEmpty(message = "*Please enter mobile number")
    private String mobile;
    
    @Column(name = "location")
    @NotEmpty(message = "*Please enter your event location")
    private String location;

    
    @Column(name = "latitude")
    @NotEmpty(message = "*Please allow your browser to get latitude")
    private String latitude;

    @Column(name = "longitude")
    @NotEmpty(message = "*Please allow your browser to get longitude")
    private String longitude;

    @Id
    public int getEventNo() {
        return eventNo;
    }

    public void setEventNo(int eventNo) {
        this.eventNo = eventNo;
    }
    
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

        public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
   
    public String getCoHostName() {
        return coHostName;
    }

    public void setCoHostName(String coHostName) {
        this.coHostName = coHostName;
    }

    public String getCoHostEmail() {
        return coHostEmail;
    }

    public void setCoHostEmail(String coHostEmail) {
        this.coHostEmail = coHostEmail;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Date getDateOfEvent() {
        return dateOfEvent;
    }

    @Transient
    public String getDateOfEventFormatted() {
        dateFormat.setLenient(true);
        return dateFormat.format(dateOfEvent);
    }

    public void setDateOfEvent(Date dateOfEvent) {
        this.dateOfEvent = dateOfEvent;
    }
    
    /*public Date getTimeOfEvent() {
        return timeOfEvent;
    }

    public void setTimeOfEvent(Date timeOfEvent) {
        this.timeOfEvent = timeOfEvent;
    }*/
      
    public String getGuestsEmails() {
        return guestsEmails;
    }

    public void setGuestsEmails(String guestsEmails) {
        this.guestsEmails = guestsEmails;
    }
 
}
