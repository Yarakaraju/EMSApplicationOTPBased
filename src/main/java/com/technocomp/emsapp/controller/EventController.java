package com.technocomp.emsapp.controller;

import com.technocomp.emsapp.service.EventService;
import com.technocomp.emsapp.domain.Event;
import com.technocomp.emsapp.domain.EventsMaster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.validation.Valid;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * This RestController has the CRUD API Exposed to handle Events.
 * @author Ravi Varma Yarakaraju
 * @version 1.0 *
 */
@RestController
@RequestMapping("api")
public class EventController {

    @Autowired
    EventService eventService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy h:mm");
        sdf.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    public String getUserdetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = "";
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
            return currentUserName;
        }
        return currentUserName;
    }

    @RequestMapping(value = "/event", method = RequestMethod.GET)
    public ModelAndView event() {
        ModelAndView modelAndView = new ModelAndView();
        Event event = new Event();
        modelAndView.addObject("event", event);
        modelAndView.addObject("events", eventService.getEvents(getUserdetails()));
        modelAndView.addObject("eventsList", eventService.getEventsMaster());
        modelAndView.setViewName("event");
        return modelAndView;
    }

    @RequestMapping(value = "/listEvents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<Event>> getAllEvents() {
        return new ResponseEntity<Iterable<Event>>(
                eventService.getEvents(getUserdetails()), HttpStatus.OK);

    }

    @RequestMapping(value = "/eventMaster", method = RequestMethod.GET)
    public ModelAndView eventMaster() {
        ModelAndView modelAndView = new ModelAndView();
        EventsMaster eventsMaster = new EventsMaster();
        modelAndView.addObject("eventMaster", eventsMaster);
        modelAndView.addObject("eventsMaster", eventService.getEventsMaster());
        modelAndView.setViewName("eventMaster");
        return modelAndView;
    }

    @RequestMapping(value = "/createEvent", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createNewEvent(
            @RequestPart(value = "email", required = true) String email,
            @RequestPart(value = "eventName", required = true) String eventName,
            @RequestPart(value = "guestsEmails", required = true) String guestsEmails,
            @RequestPart(value = "mobile", required = true) String mobile,
            @RequestPart(value = "latitude", required = true) String latitude,
            @RequestPart(value = "longitude", required = true) String longitude,
            @RequestPart(value = "coHostName", required = true) String coHostName,
            @RequestPart(value = "coHostEmail", required = true) String coHostEmail,
            @RequestPart(value = "dateOfEvent", required = true) Date dateOfEvent,
            UriComponentsBuilder ucBuilder) {
        HttpHeaders headers = new HttpHeaders();
        Event eventExists = eventService.findEventByDateAndEmailAndEventName(eventName, dateOfEvent, getUserdetails());
        if (eventExists != null) {
            headers.set("message", "There is already an event registered with the same name on this date and time");
            return new ResponseEntity<>(eventExists, headers, HttpStatus.CONFLICT);
        } else {
            Event event = new Event();
            event.setEventName(eventName);
            event.setCoHostEmail(coHostEmail);
            event.setGuestsEmails(guestsEmails);
            event.setLatitude(latitude);
            event.setLongitude(longitude);
            event.setDateOfEvent(dateOfEvent);
            event.setEmail(getUserdetails());
            eventService.addEvent(event);
            eventService.sendHtmlMail(event.getEmail(), event.getGuestsEmails(), event.getEventName());
            headers.set("message", "Event has been registered successfully");
            return new ResponseEntity<>(event, headers, HttpStatus.CREATED);
        }

    }

    @RequestMapping(value = "/eventMaster", method = RequestMethod.POST)
    public ModelAndView eventMaster(@Valid EventsMaster eventsMaster, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        EventsMaster eventExists = eventService.findEventMasterByEventNameAndCategory(eventsMaster.getEventName(), eventsMaster.getEventCategory());
        if (eventExists != null) {
            bindingResult
                    .rejectValue("eventName", "error.eventMaster",
                            "There is already an event registered with the same name and category");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("eventMaster");
        } else {
            eventService.addEventMaster(eventsMaster);
            modelAndView.addObject("successMessage", "Event type has been registered successfully");
            modelAndView.addObject("eventMaster", new EventsMaster());
            modelAndView.setViewName("eventMaster");
            modelAndView.addObject("eventsMaster", eventService.getEventsMaster());
        }
        return modelAndView;
    }

    @RequestMapping(value = "/deleteEvent/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteEvent(@PathVariable Integer id) {
         Event event = eventService.findEventByID(id);
         HttpHeaders headers = new HttpHeaders();
        boolean delete = eventService.deleteEvent(event);
        if (delete) {
            event.setEmail(getUserdetails());
            headers.set("message", "Event has been deleted successfully");

        } else {
            headers.set("message", "Event deletion failed");
        }
       
        return new ResponseEntity<>(event, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/eventMaster/delete/{id}", method = RequestMethod.POST)
    public ModelAndView deleteEventMaster(@PathVariable Integer id) {
        ModelAndView modelAndView = new ModelAndView();
        EventsMaster eventsMaster = eventService.findEventeMasterByID(id);
        boolean delete = eventService.deleteEventMaster(eventsMaster);
        if (delete) {
            modelAndView.addObject("deleteMessage", "Event has been deleted successfully");
        } else {
            modelAndView.addObject("deleteMessage", "Event deletion failed");
        }
        modelAndView.addObject("eventMaster", eventsMaster);
        modelAndView.addObject("eventsMaster", eventService.getEventsMaster());
        modelAndView.setViewName("eventMaster");

        return modelAndView;
    }
}
