import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

public abstract class ItemRequestHandler implements RequestHandler {
	private static final String ROOT = "res";

	private BufferedReader in;
	private PrintStream out;
	private String uri;

	public ItemRequestHandler(BufferedReader in, PrintStream out, String uri) {
		this.in = in;
		this.out = out;
		if (uri.equals(""))
			this.uri = "index.html";
		else
			this.uri = uri;
	}

	public void handle() {
		System.out.println("Client asked for >" + uri + "<");
		File file = new File(ROOT, uri);
		System.out.println("Got >" + file + "<");
		if (!file.exists() || !file.canRead() || !file.isFile()) {
			out.println("HTTP/1.0 404 Not Found");
			out.println("");
		} else {
			int len = (int)file.length();
			out.println("HTTP/1.0 200 OK");
			sendHeaders(len);
			out.println("");
			sendFile(file, len);
		}

		out.flush();
	}

	protected void sendHeaders(int len) {
		Date now = new Date();
		out.println("Content-Type: text/html");
		out.println("Content-Length: " + len);
		out.println("Server: MM's Java Server");
		out.println("Date: " + now);
		out.println("Last-Modified: " + now);
	}

	protected void sendFile(File file, int len) {
		try {
			DataInputStream fin = new DataInputStream(new FileInputStream(file));
			try {
				byte buf[] = new byte[len];
				fin.readFully(buf);
				out.write(buf, 0, len);
			} finally {
				fin.close();
			}
		} catch (IOException ex) {
			/* impossible anyway */
			ex.printStackTrace();
		}
	}
}
