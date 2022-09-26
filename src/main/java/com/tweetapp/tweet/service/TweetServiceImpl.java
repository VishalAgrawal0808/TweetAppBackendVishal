package com.tweetapp.tweet.service;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.tweetapp.tweet.config.KafkaProducerConfig;
import com.tweetapp.tweet.constants.TweetAppConstants;
import com.tweetapp.tweet.dto.Tweet;
import com.tweetapp.tweet.dto.TweetRequestDto;
import com.tweetapp.tweet.dto.User;
import com.tweetapp.tweet.exception.TweetException;
import com.tweetapp.tweet.repo.TweetRepository;
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
public class TweetServiceImpl implements TweetService{

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoOperations mongoOperation;

    @Autowired
    private KafkaProducerConfig kafkaProducer;

    @Override
    public String deleteTweet(final String userName, final int tweetId) throws TweetException {
        log.info("Tweet Service deleteTweet() - Start");
        try {
            if (validUser(userName) && validTweet(tweetId)) {
                tweetRepository.deleteById(tweetId);
                // kafkaProducer.sendMessage("The Tweet got deleted with the following tweetId" + tweetId);
                log.info("Tweet Service deleteTweet() - End");
                return TweetAppConstants.TWEET_DELETED;
            }
        } catch (Exception e) {
            throw new TweetException(TweetAppConstants.FAILED_STATUS + e);
        }
        log.info("Tweet Service deleteTweet() - End");
        return TweetAppConstants.FAILED_STATUS;
    }

    @Override
    public List<Tweet> getAllTweets() throws TweetException {
        log.info("Tweet Service getAllTweets() - Start");
        try {
            final List<Tweet> listOfTweets = tweetRepository.findAll();
            if (listOfTweets != null) {
                log.info("Tweet Service getAllTweets() - End");
                return listOfTweets;
            }
        } catch (Exception e) {
            throw new TweetException(TweetAppConstants.NO_TWEETS_FOUND);
        }
        log.info("Tweet Service getAllTweets() - End");
        return null;
    }

    @Override
    public List<Tweet> getAllTweetsByUser(final String userName) throws TweetException {
        log.info("Tweet Service getAllTweetsByUser() - Start");
        try {
            if (validUser(userName)) {
                final List<Tweet> userListOfTweets = tweetRepository.findByUserName(userName);
                if (userListOfTweets != null) {
                    log.info("Tweet Service getAllTweetsByUser() - End");
                    return userListOfTweets;
                }
            }
        } catch (Exception e) {
            throw new TweetException(TweetAppConstants.NO_TWEETS_FOUND);
        }
        log.info("Tweet Service getAllTweetsByUser() - End");
        return null;
    }

    @Override
    public String likeTweet(final String userName, final int tweetId) throws TweetException {
        log.info("Tweet Service likeTweet() - Start");
        try {
            if (validUser(userName) && validTweet(tweetId)) {
                Optional<Tweet> findByTweetId = tweetRepository.findById(tweetId);
                if (findByTweetId.isPresent()) {
                    Tweet tweet = findByTweetId.get();
                    final Map<String, Integer> likesOnTweetMap = tweet.getNoOfLikes();
                    if (likesOnTweetMap != null) {
                        likesOnTweetMap.put(userName, 1);
                        tweet.setNoOfLikes(likesOnTweetMap);
                    } else {
                        final Map<String, Integer> likeMap = new HashMap<>();
                        likeMap.put(userName, 1);
                        tweet.setNoOfLikes(likeMap);
                    }
                    Query query = new Query();
                    query.addCriteria(Criteria.where(TweetAppConstants.TWEET_ID).is(tweetId));
                    Update update = new Update();
                    update.set(TweetAppConstants.LIKES, tweet.getNoOfLikes());
                    tweet = mongoOperation.findAndModify(query, update, Tweet.class);
                    if (tweet != null) {
                        kafkaProducer.sendMessage("New like for tweet with TweetID" + tweet.getTweetId());
                        log.info("Tweet Service likeTweet() - End");
                        return TweetAppConstants.LIKED_TWEET;
                    }
                }
            }
        } catch (Exception e) {
            throw new TweetException(TweetAppConstants.FAILED_STATUS + e);
        }
        log.info("Tweet Service likeTweet() - End");
        return TweetAppConstants.FAILED_STATUS;
    }

