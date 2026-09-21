#include <stdio.h>
#include <stdlib.h>

void main()
{
printf("Ejemplo de uso de System():");
printf("\n\tlistado del directorio actual y envio a un fichero:");
printf("%d",system("ls > fiscsalida"));
printf("\n\tAbrimos con el gedit el fichero...");
  printf("%d",system("gedit ficsalida"));
  printf("\n\tEste comando es erróneo: %d",system("ged"));
  printf("\nFin de programa....\n");
}
