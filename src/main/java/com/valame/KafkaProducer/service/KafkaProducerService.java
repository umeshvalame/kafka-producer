package com.valame.KafkaProducer.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.valame.KafkaProducer.controller.ProducerController;
import com.valame.microservices.domain.base_domains.dto.UserRegistrationEvent;

@Service
public class KafkaProducerService {

	
	Logger logger = LoggerFactory.getLogger(ProducerController.class);
	
	@Autowired
	private KafkaTemplate kafkaTemplate;

	public  CompletableFuture<SendResult<Integer, UserRegistrationEvent>> publishUserRegistrationEvent(UserRegistrationEvent userRegistrationEvent)  throws JsonProcessingException{

		List<Header> recordHeader = List.of(new RecordHeader("event-source", "inventory-event-producer".getBytes()));
		var producerRecord = new ProducerRecord<>("second_topic", null, userRegistrationEvent.getUser().getUserId(), userRegistrationEvent, recordHeader);

		var completableFuture = kafkaTemplate.send(producerRecord);
		return completableFuture.whenComplete(((sendResult, throwable) -> {
            if (throwable != null) {
                handleFailure(userRegistrationEvent.getUser().getUserId(), userRegistrationEvent, throwable);
            } else {
                handleSuccess(userRegistrationEvent.getUser().getUserId(), userRegistrationEvent, sendResult);
            }
        }));

	}

	
	 
	  private void handleFailure(Integer userId, UserRegistrationEvent userRegistrationEvent, Object throwable) {
		// TODO Auto-generated method stub
		
	}



	private void handleSuccess(Integer userId, UserRegistrationEvent userRegistrationEvent, Object sendResult) {
		// TODO Auto-generated method stub
		logger.info("Message sent successfully for the key: {} and the value: {} ",
				userId, userRegistrationEvent);

		
	}



	/*
	 * private void handleFailure(Integer key, Object value, Throwable throwable) {
	 * 
	 * logger.error("Error sending message and exception is {}",throwable.getMessage
	 * (), throwable); }
	 */
	 

}
