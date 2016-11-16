package com.github.nhapis.util.kafka;

import java.util.ArrayList;
import java.util.List;

import com.github.nhapis.util.conf.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestMessageKafkaWriterGroup {
	
	private static final Logger logger = LoggerFactory.getLogger(TestMessageKafkaWriterGroup.class);
	
	public static void main(String [] args) {
		// src.kafka.zk.list, src.kafka.consumer.group, src.kafka.broker.list
		// dst.kafka.zk.list, dst.kafka.broker.list
		String conf_file = "../conf/kafka.properties";
		Config config = new Config();
		if (!config.parse(conf_file)) {
			logger.warn("parse conf file failed : {}", conf_file);
			return;
		}
		
		logger.info(config.toString());
		
		String topic = "test";
		int partitions = 4;
		
		List<AbstractMessageProcessor> processors = new ArrayList<AbstractMessageProcessor>();
		for (int i = 0; i < partitions; ++i) {
			MessageKafkaWriter kafka_writer = new MessageKafkaWriter(config.getString("dst.kafka.zk.list"),
					config.getString("dst.kafka.broker.list"));
			if (kafka_writer.init()) {
				processors.add(kafka_writer);
			}
		}
		
		HighLevelConsumerGroup group = new HighLevelConsumerGroup(config.getString("src.kafka.zk.list"), 
				config.getString("src.kafka.consumer.group"),
				topic, partitions, true, processors);
		
		if (group.init()) {
			logger.info("HighLevelConsumerGroup init ok");
		} else {
			logger.warn("HighLevelConsumerGroup init failed");
		}
		
		try {
			Thread.sleep(20 * 1000);
		} catch (Exception e) {
		}
		
		// uninit
		group.uninit();
		
		for (AbstractMessageProcessor processor : processors) {
			processor.uninit();
		}
	}

}
