package com.libre.mqtt;

public enum TopicFilterType {

	/**
	 * 默认 TopicFilter
	 */
	NONE {
		@Override
		public boolean match(String topicFilter, String topicName) {
			return TopicUtils.match(topicFilter, topicName);
		}
	},

	/**
	 * $queue/ 为前缀的共享订阅是不带群组的共享订阅
	 */
	QUEUE {
		@Override
		public boolean match(String topicFilter, String topicName) {
			int prefixLen = TopicFilterType.SHARE_QUEUE_PREFIX.length();
			return TopicUtils.match(topicFilter.substring(prefixLen), topicName);
		}
	},

	/**
	 * $share/{group-name}/ 为前缀的共享订阅是带群组的共享订阅
	 */
	SHARE {
		@Override
		public boolean match(String topicFilter, String topicName) {
			// 去除前缀 $share/<group-name>/ ,匹配 topicName / 前缀
			int prefixLen = TopicFilterType.findShareTopicIndex(topicFilter);
			return TopicUtils.match(topicFilter.substring(prefixLen), topicName);
		}
	};

	/**
	 * 共享订阅的 topic
	 */
	public static final String SHARE_QUEUE_PREFIX = "$queue/";

	public static final String SHARE_GROUP_PREFIX = "$share/";

	/**
	 * 判断 topicFilter 和 topicName 匹配情况
	 * @param topicFilter topicFilter
	 * @param topicName topicName
	 * @return 是否匹配
	 */
	public abstract boolean match(String topicFilter, String topicName);

	/**
	 * 获取 topicFilter 类型
	 * @param topicFilter topicFilter
	 * @return TopicFilterType
	 */
	public static TopicFilterType getType(String topicFilter) {
		if (topicFilter.startsWith(TopicFilterType.SHARE_QUEUE_PREFIX)) {
			return TopicFilterType.QUEUE;
		}
		else if (topicFilter.startsWith(TopicFilterType.SHARE_GROUP_PREFIX)) {
			return TopicFilterType.SHARE;
		}
		else {
			return TopicFilterType.NONE;
		}
	}

	/**
	 * 读取共享订阅的分组名
	 * @param topicFilter topicFilter
	 * @return 共享订阅分组名
	 */
	public static String getShareGroupName(String topicFilter) {
		int prefixLength = TopicFilterType.SHARE_GROUP_PREFIX.length();
		int topicFilterLength = topicFilter.length();
		for (int i = prefixLength; i < topicFilterLength; i++) {
			char ch = topicFilter.charAt(i);
			if ('/' == ch) {
				return topicFilter.substring(prefixLength, i);
			}
		}
		throw new IllegalArgumentException(
				"Share subscription topicFilter: " + topicFilter + " not conform to the $share/<group-name>/xxx");
	}

	private static int findShareTopicIndex(String topicFilter) {
		int prefixLength = TopicFilterType.SHARE_GROUP_PREFIX.length();
		int topicFilterLength = topicFilter.length();
		for (int i = prefixLength; i < topicFilterLength; i++) {
			char ch = topicFilter.charAt(i);
			if ('/' == ch) {
				return i + 1;
			}
		}
		throw new IllegalArgumentException(
				"Share subscription topicFilter: " + topicFilter + " not conform to the $share/<group-name>/xxx");
	}

}
