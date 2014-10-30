import java.io.*;
import java.net.*;

class EchoClient {

	// Have a conversation with the echo server on vinson
	public static void main(String[] args) throws IOException {

		// We need a socket to talk to another machine
		Socket echoSocket = null;

		// We use a PrintWriter to write to the socket
		PrintWriter socketOut = null;

		// We use a BufferedReader to read from the socket
		BufferedReader socketIn = null;

		// We use a BufferedReader to read from the user
		BufferedReader userIn =
		new BufferedReader(new InputStreamReader(System.in));

		// We store user input in a string
		String userInput;

		try {

			// Create socket and connect to the echo server on vinson (port 7)
			echoSocket = new Socket("vinson.computing.dcu.ie", 7);

			// Attach a printer to the socket's output stream
			socketOut = new PrintWriter(echoSocket.getOutputStream(), true);

			// Attach a reader to the socket's input stream
			socketIn = new BufferedReader(
			new InputStreamReader(echoSocket.getInputStream()));

		} catch (UnknownHostException e) {

			// Simply exit if vinson is unreachable
			System.err.println("Don't know about host: vinson");
			System.exit(1);
		}

		// Attach a reader to standard input
		userIn = new BufferedReader(new InputStreamReader(System.in));

		// While there is something to read from the user...
		while ((userInput = userIn.readLine()) != null) {

			// Write it to the socket
			socketOut.println(userInput);

			// Print what we get back
			System.out.println("echo: " + socketIn.readLine());
		}

		// Close
		socketOut.close();
		socketIn.close();
		userIn.close();
		echoSocket.close();
	}
}
