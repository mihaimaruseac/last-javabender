import java.io.PrintStream;

public class BadRequestHandler implements RequestHandler {
	private PrintStream out;

	public BadRequestHandler(PrintStream out) {
		this.out = out;
	}

	public void handle() {
		out.println("HTTP/1.0 400 Bad Request");
		out.println("");
		out.flush();
	}
}
