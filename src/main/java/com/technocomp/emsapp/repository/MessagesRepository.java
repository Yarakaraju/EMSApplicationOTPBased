/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.technocomp.emsapp.repository;

import com.technocomp.emsapp.domain.Messages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Ravi Varma Yarakaraj
 */
public interface MessagesRepository extends JpaRepository <Messages, Integer>{

    public Iterable<Messages> findMessagesByMessageTo(String email);

    @Query(value = "SELECT * FROM messages  where message_to like :email and "
            + "message_type like :messageType", nativeQuery = true)
    public Iterable<Messages> findMessagesByMessageToAndType(@Param("email") String email, @Param("messageType") String messageType);

    @Query(value = "SELECT * FROM messages  where message_to like :email and "
            + "message_type like :messageType and message_from not like :email", nativeQuery = true)
    public Iterable<Messages> findMessagesByMessageToAndTypeIsGroup(@Param("email") String email, @Param("messageType") String messageType);

    @Query(value = "SELECT * FROM messages  where notice_id=:noticeId and "
            + "message_type like 'g'", nativeQuery = true)
    public Iterable<Messages> findGroupEmailsFromEnroleNoticeByNoticeId(@Param("noticeId") int noticeId);
        
}
