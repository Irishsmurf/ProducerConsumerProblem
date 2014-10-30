#include <sys/types.h>
#include <unistd.h>
#include <stdio.h>

int
main(void)
{
	  /* Declare a variable to hold the value returned by getpid */
	int pid;
	int ppid;
	/* Make our system call and store the result */
	pid = getpid();
	ppid = getppid();
	  /* Display the result */
	printf("pid: %d\n", pid);
	printf("ppid: %d\n", ppid);
	return (0);
}
