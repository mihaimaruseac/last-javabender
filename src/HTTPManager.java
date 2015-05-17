import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class HTTPManager implements Runnable {
	private static final int SOCKET_TIMEOUT = Integer.parseInt(
			TinyHttpd.properties.getProperty("SOCKET_TIMEOUT", "3000"));
	public Socket client;

	public HTTPManager(Socket client) {
		this.client = client;
	}

	public void run() {
		System.out.println("\nA connection established with the remote port " +
				client.getPort() + " at " +
				client.getInetAddress().toString());

		try {
			client.setSoTimeout(SOCKET_TIMEOUT);
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintStream out = new PrintStream(client.getOutputStream());

			try {
				RequestHandlerFactory rhf = new RequestHandlerFactory(in, out);
				while (true) {
					rhf.getHandler().handle();
					if (rhf.closeConnection())
						return;
				}
			} finally {
				in.close();
				out.close();
				client.close();
				System.out.println("A connection is closed.");
			}
		} catch (SocketTimeoutException ex) {
			/* ignore */
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
