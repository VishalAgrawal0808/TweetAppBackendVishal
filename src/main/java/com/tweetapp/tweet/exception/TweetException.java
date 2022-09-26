package com.tweetapp.tweet.exception;

public class TweetException extends Exception {

    public TweetException() {
    }

    public TweetException(String message) {
        super(message);
    }

    public TweetException(Throwable cause) {
        super(cause);
    }

    public TweetException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
