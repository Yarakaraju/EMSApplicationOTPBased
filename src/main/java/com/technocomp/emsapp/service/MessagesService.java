/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.technocomp.emsapp.service;

import com.technocomp.emsapp.domain.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.technocomp.emsapp.repository.MessagesRepository;

/**
 *
 * @author Ravi Varma Yarakaraj
 */
@Service
public class MessagesService {

    @Autowired
    MessagesRepository messagesRepository;

    public void addMessage(Messages messages) {
        messagesRepository.save(messages);
    }

    public Iterable<Messages> getMessages(String email, String messageType) {
        if (messageType.equalsIgnoreCase("p")) {
            return messagesRepository.findMessagesByMessageToAndType(email, messageType);
        } else {
            return messagesRepository.findMessagesByMessageToAndTypeIsGroup(email, messageType);
        }
    }

    public boolean deleteMessages(Messages messages) {
        try {
            messagesRepository.delete(messages);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Messages findMessagesByID(Integer id) {
        return messagesRepository.getOne(id);
    }

    public boolean replyMessages(Messages messages) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Iterable<Messages> findGroupEmailsFromEnroleNoticeByNoticeId(int noticeId) {
        return messagesRepository.findGroupEmailsFromEnroleNoticeByNoticeId(noticeId);
    }
}
