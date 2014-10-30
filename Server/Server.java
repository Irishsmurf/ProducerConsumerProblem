import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

// One thread per connection, this is it
class ServerThread extends Thread 
{

	// The socket passed from the creator

	//Initialisation of Variables
	private Socket socket = null;
	private PrintWriter socketOut;
	private BufferedReader br;
	private BlockingQueue<String> messages; //Message Buffer
	private String msg;
	private String name;
	private ArrayList<ServerThread> clients; // Connected Clients List

	public ServerThread(Socket socket, BlockingQueue<String> messages, 
	ArrayList<ServerThread>clients)
	{

		this.socket = socket;
		this.messages = messages;
		this.clients = clients;
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

	//Write to Socket.
	public void write(String msg)
	{
		socketOut.println(msg);
	}
	// Handle the connection
	public void run() 
	{
		try 
		{
			//Read in first Line as sent by client which will be the username.
			name = br.readLine();
			messages.add(name+" has joined the chat server...");
			while(true)
			{
				//Read in the lines from the BufferedReader
				msg = br.readLine();
				//make sure the client is still alive, if it sends null 
				//the client is no longer responding and can be assumed dead.
				if(msg != null)
				{
					messages.add(name+" says: "+msg);
				}
				else
				{
					//Connection closed;
					messages.add(name+" has left the channel...");
					//remove from Connected clients list.
					clients.remove(clients.indexOf(this));
					System.out.println("Clients: "+clients.size());		
					socket.close();
					break;//exit while loop
				}
			}

		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();

		}
	}
}

//Removes messages from buffer and prints them to all the clients in the Client list.
class Consumer extends Thread 
{
	private BlockingQueue<String> messages; //Message Buffer
	private ArrayList<ServerThread> clients;//Connected Client list.
	public Consumer(BlockingQueue<String> messages, 
	ArrayList<ServerThread> clients)
	{
		this.messages = messages;
		this.clients = clients;
	}

	public void run()
	{
		while(true)
		{
			try
			{
				//take() waits until there is an 
				//item at the head of the buffer
				String message = messages.take();
				//Take message and iterate through 
				//all currently connected clients.
				for(ServerThread c:clients)
				{
					//Will then write to the Socket.
					c.write(message);
				}
			}
			catch(InterruptedException e)
			{
				System.out.println("BlockingQueue Interrupted");
			}
		}
	}
}
// The server
public class Server {
	public static void main(String[] args) throws IOException {

		//Client List
		ArrayList<ServerThread> clients = new ArrayList<ServerThread>();
		//Message Buffer
		BlockingQueue<String> messages = new LinkedBlockingQueue<String>();

		ServerSocket serverSocket = null;
		ServerThread client;
		Consumer printer = new Consumer(messages, clients);
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
			//Wait for new connection.
			client = new ServerThread(serverSocket.accept(), 
			messages, clients);

			client.start();//Start the thread
			clients.add(client);//Add to the client list.

			//Output the number of connected Clients
			System.out.println("Clients: "+ clients.size());
		}
	}
}
