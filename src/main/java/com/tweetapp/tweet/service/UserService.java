package com.tweetapp.tweet.service;

import java.util.List;

import com.tweetapp.tweet.dto.User;
import com.tweetapp.tweet.exception.UserException;

public interface UserService {

    public String registerUser(final User user) throws UserException;

    public String loginUser(final String emailId, final String password) throws UserException;

    public String forgotPassword(final String userName, final String password) throws UserException;

    public List<User> getAllUsers() throws UserException;
    
    public User searchUser(final String userName) throws UserException;

}
