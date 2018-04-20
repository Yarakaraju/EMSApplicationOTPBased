package com.technocomp.emsapp.controller;

import com.technocomp.emsapp.domain.EnroleNotice;
import com.technocomp.emsapp.domain.Messages;
import com.technocomp.emsapp.domain.Notices;
import com.technocomp.emsapp.domain.WhiteBoard;
import com.technocomp.emsapp.service.EnroleNoticeService;
import com.technocomp.emsapp.service.MessagesService;
import com.technocomp.emsapp.service.UserService;
import com.technocomp.emsapp.service.WhiteBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@Controller
public class WhiteBoardController {

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

    @RequestMapping(value = "/noticesPostedByMe", method = RequestMethod.GET)
    public List<WhiteBoard> getBasicDetailsWhiteBoard() {
        return whiteBoardService.getNoticesByUserID(getUserdetails());
    }

    @RequestMapping(value = "/privateMessages", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<Messages>> getPrivateMessages(@RequestParam(value = "latitudefornotice") String latitudefornotice,
            @RequestParam(value = "longitudefornotice") String longitudefornotice,
            @RequestParam(value = "maxRadius") int maxRadius) {
        return new ResponseEntity<Iterable<Messages>>((messagesService.getMessages(getUserdetails(), "p")), HttpStatus.OK);
    }

    @RequestMapping(value = "/groupMessages", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<Messages>> getGroupMessages(@RequestParam(value = "latitudefornotice") String latitudefornotice,
            @RequestParam(value = "longitudefornotice") String longitudefornotice,
            @RequestParam(value = "maxRadius") int maxRadius) {
        return new ResponseEntity<Iterable<Messages>>((messagesService.getMessages(getUserdetails(), "g")), HttpStatus.OK);
    }

    @RequestMapping(value = "/registerNewNotice", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerNewNotice(
            @RequestPart(value = "itemTitle", required = true) String itemTitle,
            @RequestPart(value = "itemDescription", required = true) String itemDescription,
            @RequestPart(value = "mobile", required = true) String mobile,
            @RequestPart(value = "maxPratispents", required = true) int maxPratispents,
            @RequestPart(value = "latitude", required = true) String latitude,
            @RequestPart(value = "longitude", required = true) String longitude,
            @RequestPart(value = "location", required = true) String location,
            @RequestPart(value = "city", required = true) String city,
            @RequestPart(value = "state", required = true) String state,
            @RequestPart(value = "allowedRadius", required = true) String allowedRadius,
            UriComponentsBuilder ucBuilder) {

        WhiteBoard whiteBoard = new WhiteBoard();
        whiteBoard.setAllowedRadius(allowedRadius);
        whiteBoard.setCity(city);
        whiteBoard.setState(state);
        whiteBoard.setItemDescription(itemDescription);
        whiteBoard.setItemTitle(itemTitle);
        whiteBoard.setLatitude(latitude);
        whiteBoard.setLongitude(longitude);
        whiteBoard.setMaxPratispents(maxPratispents);
        whiteBoard.setEmail(getUserdetails());
        whiteBoard.setLocation(location);

        whiteBoardService.addNotice(whiteBoard);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/user/{id}").buildAndExpand(whiteBoard.getId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/deleteNotice", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteNotice(@PathVariable Integer id, UriComponentsBuilder ucBuilder) {
        WhiteBoard whiteBoard = whiteBoardService.findWhiteBoardByID(id);
        HttpHeaders headers = new HttpHeaders();

        headers.setLocation(ucBuilder.path("/user/{id}").buildAndExpand(whiteBoard.getId()).toUri());

        boolean delete = whiteBoardService.deleteWhiteBoard(whiteBoard);
        if (delete) {
            headers.set("message", "Notice has been deleted successfully");
        } else {
            headers.set("message", "Notice deletion failed");
        }

        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/noticesByTwoMiles", method = RequestMethod.POST)
    public ResponseEntity<?> listOfNoticesTwoMileRadius(
            @RequestParam(value = "latitudefornotice") String latitudefornotice,
            @RequestParam(value = "longitudefornotice") String longitudefornotice,
            @RequestParam(value = "maxRadius") int maxRadius) {
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<List<WhiteBoard>>(whiteBoardService.getNoticesByLocation(latitudefornotice, longitudefornotice, maxRadius, getUserdetails()), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/noticesByCity", method = RequestMethod.POST)
    public ResponseEntity<?> noticesByCity(
            @RequestParam(value = "city") String city) {
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<List<WhiteBoard>>(whiteBoardService.getNoticesByCity(city), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/noticesByState", method = RequestMethod.POST)
    public ResponseEntity<?> noticesByState(
            @RequestParam(value = "state") String state) {
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<List<WhiteBoard>>(whiteBoardService.getNoticesByState(state), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/noticesByCountry", method = RequestMethod.POST)
    public ResponseEntity<?> noticesByCountry(
            @RequestParam(value = "country") String country) {
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<List<WhiteBoard>>(whiteBoardService.getNoticesByCountry(country), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/findEnroleNoticeByEmail", method = RequestMethod.POST)
    public ResponseEntity<?> findEnroleNoticeByEmail() {
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<List<EnroleNotice>>(enroleNoticeService.findEnroleNoticeByEmail(getUserdetails()), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/findEnroleNoticeForApproval", method = RequestMethod.POST)
    public ResponseEntity<?> findEnroleNoticeForApproval() {
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<Iterable<EnroleNotice>>(enroleNoticeService.findEnroleNoticeForApproval(getUserdetails()), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/notices", method = RequestMethod.POST)
    public ModelAndView listOfNoticesToEnrole(HttpSession httpSession, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "latitudefornotice") String latitudefornotice,
            @RequestParam(value = "longitudefornotice") String longitudefornotice,
            @RequestParam(value = "maxRadius") int maxRadius) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("notices", new Notices());
        httpSession.setAttribute("latitudefornotice", latitudefornotice);
        httpSession.setAttribute("longitudefornotice", longitudefornotice);
        httpSession.setAttribute("maxRadius", maxRadius);
        modelAndView.addObject("noticesToApprove", enroleNoticeService.findEnroleNoticeForApproval(getUserdetails()));
        modelAndView.addObject("noticesAlreadyEnrolled",
                enroleNoticeService.findEnroleNoticeByEmail(getUserdetails()));
        modelAndView.addObject("noticesNearByLocation",
                whiteBoardService.getNoticesByLocation(latitudefornotice, longitudefornotice, maxRadius, getUserdetails()));
        modelAndView.setViewName("enroleNotices");

        return modelAndView;
    }

    @RequestMapping(value = "/listUsersDetails/{id}", method = RequestMethod.GET)
    public ModelAndView individualNoticeDetails(
            @PathVariable(value = "id") int id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("individualNoticeDetails", userService.findNoticeEnrolledPeople(id));
        modelAndView.setViewName("noticeDetails");
        return modelAndView;
    }

    @RequestMapping(value = "/enroleToNotices/enrole/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> enroleToNotices(@PathVariable Integer id) {
        HttpHeaders headers = new HttpHeaders();
        EnroleNotice enroleNotice = enroleNoticeService.findEnroleNoticeByItemIdAndEmail(id, getUserdetails());
        WhiteBoard whiteBoard = whiteBoardService.findWhiteBoardByID(id);
        if (whiteBoard.getTotalEnrolled() != whiteBoard.getMaxPratispents()) {
            if (enroleNotice == null) {
                enroleNotice = new EnroleNotice();
                enroleNotice.setItemId(whiteBoard.getId());
                enroleNotice.setItemTitle(whiteBoard.getItemTitle());
                enroleNotice.setItemDescription(whiteBoard.getItemDescription());
                enroleNotice.setEnrolled(false);
                enroleNotice.setEmail(getUserdetails());
                enroleNoticeService.save(enroleNotice);
                headers.set("message", "Notice has been enroled successfully waiting for approval by owner");
                return new ResponseEntity<>(whiteBoard, headers, HttpStatus.OK);
            } else {
                headers.set("message", "You already enrolled this event ");
                return new ResponseEntity<>(whiteBoard, headers, HttpStatus.CONFLICT);
            }
        } else {
            headers.set("message", "Max paricipeants reached! ");
            return new ResponseEntity<>(whiteBoard, headers, HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
        }
    }

    @RequestMapping(value = "/approveEnrolledNotice/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> approveEnrolledNotice(@PathVariable Integer id) {
        HttpHeaders headers = new HttpHeaders();
        EnroleNotice enroleNotice = enroleNoticeService.findEnroleNoticeById(id);
        WhiteBoard whiteBoard = whiteBoardService.findWhiteBoardByID(enroleNotice.getItemId());
        if (enroleNotice != null) {
            enroleNotice.setEnrolled(true);
            whiteBoard.setTotalEnrolled(whiteBoard.getTotalEnrolled() + 1);
            whiteBoardService.addNotice(whiteBoard);
            enroleNoticeService.save(enroleNotice);
            headers.set("message", "Notice has been approved successfully");
            return new ResponseEntity<>(whiteBoard, headers, HttpStatus.OK);
        } else {
            headers.set("message", "You already enrolled this Notice ");
            return new ResponseEntity<>(whiteBoard, headers, HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/enroleNotices/rejectWithPrivateMessage/{id}", method = RequestMethod.POST)
    public ModelAndView rejectEnrollNotice(@PathVariable Integer id) {
        ModelAndView modelAndView = new ModelAndView();
        EnroleNotice enroleNotice = enroleNoticeService.findEnroleNoticeById(id);
        modelAndView.addObject("enroleNotice", enroleNotice);
        modelAndView.setViewName("rejectEnrole");
        return modelAndView;
    }

    @RequestMapping(value = "/approvalRejected/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> rejectApprovalAndSendMessage(@PathVariable Integer id, 
            @RequestParam(value = "rejectMessage") String rejectMessage) {
        HttpHeaders headers = new HttpHeaders();
        
        EnroleNotice enroleNotice = enroleNoticeService.findEnroleNoticeById(id);
        Messages messages = new Messages();
        messages.setMessageFrom(getUserdetails());
        messages.setMessageType("r");
        messages.setMessageDescription(rejectMessage);
        messages.setMessageTo(enroleNotice.getEmail());
        messages.setNoticeId(enroleNotice.getItemId());
        messagesService.addMessage(messages);
        enroleNoticeService.delete(id);
        return new ResponseEntity<>(enroleNotice, headers, HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
    }

    @RequestMapping(value = "/enroleNotices/privateMessage/{id}", method = RequestMethod.POST)
    public ModelAndView enrollNoticePrivateMessage(@PathVariable Integer id) {
        ModelAndView modelAndView = new ModelAndView();
        WhiteBoard whiteBoard = whiteBoardService.findWhiteBoardByID(id);
        modelAndView.addObject("whiteBoard", whiteBoard);
        modelAndView.setViewName("privateMessage");
        return modelAndView;
    }

    @RequestMapping(value = "/enroleNotices/privateMessage/sendMessage/{id}", method = RequestMethod.POST)
    public ModelAndView enrollNoticeSendPrivateMessage(@PathVariable Integer id,
            @RequestParam(value = "privateMessage") String privateMessage) {
        ModelAndView modelAndView = new ModelAndView();
        WhiteBoard whiteBoard = whiteBoardService.findWhiteBoardByID(id);
        Messages messages = new Messages();
        messages.setMessageFrom(getUserdetails());
        messages.setMessageType("p");
        messages.setMessageDescription(privateMessage);
        messages.setMessageTo(whiteBoard.getEmail());
        messages.setNoticeId(id);
        messagesService.addMessage(messages);
        modelAndView.addObject("successMessage", "Message sent successfully");
        modelAndView.addObject("whiteBoard", whiteBoard);
        modelAndView.setViewName("privateMessage");
        return modelAndView;
    }

    @RequestMapping(value = "/myPrivateMessages/delete/{id}", method = RequestMethod.POST)
    public ModelAndView deletePrivateMessage(@PathVariable Integer id) {
        ModelAndView modelAndView = new ModelAndView();
        Messages messages = messagesService.findMessagesByID(id);
        boolean delete = messagesService.deleteMessages(messages);
        if (delete) {
            modelAndView.addObject("deleteMessage", "Message has been deleted successfully");
        } else {
            modelAndView.addObject("deleteMessage", "Message deletion failed");
        }
        modelAndView.addObject("myPrivateMessages", messagesService.getMessages(getUserdetails(), "p"));
        modelAndView.addObject("myGroupMessages", messagesService.getMessages(getUserdetails(), "g"));
        modelAndView.addObject("whiteBoard", new WhiteBoard());
        modelAndView.addObject("notices", new Notices());
        modelAndView.addObject("whiteBoardNotices", whiteBoardService.getNoticesByUserID(getUserdetails()));
        modelAndView.setViewName("myWhiteBoard");

        return modelAndView;
    }

    @RequestMapping(value = "/myPrivateMessages/reply/{id}", method = RequestMethod.POST)
    public ModelAndView replyPrivateMessage(@PathVariable Integer id) {
        ModelAndView modelAndView = new ModelAndView();
        Messages messages = messagesService.findMessagesByID(id);
        modelAndView.addObject("messages", messages);
        modelAndView.setViewName("replyMessage");

        return modelAndView;
    }

    @RequestMapping(value = "/myPrivateMessages/sendReply/{id}", method = RequestMethod.POST)
    public ModelAndView sendReplyToPrivateMessage(@PathVariable Integer id,
            @RequestParam(value = "replyPrivateMessage") String replyPrivateMessage) {
        ModelAndView modelAndView = new ModelAndView();
        Messages messages = messagesService.findMessagesByID(id);
        Messages replyMessage = new Messages();
        replyMessage.setMessageFrom(getUserdetails());
        replyMessage.setMessageType("rp");
        replyMessage.setMessageDescription(replyPrivateMessage);
        replyMessage.setMessageTo(messages.getMessageFrom());
        replyMessage.setNoticeId(messages.getNoticeId());
        messagesService.addMessage(replyMessage);

        modelAndView.addObject("replyMessage", "Message has been Replied successfully");

        modelAndView.addObject("myPrivateMessages", messagesService.getMessages(getUserdetails(), "p"));
        modelAndView.addObject("myGroupMessages", messagesService.getMessages(getUserdetails(), "g"));
        modelAndView.addObject("whiteBoard", new WhiteBoard());
        modelAndView.addObject("notices", new Notices());
        modelAndView.addObject("whiteBoardNotices", whiteBoardService.getNoticesByUserID(getUserdetails()));
        modelAndView.setViewName("myWhiteBoard");

        return modelAndView;
    }

    @RequestMapping(value = "/enroleNotices/sendGroupMessage/{id}", method = RequestMethod.POST)
    public ModelAndView groupMessage(@PathVariable Integer id) {
        ModelAndView modelAndView = new ModelAndView();
        WhiteBoard whiteBoard = whiteBoardService.findWhiteBoardByID(id);
        modelAndView.addObject("whiteBoard", whiteBoard);
        modelAndView.setViewName("groupMessage");
        return modelAndView;
    }

    @RequestMapping(value = "/enroleNotices/groupMessage/sendMessage/{id}", method = RequestMethod.POST)
    public ModelAndView sendGroupMessage(@PathVariable Integer id,
            @RequestParam(value = "groupMessage") String groupMessage) {
        ModelAndView modelAndView = new ModelAndView();
        Iterable<EnroleNotice> noticeEnrolled = enroleNoticeService.findEnroleNoticeByItemId(id);
        WhiteBoard whiteBoard = whiteBoardService.findWhiteBoardByID(id);
        Messages messages = new Messages();
        messages.setMessageFrom(getUserdetails());
        messages.setMessageType("g");
        messages.setMessageDescription(groupMessage);
        messages.setMessageTo(whiteBoard.getEmail());
        messages.setNoticeId(id);
        messagesService.addMessage(messages);
        for (EnroleNotice enroleNotice : noticeEnrolled) {
            messages = new Messages();
            messages.setMessageFrom(getUserdetails());
            messages.setMessageType("g");
            messages.setMessageDescription(groupMessage);
            messages.setMessageTo(enroleNotice.getEmail());
            messages.setNoticeId(id);
            messagesService.addMessage(messages);
        }
        modelAndView.addObject("successMessage", "Group Message sent successfully");
        modelAndView.addObject("whiteBoard", whiteBoard);
        modelAndView.setViewName("groupMessage");
        return modelAndView;
    }

    @RequestMapping(value = "/myGroupMessages/reply/{id}", method = RequestMethod.POST)
    public ModelAndView replyGroupMessage(@PathVariable Integer id) {
        ModelAndView modelAndView = new ModelAndView();
        Messages messages = messagesService.findMessagesByID(id);
        modelAndView.addObject("messages", messages);
        modelAndView.setViewName("replyGroupMessage");

        return modelAndView;
    }

    @RequestMapping(value = "/myGroupMessages/sendReply/{id}", method = RequestMethod.POST)
    public ModelAndView sendReplyToGroupMessage(@PathVariable Integer id,
            @RequestParam(value = "replyGroupMessage") String replyGroupMessage) {
        ModelAndView modelAndView = new ModelAndView();
        Messages messages = messagesService.findMessagesByID(id);
        Iterable<Messages> noticeEnrolled = messagesService.findGroupEmailsFromEnroleNoticeByNoticeId(messages.getNoticeId());
        for (Messages messages1 : noticeEnrolled) {
            Messages replyMessage = new Messages();
            replyMessage.setMessageFrom(getUserdetails());
            replyMessage.setMessageType("rg");
            replyMessage.setMessageDescription(replyGroupMessage);
            replyMessage.setMessageTo(messages1.getMessageTo());
            replyMessage.setNoticeId(messages.getNoticeId());
            messagesService.addMessage(replyMessage);
        }
        modelAndView.addObject("replyGroupMessage", "Group Message has been Replied successfully");
        modelAndView.addObject("myPrivateMessages", messagesService.getMessages(getUserdetails(), "p"));
        modelAndView.addObject("myGroupMessages", messagesService.getMessages(getUserdetails(), "g"));
        modelAndView.addObject("whiteBoard", new WhiteBoard());
        modelAndView.addObject("notices", new Notices());
        modelAndView.addObject("whiteBoardNotices", whiteBoardService.getNoticesByUserID(getUserdetails()));
        modelAndView.setViewName("myWhiteBoard");
        return modelAndView;
    }
}
