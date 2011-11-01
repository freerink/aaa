package name.reerink.aaa;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

public class AutonomousAutomaticAnnouncerTest {

	@Test
	public void test() {
		AutonomousAutomaticAnnouncer aaa = new AutonomousAutomaticAnnouncer();
		
		assertTrue(aaa.test());
		System.out.println("now = " + new Date());
	}

	@Test
	public void testAnnounce() {
		AutonomousAutomaticAnnouncer aaa = new AutonomousAutomaticAnnouncer();

		Date date = new Date();
		aaa.announce("Deze tekst is verstuurd door de AAA op " + date);
	}
}
