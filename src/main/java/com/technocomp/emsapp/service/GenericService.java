package com.technocomp.emsapp.service;

import com.technocomp.emsapp.domain.RandomCity;
import com.technocomp.emsapp.domain.User;

import java.util.List;

/**
 * Created by nydiarra on 06/05/17.
 */
public interface GenericService {
    User findByUsername(String username);

    List<User> findAllUsers();

    List<RandomCity> findAllRandomCities();
}
