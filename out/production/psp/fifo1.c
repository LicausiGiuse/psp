
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

int main() {

    if (mkfifo("FIF02", 0666) == -1) {
        printf("Error al crear fifo\n");
    }

    int fd = open("FIF02", O_RDONLY);

    char mensaje[10];

    while (1) {

        read(fd, mensaje, sizeof(mensaje));

        if (mensaje[0] == 'C') {
            printf("Turno de carniceria\n");
        }
        else if (mensaje[0] == 'P') {
            printf("Turno de pescaderia\n");
        }
    }

    close(fd);

    return 0;
}
