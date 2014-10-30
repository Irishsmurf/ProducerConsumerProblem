/*e an anonymous pipe. We create a pipe and fork a child process. The
* child sends messages back to the parent which the parent prints out.
*/
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#define	BSIZE	1024
#define	MSGS	10

int
main(void)
{
	int	pd[2], i;
	char	buffer[BSIZE];
	FILE    *w, *r;

	/* Create pipe */
	if (pipe(pd) < 0) {
		perror("pipe()");
		exit(EXIT_FAILURE);
	}

	/* Fork */
	switch (fork()) {

		/* The child */
		case 0:

		/*
		* In child so close read side and send some
		* messages back to the parent
		*/
		(void) close(pd[0]);
		w = fdopen(pd[1], "w");

		/* Send 10 messages back to parent */
		for (i = 0; i < MSGS; i++) {

			/* Create message */
			(void) snprintf(buffer, BSIZE,
			"Hi mum, this is message %d from your child\n", i);

			/* Send message */
			fprintf(w, "%s", buffer);
		}

		/* Finished so close the write side */
		fclose(w);

		/* Exit */
		exit(EXIT_SUCCESS);
	}

	/* In the parent so close the write side */
	(void) close(pd[1]);
	r = fdopen(pd[0], "r");

	/* Read 10 messages from child */
	for (i = 0; i < MSGS; i++) {

		/* Read the message */
		fgets(buffer, sizeof (buffer), r);

		/* Display the message */
		(void) printf("%s", buffer);

	}

	/* Close read side */
	fclose(r);

	/* Wait for child process to exit */
	(void) wait(NULL);

	return (0);
}
