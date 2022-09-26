package com.tweetapp.tweet.config;

import com.tweetapp.tweet.constants.TweetAppConstants;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.Generated;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Generated
@Service
public class KafkaConsumerConfig {

	@KafkaListener(topics = "message", groupId = TweetAppConstants.GROUP_ID)
	public void consume(String message) {
		log.info("New Message received by consumer : " + message);
	}   
}
