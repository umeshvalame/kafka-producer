package com.valame.KafkaProducer.controller;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.valame.KafkaProducer.service.KafkaProducerService;
import com.valame.microservices.domain.base_domains.dto.User;
import com.valame.microservices.domain.base_domains.dto.UserRegistrationEvent;


@RestController
public class ProducerController {
	
	
	Logger logger = LoggerFactory.getLogger(ProducerController.class);
	
	@Autowired
	KafkaProducerService kafkaProducerService;
			
	@PostMapping("/kafka/producer/user")
	public ResponseEntity<String> submitOrder(@RequestBody User user) {
		
		Random rand = new Random();
		
		user.setUserId(rand.nextInt());
		UserRegistrationEvent userRegEvent = new UserRegistrationEvent();
		
		userRegEvent.setEventName("USER_REGISTERED");
		userRegEvent.setDescription("user registration event description");
		userRegEvent.setUser(user);
		
		try {
			var future = kafkaProducerService.publishUserRegistrationEvent(userRegEvent);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.info("User {} <== was created ", user);
		return ResponseEntity.ok(new String("User data published to the topic successfully!!!"));
	}
	

}
