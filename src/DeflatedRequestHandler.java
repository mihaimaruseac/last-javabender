import java.io.IOException;
import java.util.zip.Deflater;

public class DeflatedRequestHandler extends ItemRequestHandlerDecorator {
	private byte[] compresedOut;
	private int compressedLength = 0;

	public DeflatedRequestHandler(ItemRequestHandler rh) {
		super(rh);
	}

	@Override
	protected void sendOtherHeaders() {
		super.sendOtherHeaders();
		out.println("Content-Encoding: deflate");
	}

	@Override
	protected void getBody() throws IOException {
		super.getBody();

		Deflater dfl = new Deflater();
		dfl.setInput(rh.buf);
		dfl.finish();

		compresedOut = new byte[rh.resourceLength];
		compressedLength = dfl.deflate(compresedOut);
		dfl.end();

		rh.buf = compresedOut;
		rh.resourceLength = compressedLength;
	}
}
