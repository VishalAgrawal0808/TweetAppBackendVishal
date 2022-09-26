package com.tweetapp.tweet.service;

import java.util.List;

import com.tweetapp.tweet.dto.Tweet;
import com.tweetapp.tweet.dto.TweetRequestDto;
import com.tweetapp.tweet.exception.TweetException;

public interface TweetService {

    public List<Tweet> getAllTweets() throws TweetException;

    public List<Tweet> getAllTweetsByUser(final String userName) throws TweetException;

    public String likeTweet(final String userName, final int tweetId) throws TweetException;

    public String postTweet(final String userName, final TweetRequestDto tweetRequest) throws TweetException;

    public String updateTweet(final String userName, final int tweetId, final TweetRequestDto tweetRequest) throws TweetException;

    public String replyTweet(final String userName, final int tweetId, final String reply) throws TweetException;

    public String deleteTweet(final String userName, final int tweetId) throws TweetException;
    
}
