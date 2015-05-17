import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

public class KeepAliveRequestHandler extends ItemRequestHandler {
	private ItemRequestHandler rh;

	public KeepAliveRequestHandler(ItemRequestHandler rh) {
		this.rh = rh;
		this.in = rh.in;
		this.out = rh.out;
	}

	@Override
	protected void getBody() throws IOException {
		rh.getBody();
	}

	@Override
	protected void sendStatusCode() {
		rh.sendStatusCode();
	}

	@Override
	protected void sendLengthHeader() {
		rh.sendLengthHeader();
	}

	@Override
	protected void sendOtherHeaders() {
		rh.sendOtherHeaders();
		rh.out.println("Connection: keep-alive");
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
}
