package com.libre.mqtt;

import lombok.Data;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.io.Serializable;
import java.util.Objects;

@Data
public class MqttMessage implements Serializable {

	private MessageHeaders messageHeaders;

	private Object payload;

	private String topic;

	private Integer qos;

	private Boolean retained;

	public MqttMessage(Message<?> message) {
		this.messageHeaders = message.getHeaders();
		this.topic = (String) Objects.requireNonNull(message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC));
		this.qos = (Integer) Objects.requireNonNull(message.getHeaders().get(MqttHeaders.RECEIVED_QOS));
		this.retained = (Boolean) Objects.requireNonNull(message.getHeaders().get(MqttHeaders.RECEIVED_RETAINED));
		this.payload = message.getPayload();
	}

	public MqttMessage(Object payload, String topic) {
		this.payload = payload;
		this.topic = topic;
		this.qos = MqttQoS.AT_MOST_ONCE.value();
		this.retained = false;
	}

	public MqttMessage(Object payload, String topic, Integer qos) {
		this.payload = payload;
		this.topic = topic;
		this.qos = qos;
		this.retained = false;
	}

	public MqttMessage(Object payload, String topic, Integer qos, Boolean retained) {
		this.payload = payload;
		this.topic = topic;
		this.qos = qos;
		this.retained = retained;
	}

	public static MqttMessage of(Message<?> message) {
		return new MqttMessage(message);
	}

	public static MqttMessage of(Object payload, String topic, Integer qos) {
		return new MqttMessage(payload, topic, qos);
	}

}
