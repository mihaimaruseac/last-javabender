import java.io.BufferedReader;
import java.io.PrintStream;

public class BadRequestHandler extends RequestHandler {
	public BadRequestHandler(BufferedReader in, PrintStream out) {
		super(in, out);
	}

	@Override
	protected void sendStatusCode() {
		out.println("HTTP/1.0 400 Bad Request");
	}
}
