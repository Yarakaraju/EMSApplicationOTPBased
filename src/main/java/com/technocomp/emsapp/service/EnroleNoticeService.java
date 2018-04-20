/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.technocomp.emsapp.service;

import com.technocomp.emsapp.domain.EnroleNotice;
import com.technocomp.emsapp.repository.EnroleNoticeRepoitory;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Ravi Varma Yarakaraj
 */
@Service
public class EnroleNoticeService {

    @Autowired
    EnroleNoticeRepoitory enroleNoticeRepoitory;
    
    
    public void save(EnroleNotice enroleNotice) {
        enroleNoticeRepoitory.save(enroleNotice);
    }

    public Iterable<EnroleNotice> findEnroleNoticeByItemId(Integer id) {
        return enroleNoticeRepoitory.findEnroleNoticeByItemId(id);
    }

    public EnroleNotice findEnroleNoticeByItemIdAndEmail(Integer id, String userdetails) {
        return enroleNoticeRepoitory.findEnroleNoticeByItemIdAndEmail(id, userdetails);
    }

    public List <EnroleNotice> findEnroleNoticeByEmail(String email) {
       return enroleNoticeRepoitory.findEnroleNoticeByEmail(email);
    }

    public Iterable<EnroleNotice> findEnroleNoticeForApproval(String userdetails) {
        return enroleNoticeRepoitory.findEnroleNoticeForApproval(userdetails);
    }

    public EnroleNotice findEnroleNoticeById(Integer id) {
        return enroleNoticeRepoitory.findOne(id);
    }

    public void delete(Integer id) {
        enroleNoticeRepoitory.delete(id);
    }

}
