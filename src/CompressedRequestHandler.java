import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;

public class CompressedRequestHandler extends ItemRequestHandlerDecorator {
	public CompressedRequestHandler(ItemRequestHandler rh) {
		super(rh);
	}

	@Override
	protected void sendOtherHeaders() {
		super.sendOtherHeaders();
		out.println("Content-Encoding: gzip");
	}

	@Override
	protected void getBody() throws IOException {
		super.getBody();

		ByteArrayOutputStream baos = new ByteArrayOutputStream(rh.buf.length);
		GZIPOutputStream gos = new GZIPOutputStream(baos);
		gos.write(rh.buf);
		gos.close();

		rh.buf = baos.toByteArray();
		rh.resourceLength = rh.buf.length;
	}
}
