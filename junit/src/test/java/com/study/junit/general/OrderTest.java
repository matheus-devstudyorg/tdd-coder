package com.study.junit.general;

import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderTest {
	public static int counter = 0;

	@Test
	@Order(1)
	public void start(){
		counter = 1;
	}
	
	@Test
	@Order(2)
	public void verify(){
		Assertions.assertEquals(1, counter);
	}
}
