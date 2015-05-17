import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

public class RequestHandlerFactory {
	private BufferedReader in;
	private PrintStream out;

	public RequestHandlerFactory(BufferedReader in, PrintStream out) {
		this.in = in;
		this.out = out;
	}

	/**
	 * Parses only the Request-line of the Request (first line).
	 */
	public RequestHandler getHandler() throws IOException {
		String line = in.readLine();
		String components[] = line.split("\\s");

		if (components.length != 3)
			return new BadRequestHandler(out);

		String verb    = components[0];
		String uri     = components[1];
		String version = components[2];

		if (!uri.startsWith("/"))
			return new BadRequestHandler(out);
		uri = uri.substring(1, uri.length());

		switch (verb) {
			case "GET":  return new GETRequestHandler(in, out, uri);
			case "HEAD": return new HEADRequestHandler(in, out, uri);
			default:     return new NotImplementedHandler(out);
		}
	}
}

