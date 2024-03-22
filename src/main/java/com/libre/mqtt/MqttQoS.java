package com.libre.mqtt;

public enum MqttQoS {

	/**
	 * QoS level 0 至多发送一次，发送即丢弃。没有确认消息，也不知道对方是否收到。
	 */
	AT_MOST_ONCE(0),
	/**
	 * QoS level 1 至少一次，都要在可变头部中附加一个16位的消息ID，SUBSCRIBE 和 UNSUBSCRIBE 消息使用 QoS level 1。
	 */
	AT_LEAST_ONCE(1),
	/**
	 * QoS level 2 确保只有一次，仅仅在 PUBLISH 类型消息中出现，要求在可变头部中要附加消息ID。
	 */
	EXACTLY_ONCE(2),
	/**
	 * 失败
	 */
	FAILURE(0x80);

	private final int value;

	MqttQoS(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}

	public static MqttQoS valueOf(int value) {
		switch (value) {
			case 0:
				return AT_MOST_ONCE;
			case 1:
				return AT_LEAST_ONCE;
			case 2:
				return EXACTLY_ONCE;
			case 0x80:
				return FAILURE;
			default:
				throw new IllegalArgumentException("invalid QoS: " + value);
		}
	}

	@Override
	public String toString() {
		return "QoS" + value;
	}

}
