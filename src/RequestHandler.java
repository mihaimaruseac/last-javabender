import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

public abstract class RequestHandler {
	protected BufferedReader in;
	protected PrintStream out;
	private PrintStream bkp = null;

	protected RequestHandler(BufferedReader in, PrintStream out) {
		this.in = in;
		this.out = out;
	}

	protected RequestHandler() {}

	public void handle() throws IOException {
		parseBody();
		getBody();
		sendStatusCode();
		sendLengthHeader();
		sendOtherHeaders();
		out.println("");
		sendBody();
		out.flush();
	}

	/* need to override this everywhere */
	protected abstract void sendStatusCode();

	/* can override the following, if need be */
	protected void sendLengthHeader() {}
	protected void sendOtherHeaders() {}
	protected void sendBody() {}
	protected void parseBody() throws IOException {}
	protected void getBody() throws IOException {}

	/* allow changing the output type */
	protected void changeOutput(PrintStream ps) {
		if (bkp == null)
			bkp = out;

		out = ps;
	}

	protected void restoreOutput() {
		if (bkp != null) {
			out = bkp;
			bkp = null;
		}
	}
}