    @Override
    public String postTweet(final String userName, final TweetRequestDto tweetRequest) throws TweetException {
        log.info("Tweet Service postTweet() - Start");
        try {
            if (validUser(userName) && tweetRequest != null && !tweetRequest.getUserName().isEmpty()
            && userName.equals(tweetRequest.getUserName())) {
                List<Tweet> postCountList = tweetRepository.findAll();
                if (postCountList != null) {
                    int postCount = 1;
                    Collections.sort(postCountList, new Comparator<Tweet>() {
                        public int compare(Tweet tweet1, Tweet tweet2) {
                            return tweet2.getCreatedDate().compareTo(tweet1.getCreatedDate());
                        }
                    });
                    if(postCountList.size() > 0) {
                        postCount = postCountList.get(0).getTweetId() + 1;
                    }
                    final Tweet tweet = new Tweet(((int) postCount), tweetRequest.getUserName(), tweetRequest.getTweet(),
                            new HashMap<String, Integer>(), new Date(System.currentTimeMillis()), null);
                    tweetRepository.save(tweet);
                    log.info("Tweet Service postTweet() - End");
                    return TweetAppConstants.TWEET_POSTED;
                }
            }
        } catch (Exception e) {
            throw new TweetException(TweetAppConstants.FAILED_STATUS + e);
        }
        log.info("Tweet Service postTweet() - End");
        return TweetAppConstants.FAILED_STATUS;
    }

    @Override
    public String replyTweet(final String userName, final int tweetId, final String reply) throws TweetException {
        log.info("Tweet Service replyTweet() - Start");
        try {
            if (validUser(userName) && validTweet(tweetId)) {
                Optional<Tweet> replyTweetId = tweetRepository.findById(tweetId);
                if (replyTweetId.isPresent()) {
                    Tweet tweet = replyTweetId.get();
                    Map<String, List<String>> replyTweetMap = tweet.getReplies();
                    if (replyTweetMap != null) {
                        if (replyTweetMap.containsKey(userName)) {
                            replyTweetMap.get(userName).add(reply);
                            tweet.setReplies(replyTweetMap);
                        } else {
                            replyTweetMap.put(userName, Arrays.asList(reply));
                            tweet.setReplies(replyTweetMap);
                        }
                    } else {
                        final Map<String, List<String>> newReplyMap = new HashMap<>();
                        newReplyMap.put(userName, Arrays.asList(reply));
                        tweet.setReplies(newReplyMap);
                    }
                    Query query = new Query();
                    query.addCriteria(Criteria.where(TweetAppConstants.TWEET_ID).is(tweetId));
                    Update update = new Update();
                    update.set(TweetAppConstants.REPLIES, tweet.getReplies());

                    tweet = mongoOperation.findAndModify(query, update, Tweet.class);
                    if (tweet != null) {
                        log.info("Tweet Service replyTweet() - End");
                        return TweetAppConstants.REPLIED_TO_TWEET;
                    }
                }
            }
        } catch (Exception e) {
            throw new TweetException(TweetAppConstants.FAILED_STATUS + e);
        }
        log.info("Tweet Service replyTweet() - End");
        return TweetAppConstants.FAILED_STATUS;
    }

    @Override
    public String updateTweet(final String userName, final int tweetId, final TweetRequestDto tweetRequest) throws TweetException {
        log.info("Tweet Service updateTweet() - Start");
        try {
            if (validUser(userName) && validTweet(tweetId) && userName.equals(tweetRequest.getUserName())) {
                Optional<Tweet> updateTweetId = tweetRepository.findById(tweetId);
                if (updateTweetId.isPresent()) {
                    Tweet tweet = updateTweetId.get();
                    Query query = new Query();
                    query.addCriteria(Criteria.where(TweetAppConstants.TWEET_ID).is(tweetId));
                    Update update = new Update();
                    update.set(TweetAppConstants.TWEET, tweetRequest.getTweet());

                    tweet = mongoOperation.findAndModify(query, update, Tweet.class);
                    if (tweet != null) {
                        log.info("Tweet Service updateTweet() - End");
                        return TweetAppConstants.TWEET_UPDATED;
                    }
                }
            }
        } catch (Exception e) {
            throw new TweetException(TweetAppConstants.FAILED_STATUS + e);
        }
        log.info("Tweet Service updateTweet() - End");
        return TweetAppConstants.FAILED_STATUS;
    }

    private boolean validUser(final String username) {
        if (username != null) {
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                log.info("User is valid " + username);
                return true;
            }
        }
        log.info("User is invalid " + username);
        return false;
    }

    private boolean validTweet(final int tweetId) {
        if (tweetId > 0) {
            Optional<Tweet> tweet = tweetRepository.findById(tweetId);
            if (tweet.isPresent()) {
                log.info("Tweet is valid " + tweetId);
                return true;
            }
        }
        log.info("Tweet is invalid " + tweetId);
        return false;
    }
    
}
