package com.tweetapp.tweet.service;

import java.util.List;
import java.util.Optional;

import com.tweetapp.tweet.constants.TweetAppConstants;
import com.tweetapp.tweet.dto.User;
import com.tweetapp.tweet.exception.UserException;
import com.tweetapp.tweet.repo.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

	@Autowired
	UserRepository userRepository;

	@Autowired
	MongoOperations mongoOperation;

	public String loginUser(final String userName, final String password) throws UserException {
		log.info("User Service loginUser() - Start");
        String validUser = "False";
        try{
            if (userName != null && password != null) {
                Optional<User> isValid = userRepository.findByEmailIdAndPassword(userName, password);
                validUser = isValid.isPresent() ? TweetAppConstants.USER_LOGIN_SUCCESS : TweetAppConstants.USER_LOGIN_FAILED;
            }
        } catch (Exception e) {
            throw new UserException(TweetAppConstants.USER_NOT_EXIST);
        }
        log.info("User Service loginUser() - End");
        return validUser;
	}

	public String registerUser(final User user) throws UserException {
		log.info("User Service registerUser() - Start");
        String register = TweetAppConstants.FAILED_STATUS;
        try {
            if (user!= null) {
                Optional<User> existUser = userRepository.findByUsername(user.getEmailId());
                register = existUser.isPresent() ? TweetAppConstants.USER_ALREADY_EXIST
                        : TweetAppConstants.USER_REGISTERED_SUCCESSFULLY;
                if (register.equals(TweetAppConstants.USER_REGISTERED_SUCCESSFULLY)) {
                    userRepository.save(user);
                }
            }    
        } catch (Exception e) {
           throw new UserException(TweetAppConstants.USER_ALREADY_EXIST);
        }
        log.info("User Service registerUser() - End");
        return register;
	}

	public String forgotPassword(final String userName, final String password) throws UserException {
		log.info("User Service forgotPassword() - Start");
        String response = TweetAppConstants.FAILED_STATUS;
        try {
            if (userName != null && password != null) {
                Optional<User> userPresent = userRepository.findByUsername(userName);
                if (!userPresent.isPresent()) {
                response = TweetAppConstants.USER_NOT_EXIST;
                return response;
                }
                final Query query = new Query();
                query.addCriteria(Criteria.where("emailId").is(userName));

                Update update = new Update();
                update.set("password", password);

                final User updateUser = mongoOperation.findAndModify(query, update, User.class);

                if (updateUser != null) {
                    response = TweetAppConstants.PASSWORD_UPDATE_SUCCESS;
                }
            }
        } catch (Exception e) {
            throw new UserException(TweetAppConstants.ERROR_UPDATING_PASSWORD);
        }
        log.info("User Service forgotPassword() - End");
        return response;
	}

	public List<User> getAllUsers() throws UserException {
		log.info("User Service getAllUsers() - Start");
        try {
			List<User> findAllUsers = userRepository.findAll();
			if (!findAllUsers.isEmpty()) {
                log.debug("All users {}", findAllUsers);
				return findAllUsers;
			}
		} catch (Exception e) {
			throw new UserException(TweetAppConstants.USERS_NOT_FOUND);
		}
        log.info("User Service getAllUsers() - End");
		return null;
	}

	public User searchUser(final String userName) throws UserException {
		log.info("User Service searchUser() - Start");
        User getUser = new User();
        try {
            if(userName != null) {
                Optional<User> userExist = userRepository.findByUsername(userName);
                if (userExist.isPresent()) {
                    getUser = userExist.get();
                }
            }
        } catch (Exception e) {
            throw new UserException(TweetAppConstants.USER_NOT_EXIST);
        }
        log.info("User Service searchUser() - End");
        return getUser;
	}
}
