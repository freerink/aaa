package name.reerink.aaa;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class for running a process using Runtime.getRuntime().exec(...).
 * This class provides for easy access to the input-, output and error streams
 * of the process and allows the client to wait conveniently for the process to
 * terminate.
 */
public abstract class AbstractProcess {
	private static final Log LOG = LogFactory.getLog(AbstractProcess.class);

	private static final int MAX_OUTPUT_LINES = 1000;

	/**
	 * method to make a transition from System.out's to log4j messaging
	 * 
	 * @param t
	 *            text to display
	 */
	private static void outputRedirect(String t) {
		LOG.debug(t);
	}

	class StreamRunner implements Runnable {

		private BufferedReader input;

		private Vector<String> data;

		StreamRunner(InputStream input) {
			this.input = new BufferedReader(new InputStreamReader(input));
			this.data = new Vector<String>();
		}

		public void run() {
			String line = null;
			try {
				line = input.readLine();
				while (line != null) {
					if (this.data.size() < MAX_OUTPUT_LINES) {
						this.data.add(line);
					} else if (this.data.size() == MAX_OUTPUT_LINES) {
						this.data.add("Output truncated");
					}
					line = input.readLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public Vector<String> getData() {
			return this.data;
		}

	}// Class StreamRunner

	private String name, command;

	private List<String> arguments = null;

	private File working;

	private Process process;

	private StreamRunner outputReader;

	private StreamRunner errorReader;

	public AbstractProcess() {
	}

	public AbstractProcess(String name) {
		this.name = name;
	}

	public AbstractProcess(String name, String command) {
		this.name = name;
		this.command = command;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCommand() {
		return this.command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public List<String> getArguments() {
		if (this.arguments == null) {
			this.arguments = new ArrayList<String>();
		}
		return this.arguments;
	}

	public void addArgument(String argument) {
		getArguments().add(argument);
	}

	public void setArguments(List<String> arguments) {
		this.arguments = arguments;
	}

	public File getWorking() {
		return this.working;
	}

	public void setWorking(File working) {
		this.working = working;
	}

	public Process getProcess() {
		return this.process;
	}

	/**
	 * getOutput returns the contents of the process outputStream. Call after
	 * the process has terminated else it may block.
	 * 
	 * @return Vector containing the output String messages (one element per
	 *         line)
	 */
	public Vector<String> getOutput() {
		return this.outputReader.getData();
	}

	/**
	 * getError returns the contents of the process errorStream. Call after the
	 * process has terminated else it may block.
	 * 
	 * @return Vector containing the error String messages (one element per
	 *         line)
	 */
	public Vector<String> getError() {
		return this.errorReader.getData();
	}

	/**
	 * Reset the process attribute to null as to signal that the process has
	 * been stopped. Typically called from within an implementing class's stop
	 * method.
	 * 
	 */
	protected final void resetProcess() {
		this.process = null;
	}

	/**
	 * Wait for the supplied number of seconds.
	 * 
	 * @param timeout
	 *            time to wait in seconds. If <= 0 then the timeout used set to
	 *            45 seconds.
	 * @return the exit value of the process. By convention, 0 indicates normal
	 *         termination. If the process has not terminated after the supplied
	 *         number of seconds, then -1 is returned.
	 */
	public int waitFor(int timeoutSeconds) {
		if (timeoutSeconds <= 0) {
			timeoutSeconds = 45;
		}
		long millis = timeoutSeconds * 1000;
		int ret = -1;

		if (this.process == null) {
			throw new IllegalStateException("Process not started");
		}
		Timer timer = null;
		if (millis > 0) {
			timer = new Timer(true);
			TimerTask task = new TimerTask() {

				private Thread thread = Thread.currentThread();

				public void run() {
					this.thread.interrupt();
				}
			};
			timer.schedule(task, millis);
		}
		try {
			ret = process.waitFor();
		} catch (InterruptedException e) {
			// Reset interrupted status
			outputRedirect("waitFor() interrupted: "
					+ new Boolean(Thread.interrupted()).toString());
			process.destroy();
			outputRedirect("output: " + getOutput());
			outputRedirect("error: " + getError());
		} finally {
			if (millis > 0) {
				System.out.println("Timer cancelled");
				timer.cancel();
				timer = null;
			}
		}
		return ret;
	}// waitFor

	public Process start() {
		if (this.process != null) {
			throw new IllegalStateException("Process already started");
		}
		if (this.name == null || this.name.length() <= 0
				|| this.command == null || this.command.length() <= 0) {
			throw new IllegalArgumentException(
					"Invalid start and/or command argument");
		}
		outputRedirect("Start: " + this.name);
		try {
			StringBuffer commandPlusArgs = new StringBuffer(getCommand());
			for (Iterator<String> iter = getArguments().iterator(); iter
					.hasNext();) {
				String argument = iter.next();
				commandPlusArgs.append(" " + argument);
			}
			LOG.info("Spawning " + commandPlusArgs.toString());
			// Spawn the process
			if (working == null) {
				process = Runtime.getRuntime().exec(commandPlusArgs.toString(),
						null);
			} else {
				process = Runtime.getRuntime().exec(commandPlusArgs.toString(),
						null, working);
			}
			// Start threads to read the process stdout and stderr
			this.outputReader = new StreamRunner(process.getInputStream());
			new Thread(this.outputReader).start();
			this.errorReader = new StreamRunner(process.getErrorStream());
			new Thread(this.errorReader).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return process;
	}// start

	/**
	 * Call this method to force the destruction of the process
	 */
	public void destroy() {
		if (getProcess() != null)
			getProcess().destroy();
	}

	public abstract void stop();
}
