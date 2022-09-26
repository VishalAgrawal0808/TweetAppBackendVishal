package com.tweetapp.tweet;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.tweetapp.tweet.dto.User;
import com.tweetapp.tweet.exception.UserException;
import com.tweetapp.tweet.repo.UserRepository;
import com.tweetapp.tweet.service.UserServiceImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    
    @Mock
	UserRepository userRepository;

	@InjectMocks
	UserServiceImpl userService;

	@Mock
	MongoOperations mongoperation;

    @Test
	void testLoginUser() throws UserException {
		Mockito.when(userRepository.findByEmailIdAndPassword("akshita31@gmail.com", "1"))
				.thenReturn(Optional.of(new User()));
		String login = userService.loginUser("akshita31@gmail.com", "1");
		Assertions.assertEquals("User login successfully", login);
	}

    @Test
	void testLoginUserFailCase() throws UserException {
		Mockito.when(userRepository.findByEmailIdAndPassword("akshita31@gmail.com", "1"))
				.thenReturn(Optional.empty());
        String login = userService.loginUser("akshita31@gmail.com", "1");
		Assertions.assertEquals("User login failed",login);
	}

	@Test
	void testRegisterUser() throws UserException {
		User user = new User();
		user.setUserId(1);
		user.setPassword("hello123");
		user.setLastName("Srivastava");
		user.setFirstName("Akshita");
		user.setEmailId("akshita31@gmail.com");
        Mockito.when(userRepository.findByUsername("akshita31@gmail.com")).thenReturn(Optional.empty());
		String response = userService.registerUser(user);
		Assertions.assertEquals("User Registered Successfully", response);
	}

	@Test
	void testRegisterUserFailCase() throws UserException {
        User user = new User();
		user.setUserId(1);
		user.setPassword("hello123");
		user.setLastName("Srivastava");
		user.setFirstName("Akshita");
		user.setEmailId("akshita31@gmail.com");	
        Mockito.when(userRepository.findByUsername("akshita31@gmail.com")).thenReturn(Optional.of(user));
		String response = userService.registerUser(user);
		Assertions.assertEquals("User Already Exists", response);
	}

    @Test
	void testForgotPassword() throws UserException {
		Mockito.when(userRepository.findByUsername("akshita31@gmail.com")).thenReturn(Optional.of(new User()));
		Mockito.when(mongoperation.findAndModify(any(), any(), any())).thenReturn(new User());
		String response = userService.forgotPassword("akshita31@gmail.com","hello12");
		Assertions.assertEquals("New password updated successfully",response);
	}

	@Test
	void testForgotPasswordWhenUserNotFound() throws UserException {
        Mockito.when(userRepository.findByUsername("akshita31@gmail.com")).thenReturn(Optional.empty());
        String response = userService.forgotPassword("akshita31@gmail.com","hello12");
		Assertions.assertEquals("User does not exist", response);
	}

    @Test
	void testSearchUser() throws UserException {
		Mockito.when(userRepository.findByUsername("akshita31@gmail.com")).thenReturn(Optional.of(new User()));
        User user = userService.searchUser("akshita31@gmail.com");
		Assertions.assertNotNull(user);
	}

	@Test
	void testSearchUserFailCase() throws UserException {
		Mockito.when(userRepository.findByUsername("akshita31@gmail.com")).thenReturn(Optional.empty());
        User user = userService.searchUser("akshita31@gmail.com");
		Assertions.assertNull(user.getFirstName());
	}

    @Test
	void getAllusersTest() throws UserException {
        User user1 = new User();
		user1.setUserId(1);
		user1.setPassword("hello123");
		user1.setLastName("Srivastava");
		user1.setFirstName("Akshita");
		user1.setEmailId("akshita31@gmail.com");
        User user2 = new User();
		user2.setUserId(2);
		user2.setPassword("hello123");
		user2.setLastName("Srivastava");
		user2.setFirstName("Akshita");
		user2.setEmailId("akshita56@gmail.com");
		List<User> userList = Arrays.asList(user1,user2);
		Mockito.when(userRepository.findAll()).thenReturn(userList);
		List<User> allUsers = userService.getAllUsers();
		Assertions.assertEquals(2,allUsers.size());
	}

	@Test
	void testGetAllUsersFailCase() throws UserException {
		Mockito.when(userRepository.findAll()).thenReturn(new ArrayList<>());
        List<User> userList = userService.getAllUsers();
		Assertions.assertNull(userList);
	}

}
