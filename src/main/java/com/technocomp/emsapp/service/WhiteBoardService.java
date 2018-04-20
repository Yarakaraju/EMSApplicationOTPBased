/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.technocomp.emsapp.service;

import com.technocomp.emsapp.domain.WhiteBoard;
import com.technocomp.emsapp.repository.WhiteBoardRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Ravi Varma Yarakaraj
 */
@Service
public class WhiteBoardService {

    @Autowired
    WhiteBoardRepository whiteBoardRepository;

    public List<WhiteBoard> getNotices() {
        return whiteBoardRepository.findAll();
    }

    public void addNotice(WhiteBoard whiteBoard) {
        whiteBoardRepository.save(whiteBoard);
    }

    public WhiteBoard findWhiteBoardByID(Integer id) {
        return whiteBoardRepository.getOne(id);
    }

    public boolean deleteWhiteBoard(WhiteBoard whiteBoard) {
        try {
            whiteBoardRepository.delete(whiteBoard);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<WhiteBoard> getNoticesByLocation(String latitudefornotice, String longitudefornotice, int maxRadius) {
        return whiteBoardRepository.findWhiteBoardByLatitudeAndLongitude(latitudefornotice, longitudefornotice, maxRadius);
    }

    public List<WhiteBoard> getNoticesByUserID(String userdetails) {
        return whiteBoardRepository.findWhiteBoardByEmail(userdetails);
    }

    public List<WhiteBoard>  getNoticesByLocation(String latitudefornotice, String longitudefornotice, int maxRadius, String userdetails) {
        return whiteBoardRepository.findWhiteBoardByLatitudeAndLongitudeAndEmail(latitudefornotice, longitudefornotice, maxRadius, userdetails);
    }

    public List<WhiteBoard> getNoticesByCity(String city) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<WhiteBoard> getNoticesByState(String state) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<WhiteBoard> getNoticesByCountry(String country) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
