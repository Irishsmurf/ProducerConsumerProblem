/*	
	David Kernan				59597883
	Owen Corrigan				59316418
	Cian Seosamh Coady		59046747
*/
import java.text.DecimalFormat;

class BoundedBuffer
{
	//Time keeping variable
	private long totalTime;
	private int itemsUsed;

	//array of values
	private int [] buffer;
	//array of start times for measuring how long values are stored
	private long [] startTimes;
	//Position of next location to store and read
	private int nextIn;
	private int nextOut;
	//Current number of values stored
	private int occupied;
	//Total number of items read in and out
	private int ins;
	private int outs;
	//Booleans to keep track of whether we can add or remove from queue
	private boolean dataAvailable;
	private boolean roomAvailable;
	
	//Calculates average time between storing and retrieving
	public double averageRunTime()
	{
		double d = ((totalTime + 0.0) / itemsUsed);
		//Round to 4 decimal places
		DecimalFormat twoDForm = new DecimalFormat("#.####");
		return Double.valueOf(twoDForm.format(d));
	}

	//Used to check for threading errors
	//should return 0 if done correctly
	public int bufferStatus()
	{
		return ins - outs - occupied;
	}
	
	
	//Constructor which takes size of array as parameter
	public BoundedBuffer(int size)
	{
		//Initialise time variables
		totalTime = 0;
		itemsUsed = 0;

		//Initialise array with size passed as parameter
		buffer = new int[size];
		startTimes = new long[size];
		nextIn = 0;
		nextOut = 0;
		occupied = 0; 
		ins = 0;
		outs = 0;
		dataAvailable = false;
		roomAvailable = true;
	}
	
	//Insert an item into the Queue
	public synchronized void insertItem(int item)
	{
		//Wait until room is available
		while(!roomAvailable)
		{
			try {
				wait();
			} catch (InterruptedException e) {	e.printStackTrace();}
		}
		
		ins++;
		//Store item in array
		buffer[nextIn] = item;
		//store time this was added
		startTimes[nextIn] = System.currentTimeMillis();
		//Update next position of next int to be read in
		nextIn = (nextIn + 1) % buffer.length ;
		occupied++;
		
		dataAvailable = true;
		if(occupied == buffer.length) roomAvailable = false; 
		//Let threads waiting threads know resource has become available
		notify();
	}
	
	//Return an item added to the queue
	public synchronized int removeItem()
	{
		//Wait until data available
		while(!dataAvailable)
		{
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
		outs++;
		//Store number located in this position to be returned later
		int tmp = buffer[nextOut];
		//Find total time it was stored and add to running total
		totalTime += System.currentTimeMillis() - startTimes[nextOut];
		//Keep track of how many items have been used
		itemsUsed++;
		//Update position to store next variable
		nextOut = (nextOut + 1) % buffer.length ;
		occupied--;
		
		roomAvailable = true;
		if(occupied == 0) dataAvailable = false;
		//Notify waiting threads resource has become available
		notify();
		return tmp;
	}
	
	//returns number of items stored
	public int size()
	{
		return occupied;
	}
}

//Watches the thread and prints out the current status at a predetermined interval (1 second)
class Watcher extends Thread
{
	private BoundedBuffer buffer;
	public Watcher(BoundedBuffer buffer0)
	{
		buffer = buffer0;
	}
	
	//Standard run method for the Watcher Thread.
	public void run()
	{
			try
			{
				//Infinite loop to run until thread is interrupted
				while(true)
				{
					sleep(1000);//Timer
					System.out.println("Delta = " + buffer.bufferStatus() 
					+ " Occupied = " + buffer.size());
				}
			}
			catch(InterruptedException e){}
			finally{
				System.out.println("Goodbye from Watcher\nAverage wait time: " 
				+ buffer.averageRunTime()+"ms");
			}
	}
}

//Produces random numbers to insert into a boundedBuffer
class Producer extends Thread
{
	private BoundedBuffer buffer;
	
	public Producer(BoundedBuffer buffer0)
	{
		buffer = buffer0;
	}
	
	//Standard run method for the Producer thread.
	public void run()
	{
		try
		{
			//Infinite Loop that runs until the thread has been interrupted
			while(true)
			{
				//Random Number Generator
				int rand = (int) (100.0 * Math.random());
				buffer.insertItem(rand);
				 //Put the thread to sleep for n, where 0 <= N < 100ms
				sleep((int)(100.0 * Math.random()));
			}
		}
		catch(InterruptedException e){}
		finally
		{
			System.out.println("Goodbye from Producer");
		}
	}
}

//Consumer thread that removes from the BounderBuffer
class Consumer extends Thread
{
	private BoundedBuffer buffer;
	
	public Consumer(BoundedBuffer buffer0)
	{
		buffer = buffer0;
	}
	
	//Standard Run Method for the consumer thread
	public void run()
	{
		int item = 0;
		try
		{
			while(true)
			{
				//Removes an item from the BoundedBuffer
				item = buffer.removeItem();
				//Put the thread to sleep for n, where 0 <= N < 100ms
				sleep((int)(100.0 * Math.random()));
			}
		}
		catch(InterruptedException e){}
		finally
		{
			System.out.println("Goodbye from Consumer");
		}
	}
}

class ProducerConsumerProblem
{
	public static void main(String [] args)
	{
		//Initialisation section
		BoundedBuffer bf = new BoundedBuffer(Integer.parseInt(args[0]));
		Producer pro = new Producer(bf);
		Consumer con = new Consumer(bf);
		Watcher man = new Watcher(bf);
		
		//Start the threads
		pro.start();
		con.start();
		man.start();
		
		//Make the main thread sleep for 1 minute
		try {
			Thread.sleep(60000);		
			//After this time kill the running threads and end the program
			pro.interrupt();
			//Wait for pro to finish before killing other threads

			con.interrupt();
			//Wait for con to finish before killing other threads

			man.interrupt();
			//Wait for pro to finish before ending the program

		} catch (InterruptedException e) {e.printStackTrace();}
	}
}
