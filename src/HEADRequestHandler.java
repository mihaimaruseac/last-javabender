import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class HEADRequestHandler extends ItemRequestHandler {
	public HEADRequestHandler(BufferedReader in, PrintStream out, String uri, boolean keepAlive) {
		super(in, out, uri, keepAlive);
	}

	/**
	 * We don't send the file here.
	 */
	@Override
	protected void sendFile(File file, int len) {}
}
