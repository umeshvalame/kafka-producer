package com.valame.KafkaProducer.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.valame.microservices.domain.base_domains.dto.UserRegistrationEvent;



@Configuration
public class KafkaConfig {

	@Value("${spring.kafka.producer.bootstrap-servers}")
    private List<String> bootstrapAddress;

	@Bean
	public ProducerFactory<Integer, UserRegistrationEvent> producerFactory() {

		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return new DefaultKafkaProducerFactory<Integer, UserRegistrationEvent>(props);
	}

	
	  @Bean 
	  public KafkaTemplate<Integer, UserRegistrationEvent> kafkaTemplate() { 
		  
		  return new KafkaTemplate<>(producerFactory());
	  }
	  
		
}