package com.technocomp.emsapp.controller;

import com.technocomp.emsapp.domain.Group;
import com.technocomp.emsapp.service.GroupService;
import com.technocomp.emsapp.util.CustomErrorType;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Created by Ravi Varma Yarakaraj on 12/28/2017.
 */
@RestController(value = "/api/admin/")
public class GroupsController {

    @Autowired
    private GroupService groupService;

    public String getUserdetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = "";
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
            return currentUserName;
        }
        return currentUserName;
    }

    @RequestMapping(value = "/groups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Group> login(Principal principal) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Group group = groupService.findGroupsByEmail(auth.getName());
        Group group = new Group();
        return new ResponseEntity<Group>(
                group, HttpStatus.OK);
    }

    @RequestMapping(value = "/groupRegistration", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public ResponseEntity<?> createGroup(
            @RequestPart(value = "email", required = true) String email,
            @RequestPart(value = "name", required = true) String name,
            @RequestPart(value = "latitude", required = true) String latitude,
            @RequestPart(value = "longitude", required = true) String longitude,
            @RequestPart(value = "last_name", required = true) String last_name,
            @RequestPart(value = "description", required = true) String description,
            UriComponentsBuilder ucBuilder) {
        Group group = new Group();
        // if (groupService.findGroupByEmail(email) != null) {
        //     return new ResponseEntity(new CustomErrorType("Unable to create. A Group with email " + 
        //     email + " already exist."),HttpStatus.CONFLICT);
        // }
       
        groupService.saveGroup(group);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/group/{id}").buildAndExpand(group.getId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/subscribeToGroup", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public ResponseEntity<?> subscribeToGroup(
            @RequestPart(value = "email", required = true) String email,
            @RequestPart(value = "name", required = true) String name,
            @RequestPart(value = "mobile", required = true) String mobile,
            @RequestPart(value = "password", required = true) String password,
            @RequestPart(value = "latitude", required = true) String latitude,
            @RequestPart(value = "longitude", required = true) String longitude,
            @RequestPart(value = "last_name", required = true) String last_name,
            @RequestPart(value = "role", required = true) String role,
            UriComponentsBuilder ucBuilder) {
        Group group = new Group();
        if (groupService.findGroupByEmail(email) != null) {
            return new ResponseEntity(new CustomErrorType("Unable to create. A User with email "
                    + email + " already exist."), HttpStatus.CONFLICT);
        }
        
        groupService.saveGroup(group);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/group/{id}").buildAndExpand(group.getId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }
}
