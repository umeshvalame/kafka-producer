package com.valame.KafkaProducer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.valame.KafkaProducer.controller.ProducerController;
import com.valame.microservices.domain.base_domains.dto.UserRegistrationEvent;

@Service
public class KafkaProducerService {

	
	Logger logger = LoggerFactory.getLogger(ProducerController.class);
	
	@Value("${spring.kafka.producer.bootstrap-servers}")
    private List<String> bootstrapAddress;
	
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

	public void publishUserRegistrationEventWithCallBack(UserRegistrationEvent userRegistrationEvent)  throws JsonProcessingException{

		List<Header> recordHeader = List.of(new RecordHeader("event-source", "inventory-event-producer".getBytes()));
		var producerRecord = new ProducerRecord<Integer, UserRegistrationEvent>("second_topic",null,  userRegistrationEvent);
		
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		
		KafkaProducer<Integer, UserRegistrationEvent> kafkaProducer = new KafkaProducer<>(props);
		
		kafkaProducer.send(producerRecord, new Callback() {
			
			@Override
			public void onCompletion(RecordMetadata metadata, Exception exception) {
				if(exception == null) {
				
					logger.info("Received Metadata "
							+ "Topic : "+metadata.topic()+"\n"
							+ "Partition : "+metadata.partition()+"\n"
							+ "Offset : "+ metadata.offset()+"\n"
							+ "Timestamp "+metadata.timestamp()
							+ "");
				}else {
					logger.error("Error in PRODUCER!!!! in producing the record to the topic" +exception.getMessage());
				}
			}
		});
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
