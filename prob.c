#include <semaphore.h>
#include <stdio.h>
#include <pthread.h>
#include <unistd.h>

/* Initialise our global variables, the threads share these */
static int count = 0;
static int out = 0;
static int in = 0;
static int guard = 1;
static sem_t semaphore;
sem_init(&semaphore);
/*
 * The producer increments both count and in
  */
  static void * producer(void *arg)
  {
    while (1)
	{
		sem_wait(&semaphore);
		count++;
		in++;
		sem_post(&semaphore);
	}
	return ((void *)NULL);
	}

	/*The consumer decrements count and increments out*/
	static void * consumer(void *arg)
	{
		while (1) 
		{
			sem_wait(&semaphore);
			count--;
			out++;
			guard = 1;
			sem_post(&semaphore);
		}
		return ((void *)NULL);
	}
	
	int	main(void)
	{
		pthread_t p, c;
		/* Create threads */
		pthread_create(&p, NULL, producer, NULL);
		pthread_create(&c, NULL, consumer, NULL);
				  /*
				     * The producer puts elements into count and the consumer takes
					    * elements out. Therefore the number put in minus the number
						   * taken out should give us the current value of count i.e.
						      * in - out = count or
							     * in - out - count = 0
								    */
		while (1) 
		{
			printf("Delta: %d\n", in - out - count);
			sleep(5);
		}
		/* Wait for them */
		pthread_join(p, NULL);
		pthread_join(c, NULL);
		sem_destroy(&semaphore);
		return (0);
	}
