import java.io.BufferedReader;
import java.io.PrintStream;

public class NotImplementedHandler extends RequestHandler {
	public NotImplementedHandler(BufferedReader in, PrintStream out) {
		super(in, out);
	}

	@Override
	protected void sendStatusCode() {
		out.println("HTTP/1.0 501 Not Implemented");
	}
}
