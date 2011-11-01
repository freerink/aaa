/**
 * @author not specified!
 * \page AbstractJavaProcess
 * 
 * \section goal doel
 * Deze class voegt de get~ en setClassPath() methodes toe aan AbstractProcess.
 * 
 */
package name.reerink.aaa;

public abstract class AbstractJavaProcess extends AbstractProcess {
	private String classPath = System.getProperty("java.class.path");

	private String className;

	public AbstractJavaProcess() {

	}

	public AbstractJavaProcess(String name, String className) {
		this(name, "unit-test", className);
	}

	public AbstractJavaProcess(String name, String deploymentId,
			String className) {
		super(name);
		this.className = className;
		String command = "java "
				+ "-cp \"" + this.classPath + "\" " 
				+ this.className;
		setCommand(command);
	}

	public String getClassPath() {
		return this.classPath;
	}

	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

}
