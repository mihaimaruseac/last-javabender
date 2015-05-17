import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.File;

public class TinyHttpd2 {
	private static final int PORT = 8888;
	private static final String ROOT = "res";
	private ServerSocket serverSocket;

	public void init() {
		try {
			try {
				serverSocket = new ServerSocket(PORT);
				System.out.println("Socket created.");

				while (true) {
					System.out.println("Listening to a connection on the local port " +
							serverSocket.getLocalPort() + "...");
					Socket client = serverSocket.accept();
					System.out.println("\nA connection established with the remote port " +
							client.getPort() + " at " +
							client.getInetAddress().toString());
					executeCommand(client);
				}
			} finally {
				serverSocket.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void executeCommand (Socket client) {
		try {
			client.setSoTimeout(30000);
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintStream out = new PrintStream(client.getOutputStream());

			try {
				System.out.println("I/O setup done");
				String line = in.readLine();
				String filename = "index.html";

				//while ( in.ready() && line != null ) {
				while (line != null) {
					System.out.println(line);
					if (line.startsWith("GET")) {
						String l = line.substring(1 + line.indexOf("/"));
						int index = l.indexOf(" ");
						if (index > 0)
							filename = l.substring(0, index);
					}
					if(line.equals("")) break;
					line = in.readLine();
				}

				File file = new File(ROOT, filename);
				System.out.println(file.getName() + " requested.");
				sendFile(out, file);
				out.flush();
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

	private void sendFile(PrintStream out, File file) {
		try {
			DataInputStream fin = new DataInputStream(new FileInputStream(file));
			try {
				out.println("HTTP/1.0 200 OK");
				out.println("Content-Type: text/html");

				int len = (int)file.length();
				out.println("Content-Length: " + len);
				out.println("");

				byte buf[] = new byte[len];
				fin.readFully(buf);
				out.write(buf, 0, len);
				out.flush();
			} finally {
				fin.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		TinyHttpd2 server = new TinyHttpd2();
		server.init();
	}
}
