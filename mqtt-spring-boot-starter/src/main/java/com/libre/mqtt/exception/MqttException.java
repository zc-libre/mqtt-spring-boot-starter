package com.libre.mqtt.exception;

public class MqttException extends RuntimeException {

	public MqttException(String message, Throwable cause) {
		super(message, cause);
	}

	public MqttException(Throwable cause) {
		super(cause);
	}

	public MqttException(String message) {
		super(message);
	}

}
