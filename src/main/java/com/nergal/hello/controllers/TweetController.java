package com.nergal.hello.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nergal.hello.controllers.dto.CreateTweetDTO;
import com.nergal.hello.entities.Tweet;
import com.nergal.hello.repositories.TweetRepository;
import com.nergal.hello.repositories.UserRepository;

@RestController
public class TweetController {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public TweetController(TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }
    
    @PostMapping("/tweets")
    public ResponseEntity<Void> createTweet(@RequestBody CreateTweetDTO dto, JwtAuthenticationToken token) {
        var user = userRepository.findById(UUID.fromString(token.getName()));
        var tweet = new Tweet();
        tweet.setUser(user.get());
        tweet.setContent(dto.content());
        tweetRepository.save(tweet);

        return ResponseEntity.ok().build();
    }
}
