package name.reerink.aaa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DaemonTester extends AbstractJavaProcess {
	
	private static final Log LOG = LogFactory.getLog(DaemonTester.class);

	public DaemonTester() {
		super("AAADaemon", "name.reerink.aaa.Daemon");
		LOG.info("Classname: " + Daemon.class.getName() + " created");
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
	}

}
