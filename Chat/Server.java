import java.net.*;
import java.io.*;
import java.util.*;

// One thread per connection, this is it
class ServerThread extends Thread 
{

		// The socket passed from the creator
		private Socket socket = null;
		private PrintWriter socketOut;
		private BufferedReader br;
		private Queue<String> messages;
		String name;
		public ServerThread(Socket socket, Queue<String> messages) 
		{
			
			this.socket = socket;
			this.messages = messages;
			try
			{
				socketOut = new PrintWriter(socket.getOutputStream(),true);
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			}
			catch(IOException e)
			{
				System.out.println("Constructor Error");
			}
		}
		
		public void write(String msg)
		{
			socketOut.println(msg);
		}
		// Handle the connection
		public void run() 
		{
			try 
			{
				name = br.readLine();
				messages.add(name+" has joined the chat server...");
				while(true)
				{
					messages.add(name+" says: "+br.readLine());
				}
			}
			catch (SocketException e)
			{
				messages.add(name+" has left the channel...");
			}
			catch (IOException e) 
			{
				e.printStackTrace();

			}
		}
	}

	
class Watcher extends Thread
{
	private ArrayList<ServerThread> clients = new ArrayList<ServerThread>();
	private Queue<String> messages;
	
	public Watcher(Queue<String> messages)
	{
		this.messages = messages;
	}
	
	public void add(ServerThread client)
	{
		clients.add(client);
	}
	
	public void run()
	{
		while(true)
		{
			String message = messages.poll();
			if(message != null)
			{
				for(ServerThread c:clients)
				{
					c.write(message);
					
				}
			}
			
		}
	}
}
// The server
public class Server {

	public static void main(String[] args) throws IOException {

		// The server socket, connections arrive here
		Queue<String> messages = new LinkedList<String>();
		ServerSocket serverSocket = null;
		ServerThread client;
		Watcher printer = new Watcher(messages);
		printer.start();
		try 
		{
			// Listen on on port 7777
			 serverSocket = new ServerSocket(7777);

		}
		catch (IOException e) 
		{

			System.err.println("Could not listen on port: 7777");
			System.exit(-1);

		}

		// Loop forever
		while (true) 
		{
				client = new ServerThread(serverSocket.accept(), messages);
				client.start();
				printer.add(client);

		}
	}
}
