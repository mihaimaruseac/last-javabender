import java.io.BufferedReader;
import java.io.PrintStream;

public class KeepAliveRequestHandler extends RequestHandler {
	private RequestHandler rh;

	public KeepAliveRequestHandler(RequestHandler rh) {
		this.rh = rh;
		this.in = rh.in;
		this.out = rh.out;
	}

	protected void sendStatusCode() {
		rh.sendStatusCode();
	}

	protected void sendHeaders() {
		rh.sendHeaders();
		rh.out.println("Connection: keep-alive");
	}

	protected void sendBody() {
		rh.sendBody();
	}

	protected void parseBody() {
		rh.parseBody();
	}
}
