import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;

public class CompressedRequestHandler extends ItemRequestHandler {
	private ItemRequestHandler rh;

	private byte[] compresedOut;
	private int compressedLength = 0;

	public CompressedRequestHandler(ItemRequestHandler rh) {
		this.rh = rh;
		this.in = rh.in;
		this.out = rh.out;
	}

	@Override
	protected void sendStatusCode() {
		rh.sendStatusCode();
	}

	@Override
	protected void sendOtherHeaders() {
		rh.sendOtherHeaders();
		rh.out.println("Content-Encoding: gzip");
	}

	@Override
	protected void getBody() throws IOException {
		rh.getBody();

		/* obtain body from underlying object by flipping the streams */
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		changeOutput(new PrintStream(baos, true, "UTF-8"));
		rh.sendBody();
		restoreOutput();

		String body = baos.toString("UTF-8");
		if (body == null || body.equals(""))
			return; /* no body is being sent anyway */

		Deflater dfl = new Deflater();
		dfl.setInput(body.getBytes("UTF-8"));
		dfl.finish();

		compresedOut = new byte[body.length()];
		compressedLength = dfl.deflate(compresedOut);
		dfl.end();

		System.out.println("Compressed " + compressedLength + " " + compresedOut.length);
		setNewBody(compresedOut, compressedLength);
	}

	@Override
	protected void sendLengthHeader() {
		rh.sendLengthHeader();
	}

	@Override
	protected void sendBody() {
		rh.sendBody();
	}

	@Override
	protected void parseBody() throws IOException {
		rh.parseBody();
	}

	@Override
	protected void changeOutput(PrintStream ps) {
		rh.changeOutput(ps);
	}

	@Override
	protected void restoreOutput() {
		rh.restoreOutput();
	}

	@Override
	protected void setNewBody(byte newBody[], int sz) {
		rh.setNewBody(newBody, sz);
	}
}
