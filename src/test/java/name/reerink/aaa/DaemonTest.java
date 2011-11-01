package name.reerink.aaa;

import static org.junit.Assert.*;

import org.junit.Test;

public class DaemonTest {

	@Test
	public void testStartDaemon() {
		DaemonTester d = new DaemonTester();

		d.start();

		int ret = d.waitFor(30);
		System.out.println("output: " + d.getOutput());
		System.out.println("error: " + d.getError());
		assertEquals(0, ret);
	}
}
