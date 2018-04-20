/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.technocomp.emsapp.repository;

import com.technocomp.emsapp.domain.WhiteBoard;
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
public interface WhiteBoardRepository extends JpaRepository<WhiteBoard, Integer> {

    public List<WhiteBoard> findAll();

    @Query(value = "SELECT * FROM white_board  WHERE\n"
            + "ACOS( SIN( RADIANS( `latitude` ) ) * SIN( RADIANS( :lat ) ) + COS( RADIANS( `latitude` ) )\n"
            + "* COS( RADIANS( :lat )) * COS( RADIANS( `longitude` ) - RADIANS( :lng )) ) * 6380 < :maxRadius", nativeQuery = true)
    public List<WhiteBoard> findWhiteBoardByLatitudeAndLongitude(@Param("lat") String latitudefornotice, @Param("lng") String longitudefornotice,
            @Param("maxRadius") int maxRadius);

    public List<WhiteBoard> findWhiteBoardByEmail(String userdetails);

    @Query(value = "SELECT * FROM white_board    WHERE\n"
            + "( ACOS( SIN( RADIANS( `latitude` ) ) * SIN( RADIANS( :lat ) ) + COS( RADIANS( `latitude` ) )\n"
            + " * COS( RADIANS( :lat )) * COS( RADIANS( `longitude` ) - RADIANS( :lng)) ) * 6380 < :maxRadius )\n"
            + "AND email not like :userdetails And id  NOT IN (select item_id from enrole_notice where email like :userdetails)", nativeQuery = true)
    public List<WhiteBoard> findWhiteBoardByLatitudeAndLongitudeAndEmail(@Param("lat") String latitudefornotice, @Param("lng") String longitudefornotice,
            @Param("maxRadius") int maxRadius, @Param("userdetails") String userdetails);
}
