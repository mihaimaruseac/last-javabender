import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class HTTPManager implements Runnable {
	public Socket client;

	public HTTPManager(Socket client) {
		this.client = client;
	}

	public void run() {
		System.out.println("\nA connection established with the remote port " +
				client.getPort() + " at " +
				client.getInetAddress().toString());

		try {
			client.setSoTimeout(30000);
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintStream out = new PrintStream(client.getOutputStream());

			try {
				new RequestHandlerFactory(in, out).getHandler().handle();
			} finally {
				in.close();
				out.close();
				client.close();
				System.out.println("A connection is closed.");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
