package com.technocomp.emsapp.controller;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.technocomp.emsapp.domain.SMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.technocomp.emsapp.domain.User;
import com.technocomp.emsapp.service.UserService;
import com.technocomp.emsapp.util.Handler;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * This RestController has the CRUD API Exposed to handle User Management.
 * @author Ravi Varma Yarakaraju
 * @version 1.0 *
 */

@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    Handler smsSenderService;

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

    public String getSessionToken() {
        SecurityContext contextToken = SecurityContextHolder.getContext();
        return contextToken.getAuthentication().getPrincipal().toString();
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> login(Principal principal) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByMobile(auth.getName());
        return new ResponseEntity<User>(
                user, HttpStatus.OK);
    }

    @RequestMapping(value = "/verifyOTP", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> verifyOTP(@RequestPart(value = "mobile", required = true) String mobile,
            @RequestPart(value = "otp", required = true) String otp, UriComponentsBuilder ucBuilder) {
         User user = userService.findUserByMobileAndPassword(mobile, otp);
        Map<String, Object> input = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();

       // headers.setLocation(ucBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri());
        System.out.println("OTP in VerifyOTP is " + otp);
        if (user == null) {
            headers.set("message", "Mobiile Number Not Found");
            return new ResponseEntity<>(user, headers, HttpStatus.NON_AUTHORITATIVE_INFORMATION);
        } else {
            user.setActive((short) 1);
            userService.enableUser(user);
            String token = getToken(user.getUsername(), otp);
            headers.set("access-token", token);
            user.setToken(token);
            userService.saveUser(user);
            SMS sms = new SMS();
            input.put("email", user.getEmail());
            sms.setSmsFrom("From EMS APP");
            sms.setSmsTo(mobile);
            sms.setMessage("Your Mobile verified");
            input.put("body", sms);
            smsSenderService.handleRequest(input);
            headers.set("message", "Mobile verified ");
            return new ResponseEntity<>(user, headers, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/sendOTP", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity sendOTPForLogin(@RequestPart(value = "mobile", required = true) String mobile, UriComponentsBuilder ucBuilder) {
        User user = userService.findUserByMobile(mobile);
        Map<String, Object> input = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        String otp = generateOTP();

        headers.setLocation(ucBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri());
        if (!user.equals(null) && user.getActive() == (short) 1) {
            SMS sms = new SMS();
            sms.setSmsFrom("From EMS APP");
            sms.setSmsTo(mobile);
            sms.setMessage("Your OTP for login : " + otp);
            input.put("body", sms);
            input.put("email", user.getEmail());
            smsSenderService.handleRequest(input);
            user.setPassword(otp);
            userService.updateOTPPassword(user);
            String token = getToken(mobile, otp);
            user.setToken(token);
            userService.saveUser(user);
            headers.set("access-token", token);
            headers.set("message", " OTP sent to mobile ");
            return new ResponseEntity<>(user, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(user, headers, HttpStatus.NON_AUTHORITATIVE_INFORMATION);
        }
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@RequestPart(value = "email", required = true) String email,
            @RequestPart(value = "first_name", required = true) String name,
            @RequestPart(value = "username", required = true) String username,
            @RequestPart(value = "mobile", required = true) String mobile,
            @RequestPart(value = "latitude", required = true) String latitude,
            @RequestPart(value = "longitude", required = true) String longitude,
            @RequestPart(value = "last_name", required = true) String last_name,
            UriComponentsBuilder ucBuilder) {
        HttpHeaders headers = new HttpHeaders();
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        User user = new User();
        SMS sms = new SMS();
        String otp = generateOTP();
        if (userService.findUserByMobile(mobile) != null) {
            headers.setLocation(ucBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri());
            headers.set("message", " A User with mobile already exists");
            return new ResponseEntity<>(user, headers, HttpStatus.CONFLICT);
        }
        Map<String, Object> input = new HashMap<>();
        sms.setSmsFrom("From EMS APP Registration");
        sms.setSmsTo(mobile);
        sms.setMessage("OTP for EMS App registration : " + otp);
        input.put("body", sms);
        input.put("email", email);
        smsSenderService.handleRequest(input);
         user.setFirstName(name);
        user.setEmail(email);
        user.setLastName(last_name);
        user.setUsername(username);
        user.setLatitude(latitude);
        user.setLongitude(longitude);
        user.setMobile(mobile);
        user.setPassword(otp);
        user.setDateAndTime(date);
        userService.saveUser(user);

        headers.setLocation(ucBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri());
        headers.set("message", " A User with mobile "
                + mobile + " registered successfully. check for OTP sent to your mobile and email. ");

        return new ResponseEntity<>(user, headers, HttpStatus.CREATED);
    }

    /**
     * Method for Generate OTP String
     *
     * @return
     */
    public String generateOTP() {
        Random randInt = new Random();
         int rand = 14237536 + randInt.nextInt(85662999);
        String otp = String.valueOf(rand);
        return otp;
    }

    public String getToken(String userName, String otp) {
        String token = "";
        try {
            System.out.println(" Inside getToken try block " + userName + "  :  " + otp);
            HttpResponse<JsonNode> httpResponse = Unirest.post("http://emsappclient:XY7kmzoNzl100@localhost:8080/oauth/token")
                    .header("Cache-Control", "no-cache")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .body("grant_type=password&username=" + userName + "&password=" + otp)
                    .asJson();
            System.out.println(" Respose " + httpResponse.toString());
            //httpResponse.toString();
            System.out.println(" Respose " + httpResponse.getBody().getObject());
            token = httpResponse.getBody().getObject().getString("access_token");
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return token;
    }
}
