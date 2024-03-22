package com.libre.mqtt;

public interface MqttMessageListener {

	/**
	 * 消息订阅
	 * @see MqttMessageInboundHandler
	 * @param message {@link MqttMessage}
	 */
	void onMessage(MqttMessage message);

}
