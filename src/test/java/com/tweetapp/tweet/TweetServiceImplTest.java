package com.tweetapp.tweet;

import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.tweetapp.tweet.config.KafkaProducerConfig;
import com.tweetapp.tweet.constants.TweetAppConstants;
import com.tweetapp.tweet.dto.Tweet;
import com.tweetapp.tweet.dto.TweetRequestDto;
import com.tweetapp.tweet.dto.User;
import com.tweetapp.tweet.exception.TweetException;
import com.tweetapp.tweet.repo.TweetRepository;
import com.tweetapp.tweet.repo.UserRepository;
import com.tweetapp.tweet.service.TweetServiceImpl;

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
public class TweetServiceImplTest {
    
    @InjectMocks
    TweetServiceImpl tweetService;

    @Mock
    UserRepository userRepository;

    @Mock
    TweetRepository tweetRepository;

    @Mock
    MongoOperations mongoperation;

    @Mock
    KafkaProducerConfig producer;

    @Test
    void testPostTweet() throws TweetException {
        TweetRequestDto tweetRequestDto = new TweetRequestDto();
        Tweet tweet1 = new Tweet(2, "akshita31@gmail.com", "Hello , my second tweet", null, null, null);
        List<Tweet> listOfTweets = Arrays.asList(tweet1);
        Mockito.when(tweetRepository.findAll()).thenReturn(listOfTweets);
        tweetRequestDto.setTweetId(2);
        tweetRequestDto.setUserName("akshita31@gmail.com");
        tweetRequestDto.setTweet("Hello , my first tweet");
        Mockito.when(userRepository.findByUsername("akshita31@gmail.com")).thenReturn(Optional.of(new User()));
        String response = tweetService.postTweet("akshita31@gmail.com", tweetRequestDto);
        Assertions.assertEquals(TweetAppConstants.TWEET_POSTED, response);
    }

    @Test
    void testPostTweetWhenUserNotExist() throws TweetException {
        TweetRequestDto tweetRequestDto = new TweetRequestDto();
        tweetRequestDto.setTweetId(1);
        tweetRequestDto.setUserName("akshita31@gmail.com");
        tweetRequestDto.setTweet("Hello , my first tweet");
        Mockito.when(userRepository.findByUsername("akshita31@gmail.com")).thenReturn(Optional.empty());
        String response = tweetService.postTweet("akshita31@gmail.com", tweetRequestDto);
        Assertions.assertEquals(TweetAppConstants.FAILED_STATUS, response);
    }

    @Test
    void testGetAllTweets() throws TweetException {
        Tweet tweet1 = new Tweet(1, "akshita31@gmail.com", "Hello , my first tweet", null, null, null);
        Tweet tweet2 = new Tweet(2, "akshita31@gmail.com", "Hello , my second tweet", null, null, null);
        List<Tweet> listOfTweets = Arrays.asList(tweet1, tweet2);
        Mockito.when(tweetRepository.findAll()).thenReturn(listOfTweets);
        List<Tweet> tweetList = tweetService.getAllTweets();
        Assertions.assertEquals(2, tweetList.size());
    }

    @Test
    void testGetAllTweetsFailCase() throws TweetException {
        Mockito.when(tweetRepository.findAll()).thenReturn(null);
        Assertions.assertNull(tweetService.getAllTweets());
    }

    @Test
    void testGetAllTweetsByUser() throws TweetException {
        Tweet tweet1 = new Tweet(1, "akshita31@gmail.com", "Hello , my first tweet", null, null, null);
        Mockito.when(userRepository.findByUsername("akshita31@gmail.com")).thenReturn(Optional.of(new User()));
        Mockito.when(tweetRepository.findByUserName("akshita31@gmail.com"))
                .thenReturn(Arrays.asList(tweet1));
        List<Tweet> tweetList = tweetService.getAllTweetsByUser("akshita31@gmail.com");
        Assertions.assertEquals(1, tweetList.size());
    }

    @Test
    void testGetAllTweetsByUserFailCase() throws TweetException {
        Mockito.when(userRepository.findByUsername("akshita31@gmail.com")).thenReturn(Optional.of(new User()));
        Mockito.when(tweetRepository.findByUserName("akshita31@gmail.com")).thenReturn(null);
        Assertions.assertNull(tweetService.getAllTweetsByUser("akshita31@gmail.com"));
    }

