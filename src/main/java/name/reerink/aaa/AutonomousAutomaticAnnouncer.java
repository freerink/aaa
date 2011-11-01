package name.reerink.aaa;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class AutonomousAutomaticAnnouncer {

	public boolean test() {
		Twitter twitter = new TwitterFactory().getInstance();

		boolean ret = false;
		try {
			ret = twitter.test();
		} catch (TwitterException e) {
			System.out.println(e);
			throw new RuntimeException(e);
		}
		return ret;
	}

	/**
	 * Sent an announcement directly to Twitter.
	 * 
	 * @param newStatus
	 */
	public void announce(String newStatus) {
		Twitter twitter = new TwitterFactory().getInstance();

		StatusUpdate statusUpdate = new StatusUpdate(newStatus);
		try {
			Status status = twitter.updateStatus(statusUpdate);
			System.out.println("updateStatus returned: " + status);
		} catch (TwitterException e) {
			System.out.println(e);
			throw new RuntimeException(e);
		}
	}

	public static void main(String args[]) {
		AutonomousAutomaticAnnouncer aaa = new AutonomousAutomaticAnnouncer();

		if (args.length < 1) {
			throw new IllegalArgumentException(
					"Supply at least one argument: the status message.");
		}
		StringBuffer newStatusBuf = new StringBuffer();
		for (String arg : args) {
			newStatusBuf.append(arg + " ");
		}
		String newStatus = newStatusBuf.substring(0, newStatusBuf.length() - 1);
		System.out.println("Twittering status: " + newStatus);
		aaa.announce(newStatus.toString());
	}
}
