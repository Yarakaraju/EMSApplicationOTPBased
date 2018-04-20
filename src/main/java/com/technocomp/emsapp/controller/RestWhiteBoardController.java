/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.technocomp.emsapp.controller;

import com.technocomp.emsapp.domain.WhiteBoard;
import com.technocomp.emsapp.service.EnroleNoticeService;
import com.technocomp.emsapp.service.MessagesService;
import com.technocomp.emsapp.service.UserService;
import com.technocomp.emsapp.service.WhiteBoardService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Ravi Varma Yarakaraj
 */

@RestController
@RequestMapping("/api")
public class RestWhiteBoardController {
    @Autowired
    WhiteBoardService whiteBoardService;

    @Autowired
    EnroleNoticeService enroleNoticeService;

    @Autowired
    UserService userService;

    @Autowired
    MessagesService messagesService;

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
    
    @RequestMapping(value = "/basicWhiteBoard", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<WhiteBoard> getBasicDetailsWhiteBoard() {
        return whiteBoardService.getNoticesByUserID(getUserdetails());
    }
    
    @RequestMapping(value = "/noticesNearByme", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<WhiteBoard>> getNoticesNearByMe( @RequestParam(value = "latitudefornotice") String latitudefornotice,
            @RequestParam(value = "longitudefornotice") String longitudefornotice,
            @RequestParam(value = "maxRadius") int maxRadius) {
          return new ResponseEntity<List<WhiteBoard>>( 
                whiteBoardService.getNoticesByLocation(latitudefornotice, longitudefornotice,
                        maxRadius, getUserdetails()),  HttpStatus.OK);
    }
    
   
    @PutMapping("/myWhiteBoard/createNotice")
    @PreAuthorize("hasAuthority('ADMIN')")
    public WhiteBoard createNewNotice(@Valid WhiteBoard whiteBoard) {
       
            whiteBoard.setEmail(getUserdetails());
            whiteBoardService.addNotice(whiteBoard);
           return whiteBoard;
    }
}
