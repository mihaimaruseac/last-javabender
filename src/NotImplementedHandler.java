import java.io.PrintStream;

public class NotImplementedHandler implements RequestHandler {
	private PrintStream out;

	public NotImplementedHandler(PrintStream out) {
		this.out = out;
	}

	public void handle() {
		out.println("HTTP/1.0 501 Not Implemented");
		out.println("");
		out.flush();
	}
}
