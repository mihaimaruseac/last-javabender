import java.io.BufferedReader;
import java.io.PrintStream;

public class HEADRequestHandler extends ItemRequestHandler {
	public HEADRequestHandler(BufferedReader in, PrintStream out, String uri) {
		super(in, out, uri);
	}

	/**
	 * We don't send the file here.
	 */
	@Override
	protected void sendBody() {}
}
