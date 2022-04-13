package com.study.junit.general;
import com.study.junit.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AssertTest {

	@Test
	public void test(){
		Assertions.assertTrue(true);
		Assertions.assertFalse(false);

		Assertions.assertEquals( 1, 1);
		Assertions.assertEquals(0.51234, 0.512, 0.001);
		Assertions.assertEquals(Math.PI, 3.14, 0.01);
		
		int i = 5;
		Integer i2 = 5;
		Assertions.assertEquals(Integer.valueOf(i), i2);
		Assertions.assertEquals(i, i2.intValue());

		Assertions.assertEquals("ball", "ball");
		Assertions.assertNotEquals("ball", "house");
		Assertions.assertTrue("ball".equalsIgnoreCase("Ball"));
		Assertions.assertTrue("ball".startsWith("ba"));
		
		User u1 = new User("Jessica");
		User u2 = new User("Jessica");
		User u3 = null;

		Assertions.assertEquals(u1, u2);

		Assertions.assertSame(u2, u2);
		Assertions.assertNotSame(u1, u2);

		Assertions.assertNull(u3);
		Assertions.assertNotNull(u2);
	}
}
