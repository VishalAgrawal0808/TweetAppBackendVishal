package com.tweetapp.tweet.repo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.tweetapp.tweet.dto.User;

@Repository
public interface UserRepository extends MongoRepository<User, Long>{
    
    @Query("{ emailId : ?0, password: ?1 }")
	Optional<User> findByEmailIdAndPassword(final String emailId, final String password);

	@Query("{ emailId : ?0}")
	Optional<User> findByUsername(final String userName);
}
