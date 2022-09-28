package com.tweetapp.tweet.controller;

import com.tweetapp.tweet.dto.User;
import com.tweetapp.tweet.dto.ResponseDto;
import com.tweetapp.tweet.exception.UserException;
import com.tweetapp.tweet.service.UserService;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Generated;
import lombok.extern.slf4j.Slf4j;

@RequestMapping(value = "/api/v1.0/tweets")
@CrossOrigin(origins="https://tweetappfrontendvishal.azurewebsites.net")
@RestController
@Slf4j
@Generated
public class UserController {

	@Autowired
	UserService userService;

	@PostMapping(value = "/register", produces= MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseDto> registerUser(@RequestBody @Valid final User user) throws UserException {
		log.info("New User to Register is {} ", user.getFirstName());
		final String register = userService.registerUser(user);
		ResponseDto response = new ResponseDto();
		response.setResponse(register);
        return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping(value = "/login")
	public ResponseEntity<String> loginUser(@RequestParam("emailId") final String emailId,
			@RequestParam("password") final String password) throws UserException {
		log.info("User to login {} {}", emailId, password);
		final String login = userService.loginUser(emailId, password);
        return new ResponseEntity<>(login, HttpStatus.OK);
	}

	@GetMapping(value = "/forgot")
	public ResponseEntity<String> forgotPassword(@RequestParam("userName") final String userName,
			@RequestParam("password") final String password) throws UserException {
		log.info("Calling forgot password for user id ", userName);
		final String response = userService.forgotPassword(userName, password);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping(value = "/users/all")
	public ResponseEntity<List<User>> getAllUsers() throws UserException {
		log.info("Calling Get All Users method");
		final List<User> userList = userService.getAllUsers();
		if(userList != null) {
			return new ResponseEntity<>(userList, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
	}

	@GetMapping(value = "/users/search")
	public ResponseEntity<User> searchUser(@RequestParam("userName") final String userName) throws UserException {
		log.info("Calling search user {}", userName);
		final User user = userService.searchUser(userName);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
}
