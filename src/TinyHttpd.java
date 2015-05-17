import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TinyHttpd {
	private static final int PORT = 8888;
	private ServerSocket serverSocket;

	public void init() {
		try {
			try {
				serverSocket = new ServerSocket(PORT);
				System.out.println("Server socket created.");

				while (true) {
					System.out.println("Listening to a connection on the local port " +
							serverSocket.getLocalPort() + "...");
					Socket client = serverSocket.accept();
					new Thread(new HTTPManager(client)).start();
				}
			} finally {
				serverSocket.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		TinyHttpd server = new TinyHttpd();
		server.init();
	}
}
