#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <signal.h>

#define CHILD (pid_t) 0

int tokenizeArgs(char* argCount, char* args[]);

int main(int argc, char* argv[] ){
    
    pid_t child;
    int fdes[2];
    
    //Parse arguments
    char* args2[1024];
    tokenizeArgs(argv[2], args2);
    char* args1[1024];
    tokenizeArgs(argv[1], args1);
    
    //Sets up a pipe
    pipe(fdes);
    
    //Program forks
    if((child = fork()) == CHILD){

        //Close and flush stdin
        close(fdes[0]);
        fflush(stdin);

        //Duplicates stdout to the pipes writer
        dup2(fdes[1], STDOUT_FILENO);
        
        //Run commands
        execvp(args1[0], args1);
    }
    else{
        
        //Close and flush stdout
        close(fdes[1]);
        fflush(stdout);

        //Duplicates stdin to the pipes reader
        dup2(fdes[0], STDIN_FILENO);
        
        //Run commands
        execvp(args2[0], args2);
    }
    
    return 0;
}

int tokenizeArgs(char* argCount, char* args[]){
    int totalArgs = 0;
    int length = strlen(argCount);

    int j = 0;
    int i = 0;
    
    while(i < length){
        args[totalArgs] = (char*)malloc(1024 * sizeof(char));
        
        while(argCount[i] != ' ' && i < length){
            args[totalArgs][j] = argCount[i];
            j++;
            i++;
        }
        
        args[totalArgs][j] = '\0';
        
        if(argCount[i] == ' '){
            totalArgs++;
            j = 0;
        }
        
        i++;
    }
    
    return 0;
}