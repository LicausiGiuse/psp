#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>

int main(){

int fd = open("FIF02", O_WRONLY);
char mensaje[] = "P\n";
write(fd,mensaje,sizeof(mensaje));

close(fd);
return 0;
}
