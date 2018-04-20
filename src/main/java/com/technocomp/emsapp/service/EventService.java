package com.technocomp.emsapp.service;

import com.technocomp.emsapp.util.email.Email;
import com.technocomp.emsapp.util.email.EmailService;
import com.technocomp.emsapp.util.email.EmailTemplate;
import com.technocomp.emsapp.domain.Event;
import com.technocomp.emsapp.domain.EventsMaster;
import com.technocomp.emsapp.repository.EventRepository;
import com.technocomp.emsapp.repository.EventsMasterRepository;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    EventsMasterRepository eventsMasterRepository;

    @Autowired
    EmailService emailService;

    public void sendTextMail() {

        String from = "";
        String to = "";
        String subject = "Java Mail with Spring Boot - Plain Text";

        EmailTemplate template = new EmailTemplate("eventMailPlain.txt");

        Map<String, String> replacements = new HashMap<String, String>();
        replacements.put("user", "ravi");
        replacements.put("today", String.valueOf(new Date()));

        String message = template.getTemplate(replacements);

        Email email = new Email(from, to, subject, message);

        emailService.send(email);
    }

    public void sendHtmlMail(String from, String to, String subject) {

        EmailTemplate template = new EmailTemplate("eventMail.html");
        Map<String, String> replacements = new HashMap<String, String>();
        replacements.put("user", to);
        replacements.put("today", String.valueOf(new Date()));
        String message = template.getTemplate(replacements);
        Email email = new Email(from, to, subject, message);
        email.setHtml(true);
        emailService.send(email);
    }

    public Event addEvent(Event event) {
        eventRepository.save(event);
        return event;
    }
    
    public Event addEvent(Event event, String eventName) {
        event.setEventName(eventName);
        eventRepository.save(event);
        return event;
    }

    public Iterable<Event> getEvents(String email) {
        return eventRepository.findEventsByEmail(email);
    }

    public Event findEventByDate(Date dateOfEvent) {
        return eventRepository.findEventByDateOfEvent(dateOfEvent);
    }

    public boolean deleteEvent(Event event) {
        try {
            eventRepository.delete(event);
            return true;
        } catch (Exception e) {
            System.out.println(" Exception is : " + e);
            return false;
        }
    }

    public Event findEventByID(int eventNumber) {
        return eventRepository.findOne(eventNumber);
    }

    public Event findEventByDateAndEmail(Date dateOfEvent, String email) {
        return eventRepository.findEventByEmailAndDateOfEvent(email, dateOfEvent);
    }

    public Event findEventByDateAndEmailAndEventName(String eventName, Date dateOfEvent, String email) {
        return eventRepository.findEventByEventNameAndEmailAndDateOfEvent(eventName, email, dateOfEvent);
    }

    public EventsMaster findEventMasterByEventName(String eventName) {
        return eventsMasterRepository.findEventMasterByEventName(eventName);
    }

    public EventsMaster addEventMaster(EventsMaster eventsMaster) {
        return eventsMasterRepository.save(eventsMaster);
    }

    public List<EventsMaster> getEventsMaster() {
        return eventsMasterRepository.findAllByOrderByEventCategory();
    }

    public EventsMaster findEventMasterByEventNameAndCategory(String eventName, String eventCategory) {
        return eventsMasterRepository.findEventMasterByEventNameAndEventCategory(eventName, eventCategory);
    }

    public EventsMaster findEventeMasterByID(Integer id) {
        return eventsMasterRepository.findOne(id);
    }

    public boolean deleteEventMaster(EventsMaster eventsMaster) {
        try {
            eventsMasterRepository.delete(eventsMaster);
            return true;
        } catch (Exception e) {
            System.out.println(" Exception is : " + e);
            return false;
        }
    }
}
