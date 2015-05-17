import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

public class RequestHandlerFactory {
	private BufferedReader in;
	private PrintStream out;

	private String verb;
	private String uri;
	private String version;
	private boolean keepAlive;

	public RequestHandlerFactory(BufferedReader in, PrintStream out) {
		this.in = in;
		this.out = out;
	}

	public RequestHandler getHandler() throws IOException {
		/* read Request-Line, might get a bad request */
		if (!parseRequestLine())
			return new BadRequestHandler(out);

		/* ignore all headers but Connection */
		String line;
		while ((line = in.readLine()) != null) {
			System.out.println("Got header: " + line);
			if (line.equals("Connection: keep-alive"))
				keepAlive = true;
			else if (line.equals(""))
				break;
		}

		/* act upon verb and connection */
		switch (verb) {
			case "GET":  return new GETRequestHandler(in, out, uri, keepAlive);
			case "HEAD": return new HEADRequestHandler(in, out, uri, keepAlive);
			default:     return new NotImplementedHandler(out);
		}
	}

	public boolean closeConnection() {
		return !keepAlive;
	}

	/**
	 * Parses only the Request-line of the Request (first line).
	 */
	private boolean parseRequestLine() throws IOException {
		keepAlive = false;
		String line = in.readLine();

		/* line might be null if the connection closed after SYN/SYNACK */
		if (line == null)
			return false;

		System.out.println("Request-line: " + line);
		String components[] = line.split("\\s");

		if (components.length != 3)
			return false;

		verb    = components[0];
		uri     = components[1];
		version = components[2];

		if (!uri.startsWith("/"))
			return false;
		uri = uri.substring(1, uri.length());

		if (!version.startsWith("HTTP/1."))
			return false;
		version = version.substring(1 + version.indexOf("."));
		if (version.equals("1"))
			keepAlive = true;
		else
			return false;

		return true;
	}
}
