#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>

int main() {
int valor =10;
printf("Valor inicial de la variable es:%d\n",valor);

pid_t proceso = fork();

if (proceso == 0){

valor = valor - 5;
printf("Variable en proceso hijo: %d\n",valor);

}else{
valor = valor + 5;
printf("Variable en proceso padre: %d\n",valor);
}
return 0;


}
