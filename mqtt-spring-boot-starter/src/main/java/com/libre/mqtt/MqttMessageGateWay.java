package com.libre.mqtt;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import static com.libre.mqtt.MqttProperties.MQTT_OUT_BOUND_CHANNEL_NAME;

@MessagingGateway(defaultRequestChannel = MQTT_OUT_BOUND_CHANNEL_NAME)
public interface MqttMessageGateWay {

	/**
	 * 消息发送
	 * @param payload 消息体
	 */
	void sendToMqtt(String payload);

	/**
	 * 指定topic进行消息发送
	 * @param topic topic
	 * @param payload 消息体
	 */
	void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, @Payload String payload);

	/**
	 * 指定topic进行消息发送
	 * @param topic topic
	 * @param qos qos
	 * @param payload 消息体
	 */
	void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos,
			@Header(MqttHeaders.RETAINED) boolean retained, @Payload String payload);

	/**
	 * 指定topic进行消息发送
	 * @param topic topic
	 * @param qos qos
	 * @param payload 消息体
	 */
	void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos,
			@Header(MqttHeaders.RETAINED) boolean retained, @Payload byte[] payload);

}
