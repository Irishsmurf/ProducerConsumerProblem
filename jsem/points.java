import java.util.concurrent.Semaphore;

class PointA extends Thread 
{
	Semaphore sem;
	public PointA(Semaphore sem0) 
	{
		sem = sem0;
	}
	public void run() 
	{

		try 
		{
			sleep((int)(Math.random() * 100));
		}
		catch (InterruptedException e) { }
		System.out.println("Point A reached");
		sem.release();
												    }
}

class PointB extends Thread 
{
	Semaphore sem;
	public PointB(Semaphore sem0) 
	{
		sem = sem0;
	}
    public void run() 
	{
		
		try 
		{
			sleep((int)(Math.random() * 100));
			sem.acquire();
		} 
		catch (InterruptedException e) { }
		System.out.println("Point B reached");
		
	}
}

class Points 
{
    public static void main(String[] args) throws InterruptedException 
	{
		Semaphore sem = new Semaphore(0);
        PointA a = new PointA(sem);
	    PointB b = new PointB(sem);
		b.start();
		a.start();
		b.join();
		a.join();
	}
}
