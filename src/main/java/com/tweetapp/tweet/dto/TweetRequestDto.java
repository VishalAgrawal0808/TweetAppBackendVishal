package com.tweetapp.tweet.dto;

import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Component
@Getter
@Setter
@ToString
public class TweetRequestDto {

	@Id
	private int tweetId;

	@NotBlank(message = "UserName cannot be null")
	private String userName;

	@NotBlank(message = "Tweet cannot be null")
	private String tweet;
}
