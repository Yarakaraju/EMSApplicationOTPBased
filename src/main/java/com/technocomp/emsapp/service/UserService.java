package com.technocomp.emsapp.service;

import com.technocomp.emsapp.domain.User;

/**
 * Created by Ravi Varma Yarakaraj on 12/28/2017.
 */
public interface UserService {

    public User findUserByEmail(String email);

    public void saveUser(User user);

    public User addUser(User user);

    /**
     *
     * @param id
     * @return
     */
    public Iterable<User> findNoticeEnrolledPeople(int id);

    public User findUserByMobile(String name);

    public User findUserByMobileAndPassword(String mobile, String otp);

    public void enableUser(User user);

    public void updateOTPPassword(User user);
}
