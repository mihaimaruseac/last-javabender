import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

public class RequestHandlerFactory {
	private BufferedReader in;
	private PrintStream out;

	private String verb;
	private String uri;
	private String version;
	private boolean keepAlive = false;
	private String compressedContent = "";

	public RequestHandlerFactory(BufferedReader in, PrintStream out) {
		this.in = in;
		this.out = out;
	}

	public RequestHandler getHandler() throws IOException {
		RequestHandler rh = getBaseHandler();

		if (rh instanceof ItemRequestHandler) {
			if (keepAlive)
				rh = new KeepAliveRequestHandler((ItemRequestHandler)rh);

			switch (compressedContent) {
			case "gzip":    rh = new CompressedRequestHandler((ItemRequestHandler)rh); break;
			case "deflate": rh = new DeflatedRequestHandler((ItemRequestHandler)rh); break;
			default: /* do nothing */
			}
		}

		return rh;
	}

	public boolean closeConnection() {
		return !keepAlive;
	}

	/**
	 * Gets the base handler, can be decorated for keep-alive connections
	 */
	private RequestHandler getBaseHandler() throws IOException {
		/* read Request-Line, might get a bad request */
		if (!parseRequestLine()) {
			keepAlive = false;
			return new BadRequestHandler(in, out);
		}

		/* ignore all headers but Connection, Content-Length and
		 * Accept-Encoding
		 */
		String line;
		int contentLength = 0;
		while ((line = in.readLine()) != null) {
			System.out.println("Got header: " + line);
			if (line.equals("Connection: keep-alive"))
				keepAlive = true;
			if (line.startsWith("Accept-Encoding:"))
				parseAcceptEncoding(line);
			if (line.startsWith("Content-Length:"))
				contentLength = Integer.parseInt(line.substring(1 + line.indexOf(" ")));
			else if (line.equals(""))
				break;
		}

		/* act upon verb and connection */
		switch (verb) {
			case "GET":  return new GETRequestHandler(in, out, uri);
			case "HEAD": return new HEADRequestHandler(in, out, uri);
			case "POST": return new POSTRequestHandler(in, out, uri, contentLength);
			default:     return new NotImplementedHandler(in, out);
		}
	}

	/**
	 * Parses only the Request-line of the Request (first line).
	 */
	private boolean parseRequestLine() throws IOException {
		String line = in.readLine();

		/* line might be null if the connection closed after SYN/SYNACK */
		if (line == null)
			throw new ClientClosedIOException("Client connection closed!");

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
		if (version.equals("0"))
			keepAlive = false;
		else if (version.equals("1"))
			keepAlive = true;
		else
			return false;

		return true;
	}

	/**
	 * Parses Accept-Encoding header and determines whether we can send gzip
	 * encoded data to the client (if he supports this)
	 */
	private void parseAcceptEncoding(String line) {
		line = line.substring(1 + line.indexOf(" "));
		if (line.contains("*") || line.startsWith("gzip"))
			compressedContent = "gzip";
		else if (line.contains("deflate"))
			compressedContent = "deflate";
		else
			compressedContent = "";
	}
}
