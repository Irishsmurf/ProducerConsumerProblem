import java.util.concurrent.Semaphore;
class Counter {

    private int count;
    private int in;
    private int out;

    public Counter() {

        count = in = out = 0;

    }

    public int delta() {

	return (in - out - count);

    }


    public void increment() {

	count++;
	in++;

    }

    public void decrement() {

	count--;
	out++;

    }
}

class Incrementer extends Thread {

    private Counter c;
	private Semaphore sem;
    public Incrementer(Counter c, Semaphore sem) {
		this.sem = sem;
        this.c = c;

    }

    public void run()
	{
		while (true) 
		{
			try
			{
				sem.acquire();
		 		c.increment();
			}
			catch (InterruptedException e){}
				sem.release();
		}
    }
}

class Decrementer extends Thread {

    private Counter c;
	private Semaphore sem;
    public Decrementer(Counter c, Semaphore sem) 
	{
		this.sem = sem;
        this.c = c;
    }

    public void run() {

	while (true) {
		try{
			sem.acquire();
	    	c.decrement();
		}catch(InterruptedException e){}
		sem.release();

	}
    }
}

public class Mutex {

    public static void main(String[] args) throws InterruptedException {
		
		Semaphore sem = new Semaphore(1);
        Counter c = new Counter();
        Incrementer i = new Incrementer(c,sem);
        Decrementer d = new Decrementer(c,sem);

        i.start();
        d.start();

	while (true) {

	    Thread.sleep(2000);

	    System.out.println("Delta: " + c.delta());
	}
    }
}
