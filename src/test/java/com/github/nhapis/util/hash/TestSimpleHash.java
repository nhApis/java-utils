package com.github.nhapis.util.hash;

public class TestSimpleHash {
	
	public static void main(String [] args) {
		System.out.println(SimpleHash.hash("abc"));
		System.out.println(SimpleHash.hash("ab"));
		System.out.println(SimpleHash.hash(""));
	}

}
