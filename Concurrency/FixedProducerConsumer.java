/*
 * Now we will add the code to take care of the issues
 */
class CubbyHole {

    private int contents;

    /*
     * Add a boolean variable "occupied" which will be used as follows:
     *
     * 1. When false it indicates there is no data in the CubbyHole and so it
     *    is safe for the Producer to put in new data but the Consumer must
     *    wait.
     *
     * 2. When true it indicates that there is data in the CubbyHole and so it
     *    is safe for the Consumer to take it out but the Producer must
     *    wait.
     *
     * Initialise "occupied" to the appropriate value.
     */
	boolean occupied = false;
    /*
     * The method below modifies CubbyHole. CubbyHole is shared by threads. We
     * only want one thread to be modifying the CubbyHole at any instant.
     * Modify the method declaration to ensure only one thread is ever
     * updating CubbyHole.
     */
    public synchronized int get() {

	/*
	 * We want to wait before we take any data from the CubbyHole until
	 * there is data available to take. Insert the appropriate condition
	 * in the while loop.
	 */
        while (!occupied) {

            try {
				wait();
					
		/*
		 * While there is nothing available put this thread to sleep
		 * i.e. ask it to wait.
		 */

            } catch (InterruptedException e) { }
        }
	
	/*
	 * We have taken something from the CubbyHole so the status of
	 * "occupied" has changed. Set it to its new value.
	 */
		occupied = false;
	/*
	 * Some sleeping thread may be waiting on room to become available so
	 * it can put something into the CubbyHole. Wake those threads up i.e.
	 * notify them of a change in "occupied".
	 */
	notify();
	/* Display message */
	System.out.println("Consumer (" + Thread.currentThread().getName() +
	    ") got " + contents);

        return contents;
    }

    /*
     * The method below modifies CubbyHole. CubbyHole is shared by threads. We
     * only want one thread to be modifying the CubbyHole at any instant.
     * Modify the method declaration to ensure only one thread is ever updating
     * CubbyHole.
     i*/
    public synchronized void put(int value) {

	/*
	 * We want to wait before we put data into the CubbyHole until there is
	 * room available to put it in (i.e. the CubbyHole is unoccupied).
	 * Insert the appropriate condition in the while loop.
	 */
        while (occupied) {

            try {

		/*
		 * While there is no room available, put this thread to sleep
		 * i.e. ask it to wait.
		 */
			wait();
            } catch (InterruptedException e) { }
        }
		
        contents = value;

	/*
	 * We have taken something from the CubbyHole so the status of
	 * "occupied" has changed. Set it to its new value.
	 */
		occupied = true;
	/*
	 * Some sleeping thread may be waiting to take some data from the
	 * CubbyHole. Wake those threads up i.e. notify them of a change in
	 * "occupied".
	 */
		notify();
	/* Display message */
	System.out.println("Producer (" + Thread.currentThread().getName() +
	    ") put " + contents);
    }
}

/*
 * This is the Producer thread, it attempts to insert data into the CubbyHole.
 * The private number field below we use as a simple thread ID. After inserting
 * an item it sleeps for a few ms to simulate random data arrival times.
 */
class Producer extends Thread {

    private CubbyHole cubbyhole;

    public Producer(CubbyHole c) {
        cubbyhole = c;
    }

    public void run() {

	try {

	    for (int i = 0; i < 10; i++) {
		cubbyhole.put(i);
                sleep((int)(Math.random() * 100));
	    }

	} catch (InterruptedException e) { }

	finally {

	    System.out.println("Goodbye from Producer ("
	        + Thread.currentThread().getName() + ")");

	}
    }
}

/*
 * This is the Consumer thread, it attempts to take data out of the CubbyHole.
 * The private number field below we use as a simple thread ID.
 */
class Consumer extends Thread {

    private CubbyHole cubbyhole;

    public Consumer(CubbyHole c) {
        cubbyhole = c;
    }

    public void run() {

        int value = 0;

	try {

	    for (int i = 0; i < 10; i++) {
		value = cubbyhole.get();
		sleep((int)(Math.random() * 100));
	    }

	} catch (InterruptedException e) { }

	System.out.println("Goodbye from Consumer ("
	    + Thread.currentThread().getName() + ")");
    }
}

/*
 * Create and start our Producer and Consumer threads
 */
public class FixedProducerConsumer {

    public static void main(String[] args) throws InterruptedException {

        CubbyHole c = new CubbyHole();
        Producer p1 = new Producer(c);
        Consumer c1 = new Consumer(c);

        p1.start();
        c1.start();
	p1.join();
	c1.join();
    }
}
