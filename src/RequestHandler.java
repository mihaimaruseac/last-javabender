import java.io.BufferedReader;
import java.io.PrintStream;

public abstract class RequestHandler {
	protected BufferedReader in;
	protected PrintStream out;

	protected RequestHandler(BufferedReader in, PrintStream out) {
		this.in = in;
		this.out = out;
	}

	protected RequestHandler() {}

	public void handle() {
		parseBody();
		sendStatusCode();
		sendHeaders();
		out.println("");
		sendBody();
		out.flush();
	}

	/* need to override this everywhere */
	protected abstract void sendStatusCode();

	/* can override the following, if need be */
	protected void sendHeaders() {}
	protected void sendBody() {}
	protected void parseBody() {}
}
