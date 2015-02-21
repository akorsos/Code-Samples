#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <fcntl.h>
#include <sys/stat.h>

int main(int argc, char * argv[]){
    char input[1024];
    char* mypipe = "mypipe";
    int pipe;
    int total;
    
    //Create new pipe or use is already exists
    mkfifo(mypipe, 0777);

    printf("Creating named pipe: %s\n", mypipe);
    
    //Open pipe
    pipe = open(mypipe, O_RDONLY);
    
    //While whats coming in is non zero
    while((total = read(pipe, input, sizeof(input))) > 0){
        printf("Waiting for input...Got it: %s", input);
        
        //Catch 'exit'
        if(strcmp(input, "exit\n") == 0){
            printf("Exiting\n");
            close(pipe);
            exit(0);
        }
        
        //Clear input
        memset(input, '\0', 1024);
    }
    
    return 0;
}