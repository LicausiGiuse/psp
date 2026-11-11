#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <string.h>

int main() {

    int pipefd[2];

    pipe(pipefd);

    pid_t proceso;

    proceso = fork();

    if (proceso == 0) {
        // HIJO
        close(pipefd[1]);

        char buffer[100];

        read(pipefd[0], buffer, sizeof(buffer));

        printf("El HIJO recibe algo del pipe: %s\n", buffer);

        close(pipefd[0]);

    } else {
        // PADRE
        close(pipefd[0]);

        char mensaje[] = "Buenos días hijo.";

        printf("El PADRE envía algo al pipe.\n");

        write(pipefd[1], mensaje, strlen(mensaje) + 1);

        close(pipefd[1]);
    }

    return 0;
}
