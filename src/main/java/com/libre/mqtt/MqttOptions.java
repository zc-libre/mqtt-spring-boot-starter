package com.libre.mqtt;

import java.util.List;

public interface MqttOptions {

	/**
	 * 添加topic
	 * @param topic topic
	 */
	void addTopic(String topic);

	/**
	 * 添加topic
	 * @param topic topic
	 * @param qos qos
	 */
	void addTopic(String topic, int qos);

	/**
	 * 删除topic
	 * @param topic topic
	 */
	void removeTopic(String topic);

	/**
	 * 查询订阅的所有topic
	 * @return topic list
	 */
	List<String> listTopics();

	/**
	 * 发布消息
	 * @param mqttMessage {@link MqttMessage}
	 */
	void convertAndSend(MqttMessage mqttMessage);

}
