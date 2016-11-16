package com.github.nhapis.util.hash;

public class TestSimplePartitioner {
	
	public static void main(String [] args) {
		System.out.println(SimplePartitioner.partition("abc", 10));
		System.out.println(SimplePartitioner.partition("ab", 10));
		System.out.println(SimplePartitioner.partition("29890349", 100));
	}

}
