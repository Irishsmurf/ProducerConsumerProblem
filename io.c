#include <stdio.h>

main()
{
	long count = 0;

	while(getchar() != EOF)
	{
		++count;
	}
	printf("%ld\n", count);
}
