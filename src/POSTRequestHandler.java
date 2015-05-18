import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.StringBuilder;

public class POSTRequestHandler extends ItemRequestHandler {
	private int contentLength;
	private boolean badFormat = false;
	private StringBuilder sb = new StringBuilder();

	private char rqBuf[];

	public POSTRequestHandler(BufferedReader in, PrintStream out, String uri, int contentLength) {
		super(in, out, uri);
		this.contentLength = contentLength;
		badFormat = contentLength == 0;
	}

	@Override
	protected void parseBody() throws IOException {
		if (badFormat)
			return;

		rqBuf = new char[contentLength];
		in.read(rqBuf, 0, contentLength);
	}

	@Override
	protected void getBody() throws IOException {
		String buff = new String(rqBuf);
		String components[] = buff.split("&");

		sb.append("<html><head><title>");
		sb.append(uri);
		sb.append("</title></head><body><p>Uploaded to ");
		sb.append(uri);
		sb.append("</p><table><tr><th>Variable</th><th>Value</th></tr>");

		for (String s: components) {
			String parts[] = s.split("=");
			if (parts.length != 2) {
				badFormat = true;
				return;
			}

			sb.append("<tr><td>");
			sb.append(parts[0]);
			sb.append("</td><td>");
			sb.append(parts[1]);
			sb.append("</tr>");
		}

		sb.append("</table></body></html>");

		resourceLength = sb.length();
		buf = sb.toString().getBytes();
	}

	@Override
	protected void sendStatusCode() {
		if (badFormat)
			out.println("HTTP/1.0 400 Bad Request");
		else
			out.println("HTTP/1.0 200 OK");
	}

	@Override
	protected void sendLengthHeader() {
		if (badFormat)
			return;

		super.sendLengthHeader();
	}

	@Override
	protected void sendOtherHeaders() {
		if (badFormat)
			return;

		super.sendOtherHeaders();
	}


	@Override
	protected void sendBody() {
		if (badFormat)
			return;

		super.sendBody();
	}
}
