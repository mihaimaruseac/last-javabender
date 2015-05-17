import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.File;

public class HEADRequestHandler extends ItemRequestHandler {
	public HEADRequestHandler(BufferedReader in, PrintStream out, String uri) {
		super(in, out, uri);
	}

	/**
	 * We don't send the file here.
	 */
	@Override
	protected void sendFile(File file, int len) {}
}
