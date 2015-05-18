import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLServerSocket;

public class TinyHttpd {
	public static final Properties properties = readProperties();
	private static final int PORT = Integer.parseInt(properties.getProperty("PORT", "8888"));

	private ServerSocket serverSocket;

	public void init(boolean secure) {
		try {
			try {
				serverSocket = secure ? getSafeServerSocket() : getUnsafeServerSocket();
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

	private ServerSocket getUnsafeServerSocket() throws IOException {
		return new ServerSocket(PORT);
	}

	private ServerSocket getSafeServerSocket() throws IOException {
		System.setProperty("javax.net.ssl.keyStoreType", properties.getProperty("KEY_TYPE", "PKCS12"));
		System.setProperty("javax.net.ssl.keyStore", properties.getProperty("KEY_FILE", "key/key.cert.pcks12"));
		System.setProperty("javax.net.ssl.keyStorePassword", properties.getProperty("KEY_PASS", "Mihai"));

		SSLServerSocketFactory factory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
		SSLServerSocket sSocket = (SSLServerSocket)factory.createServerSocket(PORT);

		// TODO: properties
		String enabledSuites[] = properties.getProperty("CIPHER_SUITES", "SSL_RSA_WITH_3DES_EDE_CBC_SHA").split(",");
		for (String s : enabledSuites)
			System.out.println(s);
		sSocket.setEnabledCipherSuites(enabledSuites);

		return sSocket;
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
		properties.list(System.out);
		TinyHttpd server = new TinyHttpd();
		server.init(new Boolean(properties.getProperty("SECURE", "false")));
	}
}