    @Test
    void testUpdateTweet() throws TweetException {
        Mockito.when(tweetRepository.findById(1)).thenReturn(Optional.of(new Tweet()));
        Mockito.when(userRepository.findByUsername("akshita31@gmail.com")).thenReturn(Optional.of(new User()));
        Mockito.when(mongoperation.findAndModify(any(), any(), any())).thenReturn(new Tweet());
        TweetRequestDto tweetRequestDto = new TweetRequestDto();
        tweetRequestDto.setTweetId(1);
        tweetRequestDto.setUserName("akshita31@gmail.com");
        tweetRequestDto.setTweet("Hello , my first tweet");
        String response = tweetService.updateTweet("akshita31@gmail.com", 1, tweetRequestDto);
        Assertions.assertEquals(TweetAppConstants.TWEET_UPDATED, response);
    }

    @Test
    void testUpdateTweetFailCase() throws TweetException {
        Mockito.when(tweetRepository.findById(1)).thenReturn(Optional.of(new Tweet()));
        Mockito.when(userRepository.findByUsername("akshita31@gmail.com")).thenReturn(Optional.of(new User()));
        Mockito.when(mongoperation.findAndModify(any(), any(), any())).thenReturn(null);
        TweetRequestDto tweetRequestDto = new TweetRequestDto();
        tweetRequestDto.setTweetId(1);
        tweetRequestDto.setUserName("akshita31@gmail.com");
        tweetRequestDto.setTweet("Hello , my first tweet");
        String response = tweetService.updateTweet("akshita31@gmail.com", 1, tweetRequestDto);
        Assertions.assertEquals(TweetAppConstants.FAILED_STATUS, response);
    }

    @Test
    void testDeleteTweet() throws TweetException {
        Mockito.when(tweetRepository.findById(1)).thenReturn(Optional.of(new Tweet()));
        Mockito.when(userRepository.findByUsername("akshita31@gmail.com")).thenReturn(Optional.of(new User()));
        String response = tweetService.deleteTweet("akshita31@gmail.com", 1);
        Assertions.assertEquals(TweetAppConstants.TWEET_DELETED, response);
    }

    @Test
    void testDeleteTweetFailCase() throws TweetException {
        Mockito.when(tweetRepository.findById(1)).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByUsername("akshita31@gmail.com")).thenReturn(Optional.of(new User()));
        String response = tweetService.deleteTweet("akshita31@gmail.com", 1);
        Assertions.assertEquals(TweetAppConstants.FAILED_STATUS, response);
    }

    @Test
    void testReplyTweet() throws TweetException {
        Mockito.when(tweetRepository.findById(1)).thenReturn(Optional.of(new Tweet()));
        Mockito.when(userRepository.findByUsername("akshita31@gmail.com")).thenReturn(Optional.of(new User()));
        Mockito.when(mongoperation.findAndModify(any(), any(), any())).thenReturn(new Tweet());
        String response = tweetService.replyTweet("akshita31@gmail.com", 1, "Reply tweet");
        Assertions.assertEquals(TweetAppConstants.REPLIED_TO_TWEET, response);
    }

    @Test
    void testReplyTweetFailCase() throws TweetException {
        Mockito.when(userRepository.findByUsername("akshita31@gmail.com")).thenReturn(Optional.empty());
        String response = tweetService.replyTweet("akshita31@gmail.com", 1, "Reply tweet");
        Assertions.assertEquals(TweetAppConstants.FAILED_STATUS, response);
    }

    @Test
    void testLikeTweet() throws TweetException {
        Mockito.when(tweetRepository.findById(1)).thenReturn(Optional.of(new Tweet()));
        Mockito.when(userRepository.findByUsername("akshita31@gmail.com")).thenReturn(Optional.of(new User()));
        Mockito.when(mongoperation.findAndModify(any(), any(), any())).thenReturn(new Tweet());
        String response = tweetService.likeTweet("akshita31@gmail.com", 1);
        Assertions.assertEquals(TweetAppConstants.LIKED_TWEET, response);
    }

    @Test
    void testLikeTweetFailCase() throws TweetException {
        Mockito.when(tweetRepository.findById(1)).thenReturn(Optional.of(new Tweet()));
        Mockito.when(userRepository.findByUsername("akshita31@gmail.com")).thenReturn(Optional.of(new User()));
        Mockito.when(mongoperation.findAndModify(any(), any(), any())).thenReturn(null);
        String response = tweetService.likeTweet("akshita31@gmail.com", 1);
        Assertions.assertEquals(TweetAppConstants.FAILED_STATUS, response);
    }

}
