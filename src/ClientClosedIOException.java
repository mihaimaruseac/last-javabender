import java.io.IOException;

public class ClientClosedIOException extends IOException {
	public ClientClosedIOException(String msg) {
		super(msg);
	}
}
