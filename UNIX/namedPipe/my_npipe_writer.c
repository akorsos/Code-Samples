#include <string.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>

int main(){
    char input[1024];
    char* mypipe = "mypipe";
    int pipe;
    
    //Use pipe
    mkfifo(mypipe, 0777);
    
    printf("Opening named pipe: %s\n", mypipe);
    
    //Open pipe
    pipe = open(mypipe, O_WRONLY);
    
    printf( "Enter Input: ");
    
    //While input is non null
    while(fgets(input, 1024, stdin) != NULL){
        printf("Writing buffer to pipe...done\n");
        
        //Write input to the pipe
        write(pipe, input, strlen(input));
        
        //Catches exit
        if (strcmp(input, "exit\n") != 0) {
            printf("Enter Input: " );
            
        } else {
            
            printf("Exiting\n");
            close(pipe);
            exit(0);
        }
        
        //Clear input
        memset(input, '\0', 1024);
    }
    
    return 0;
}