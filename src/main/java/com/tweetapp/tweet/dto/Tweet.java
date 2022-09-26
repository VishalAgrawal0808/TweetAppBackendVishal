package com.tweetapp.tweet.dto;


import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "TWEET")
@Component
public class Tweet {

	@Id
	private int tweetId;

	private String userName;

	private String tweet;

	private Map<String, Integer> noOfLikes;

	private Date createdDate;
	
	private Map<String, List<String>> replies;
	
}
