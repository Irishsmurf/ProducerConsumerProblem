// The bag that holds the sweets
class Bag {

    // Variables
    public int sweets, in, out;

    // Constructor
    public Bag() {

	sweets = in = out = 0;

    }
}

// The parent thread
class Parent extends Thread {

    // Our thread works on this
    public Bag bag;

    // Constructor
    public Parent(Bag bag) {

	this.bag = bag;

    }

    // What our thread does
    public void run() {

	while (true) {

	    bag.sweets++;
	    bag.in++;

	}
    }
}

// The child thread
class Child extends Thread {

    // Our thread works on this
    public Bag bag;

    // Constructor
    public Child(Bag bag) {

	this.bag = bag;

    }

    // What our thread does
    public void run() {

	while (true) {

	    bag.sweets--;
	    bag.out++;

	}
    }
}

// Main
class Sweets {

    public static void main(String[] args) throws InterruptedException {

	// Create our bag
	Bag bag = new Bag();

	// Create our child thread
	Child cthread = new Child(bag);

	// Create our parent thread
	Parent pthread = new Parent(bag);

	// Start threads
	cthread.start();
	pthread.start();

	// Keep track
	while (true) {

	    // Sleep for two seconds
	    Thread.sleep(2000);

	    // Display the current state of affairs
	    System.out.println("Delta = " + (bag.in - bag.out - bag.sweets)
	        + " Sweets = " + bag.sweets);
	}

    }

}
