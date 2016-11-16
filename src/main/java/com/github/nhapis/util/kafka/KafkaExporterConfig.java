package com.github.nhapis.util.kafka;

import com.github.nhapis.util.StringUtil;
import com.github.nhapis.util.conf.Config;

public class KafkaExporterConfig {
	
	private static KafkaExporterConfig instance = new KafkaExporterConfig();

	private Config config = new Config();
	
	public static KafkaExporterConfig getInstance() {
		return instance;
	}
	
	private KafkaExporterConfig() {
	}
	
	public boolean parse(String confFile) {
		return config.parse(confFile);
	}
	
	public Config getConfig() {
		return config;
	}
	
	public boolean isValid() {
		return (!StringUtil.isEmpty(config.getString("kafka.zk.list")) &&
				!StringUtil.isEmpty(config.getString("kafka.broker.list")) &&
				!StringUtil.isEmpty(config.getString("kafka.consumer.group")) &&
				!StringUtil.isEmpty(config.getString("kafka.consumer.group.restart.offset.largest")) &&
				!StringUtil.isEmpty(config.getString("kafka.topic.partition")) &&
				!StringUtil.isEmpty(config.getString("local.data.dir")));
	}
	
	@Override
	public String toString() {
		return config.toString();
	}
	
}
