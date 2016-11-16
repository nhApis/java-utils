package com.github.nhapis.util.kafka;

public class TestMessageLocalWriter {
	
	public static void main(String [] args) {
		MessageLocalWriter local_writer = new MessageLocalWriter("./data");
		
		for (int i = 0; i < 120; ++i) {
			local_writer.processMessage("aa", 1, new Integer(i).toString());
			try {
				Thread.sleep(1 * 1000);
			} catch (Exception e) {
				
			}
		}
	}

}
