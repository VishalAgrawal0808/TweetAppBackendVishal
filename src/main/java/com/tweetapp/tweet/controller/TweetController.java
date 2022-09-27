package com.tweetapp.tweet.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.validation.Valid;

import com.tweetapp.tweet.constants.TweetAppConstants;
import com.tweetapp.tweet.dto.ResponseDto;
import com.tweetapp.tweet.dto.Tweet;
import com.tweetapp.tweet.dto.TweetRequestDto;
import com.tweetapp.tweet.exception.TweetException;
import com.tweetapp.tweet.service.TweetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Generated;

@RequestMapping(value = "/api/v1.0/tweets")
// @CrossOrigin("https://fsetweetappfrontend.azurewebsites.net")
@CrossOrigin("https://localhost:4200")
@RestController
@Generated
public class TweetController {
    
    @Autowired
	TweetService tweetService;

    @GetMapping("/all")
	public ResponseEntity<List<Tweet>> getAllTweets() throws TweetException {
		List<Tweet> tweetList = tweetService.getAllTweets();
        if(tweetList != null) {
            Collections.sort(tweetList, new Comparator<Tweet>() {
                public int compare(Tweet tweet1, Tweet tweet2) {
                   return tweet2.getCreatedDate().compareTo(tweet1.getCreatedDate());
                }
            });
            return new ResponseEntity<>(tweetList, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
	}

    @GetMapping("/{userName}")
	public ResponseEntity<List<Tweet>> getAllTweetsByUser(@PathVariable final String userName) throws TweetException {
		List<Tweet> allTweetsListByUser = tweetService.getAllTweetsByUser(userName);
        if (allTweetsListByUser != null) {
            Collections.sort(allTweetsListByUser, new Comparator<Tweet>() {
                public int compare(Tweet tweet1, Tweet tweet2) {
                    return tweet2.getCreatedDate().compareTo(tweet1.getCreatedDate());
                }
            });
            return new ResponseEntity<>(allTweetsListByUser, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
	}

    @PutMapping("/{userName}/like/{tweetId}")
	public ResponseEntity<String> likeTweet(@PathVariable("userName") final String userName,
			@PathVariable("tweetId") final int tweetId) throws TweetException {
       
		String likedTweet = tweetService.likeTweet(userName, tweetId);
        if(likedTweet.equals(TweetAppConstants.LIKED_TWEET)) {
            return new ResponseEntity<>(TweetAppConstants.LIKED_TWEET, HttpStatus.OK);
        }
        return new ResponseEntity<>(TweetAppConstants.FAILED_STATUS, HttpStatus.OK);
	}

    @PostMapping("/add/{userName}")
	public ResponseEntity<ResponseDto> postTweet(@PathVariable("userName") final String userName,
			@RequestBody @Valid final TweetRequestDto tweetRequest) throws TweetException {
        String postedTweet = tweetService.postTweet(userName, tweetRequest);
        if(postedTweet.equals(TweetAppConstants.TWEET_POSTED)) {
            ResponseDto response = new ResponseDto();
            response.setResponse(postedTweet);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseDto(), HttpStatus.OK);
	}

    @PutMapping("/{userName}/update/{tweetId}")
	public ResponseEntity<String> updateTweet(@PathVariable("userName") final String userName,
			@PathVariable("tweetId") final int tweetId, @RequestBody @Valid final TweetRequestDto tweetRequest) throws TweetException {
        String updatedTweet = tweetService.updateTweet(userName, tweetId, tweetRequest);
        if(updatedTweet.equals(TweetAppConstants.TWEET_UPDATED)) {
            return new ResponseEntity<>(TweetAppConstants.TWEET_UPDATED, HttpStatus.OK);
        }
        return new ResponseEntity<>(TweetAppConstants.FAILED_STATUS, HttpStatus.OK);
	}

    @PostMapping("/{userName}/reply/{tweetId}")
	public ResponseEntity<String> replyTweet(@PathVariable("userName") final String userName,
			@PathVariable("tweetId") final int tweetId, @RequestBody @Valid final String reply) throws TweetException {
        String replyTweet = tweetService.replyTweet(userName, tweetId, reply);
        if(replyTweet.equals(TweetAppConstants.REPLIED_TO_TWEET)) {
            return new ResponseEntity<>(TweetAppConstants.REPLIED_TO_TWEET, HttpStatus.OK);
        }
        return new ResponseEntity<>(TweetAppConstants.FAILED_STATUS, HttpStatus.OK);
	}

	@DeleteMapping("/{userName}/delete/{tweetId}")
	public ResponseEntity<ResponseDto> deleteTweet(@PathVariable("userName") final String userName,
			@PathVariable("tweetId") final int tweetId) throws TweetException {
        String deletedTweet = tweetService.deleteTweet(userName, tweetId);
        if(deletedTweet.equals(TweetAppConstants.TWEET_DELETED)) {
            ResponseDto response = new ResponseDto();
            response.setResponse(deletedTweet);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseDto(), HttpStatus.OK);
	}

}
