import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

public abstract class ItemRequestHandler extends RequestHandler {
	private static final String ROOT = TinyHttpd.properties.getProperty("ROOT", "res");

	protected String uri;
	private boolean keepAlive;

	private boolean is200 = true;
	protected int resourceLength = 0;
	private File theFile;
	protected byte[] buf;

	public ItemRequestHandler(BufferedReader in, PrintStream out, String uri) {
		super(in, out);
		this.uri = uri;
		if (uri.equals(""))
			this.uri = TinyHttpd.properties.getProperty("INDEX", "index.html");
	}

	@Override
	protected void parseBody() throws IOException {
		System.out.println("Client asked for >" + uri + "<");
		theFile = new File(ROOT, uri);
		System.out.println("Got >" + theFile + "<");

		if (!theFile.exists() || !theFile.canRead() || !theFile.isFile()) {
			is200 = false;
			return;
		}

		resourceLength = (int)theFile.length();
	}

	@Override
	protected void sendStatusCode() {
		if (!is200)
			out.println("HTTP/1.0 404 Not Found");
		else
			out.println("HTTP/1.0 200 OK");
	}

	@Override
	protected void sendLengthHeader() {
		if (!is200)
			return;

		out.println("Content-Length: " + resourceLength);
	}

	@Override
	protected void sendOtherHeaders() {
		if (!is200)
			return;

		Date now = new Date();
		out.println("Content-Type: text/html");
		out.println("Server: MM's Java Server");
		out.println("Date: " + now);
		out.println("Last-Modified: " + now);
	}

	@Override
	protected void getBody() throws IOException {
		FileCache fc;
		if (TinyHttpd.properties.getProperty("CACHE_TYPE", "LRU").equals("LRU"))
			fc = FileCacheLRU.getInstance();
		else
			fc = FileCacheLFU.getInstance();

		buf = fc.fetch(theFile);
	}

	@Override
	protected void sendBody() {
		out.write(buf, 0, resourceLength);
	}
}
