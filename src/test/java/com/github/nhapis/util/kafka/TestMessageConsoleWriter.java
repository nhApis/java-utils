package com.github.nhapis.util.kafka;

public class TestMessageConsoleWriter {

	public static void main(String [] args) {
		MessageConsoleWriter writer = new MessageConsoleWriter();
		writer.processMessage("test", 0, "abc");
	}
}
