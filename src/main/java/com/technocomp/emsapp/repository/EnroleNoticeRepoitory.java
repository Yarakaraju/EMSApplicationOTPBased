/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.technocomp.emsapp.repository;

import com.technocomp.emsapp.domain.EnroleNotice;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Ravi Varma Yarakaraj
 */
@Repository
public interface EnroleNoticeRepoitory extends JpaRepository<EnroleNotice, Integer> {

    public Iterable<EnroleNotice> findEnroleNoticeByItemId(Integer id);

    public EnroleNotice findEnroleNoticeByItemIdAndEmail(Integer id, String email);

    //public List<EnroleNotice> findEnroleNoticeByEmail(String userdetails);
    
    @Query(value = "SELECT * FROM enrole_notice where enrolled=true and email like :email", nativeQuery = true)
    public List<EnroleNotice> findEnroleNoticeByEmail(@Param("email") String email );

    @Query(value = "SELECT * FROM enrole_notice where enrolled=false and item_id in (select id from white_board"
            + " where email like :userdetails);", nativeQuery = true)
    public Iterable<EnroleNotice> findEnroleNoticeForApproval(@Param("userdetails") String userdetails);

}
