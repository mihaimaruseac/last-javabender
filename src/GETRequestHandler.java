import java.io.BufferedReader;
import java.io.PrintStream;

public class GETRequestHandler extends ItemRequestHandler {
	public GETRequestHandler(BufferedReader in, PrintStream out, String uri) {
		super(in, out, uri);
	}
}
