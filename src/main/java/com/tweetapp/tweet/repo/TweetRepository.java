package com.tweetapp.tweet.repo;

import java.util.List;

import com.tweetapp.tweet.dto.Tweet;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetRepository extends MongoRepository<Tweet, Integer> {

	@Query("{ userName : ?0}")
	List<Tweet> findByUserName(final String userName);

}
