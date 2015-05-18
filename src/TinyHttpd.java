import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

public class TinyHttpd {
	public static final Properties properties = readProperties();
	private static final int PORT = Integer.parseInt(properties.getProperty("PORT", "8888"));

	//private ServerSocket serverSocket;
	private SSLServerSocket serverSocket;

	public void init() {
		System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
		System.setProperty("javax.net.ssl.keyStore", "key.cert.pcks12");
		System.setProperty("javax.net.ssl.keyStorePassword", "Mihai");
		try {
			try {
				SSLServerSocketFactory factory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
				serverSocket = (SSLServerSocket)factory.createServerSocket(PORT);
				//serverSocket = new ServerSocket(PORT);
				System.out.println("Server socket created.");

				String enabledSuites[] = { "SSL_RSA_WITH_3DES_EDE_CBC_SHA" };
				serverSocket.setEnabledCipherSuites(enabledSuites);

				while (true) {
					System.out.println("Listening to a connection on the local port " +
							serverSocket.getLocalPort() + "...");
					//Socket client = serverSocket.accept();
					SSLSocket client = (SSLSocket)serverSocket.accept();
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
		properties.list(System.out);
		TinyHttpd server = new TinyHttpd();
		server.init();
	}
}
