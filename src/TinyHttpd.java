import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class TinyHttpd {
	public static final Properties properties = readProperties();
	private static final int PORT = Integer.parseInt(properties.getProperty("PORT", "8888"));

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
					StaticThreadPool.getInstance().execute(new HTTPManager(client));
				}
			} finally {
				serverSocket.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static Properties readProperties() {
		Properties properties = new Properties();

		try {
			InputStream in = new FileInputStream(new File("server.properties"));
			properties.load(in);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return properties;
	}

	public static void main(String[] args) {
		TinyHttpd server = new TinyHttpd();
		server.init();
	}
}
