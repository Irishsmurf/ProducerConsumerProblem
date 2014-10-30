/*
 * From http://java.sun.com/docs/books/tutorial/essential/threads/
 *
 * This is the CubbyHole class. We can think of it as defining a pigeon-hole
 * which can hold a single item. A Producer thread continuously puts items
 * into the pigeon-hole and a Consumer thread continuously takes them out.
 * 
 * We want to synchronise the Producer and Consumer threads such that each
 * time the Producer puts something in the CubbyHole the Consumer is informed,
 * it takes that something out and informs the Producer it has got it so the
 * Producer can put something else in etc. etc. Thus the Producer and Consumer
 * should take alternate turns at inserting and removing items. We need to
 * ensure that:
 *
 * 1. The Producer and Consumer are never modifying the CubbyHole at the same
 *    time i.e. we need to provide mutual exclusion over the shared CubbyHole
 *    resource.
 *
 * 2. The Producer never overwrites the contents of the CubbyHole until the
 *    Consumer has retrieved those contents i.e. we need a means of allowing
 *    the Consumer to inform the Producer that it has retrieved the current
 *    contents and there is now room available for the Producer to insert a
 *    new item.
 *
 * 3. The Consumer never takes from the CubbyHole until the Producer has put
 *    some new data in i.e. we need a means of allowing the Producer to inform
 *    the Consumer that it has put new data in the CubbyHole which is now
 *    available for collection.
 *
 * Currently the code does not ensure any of the above. Run it and verify that
 * problems exist.
 */
class CubbyHole {

    private int contents;

    public int get() {

	// Display message
	System.out.println("Consumer (" + Thread.currentThread().getName() +
	    ") got " + contents);

	// Return contents
	return contents;
    }

    public void put(int value) {

	// Update contents
        contents = value;

	// Display message
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
public class BrokenProducerConsumer {

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
