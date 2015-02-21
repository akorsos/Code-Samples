#include <stdio.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/types.h>

int main(int argc, char *argv[]){
    
    int inputFd, outputFd, openFlags;
    mode_t filePerms;
    ssize_t numRead;
    char buf[1024];
    
    //Create a stat struct
    struct stat sb;
    struct stat sbNew;
    
    //Catches improper format
    if (argc != 3) {
        fprintf(stderr, "Usage: %s <filename> <newfilename>\n", argv[0]);
        return 1;
    }
    
    int sbRet = stat(argv[1], &sb);
    int sbNewRet = stat(argv[2], &sbNew);
    
    //Catches if input file does not exist
    if (sbRet == -1) {
        perror("filecopy");
        fprintf(stderr, "Usage: %s <filename> <newfilename>\n", argv[0]);
        return 1;
    }
    
    //Prevents file input from being copied over input by comparing inodes
    if (sb.st_ino == sbNew.st_ino) {
        printf("Cannot copy input file over itself\n");
        return 1;
    }

    //printf("Inode 1: %ld\n", (long) sb.st_ino);
    //printf("Inode 2: %ld\n", (long) sbNew.st_ino);
    
    //Open input file
    inputFd = open(argv[1], O_RDONLY);
    
    //Error opening input file
    if (inputFd == -1){
        printf("Error opening input file %s", argv[1]);
    }
    
    //Set flags and permissions for new file
    openFlags = O_CREAT | O_WRONLY | O_TRUNC;
    filePerms = S_IRUSR | S_IWUSR | S_IRGRP | S_IWGRP | S_IROTH | S_IWOTH;
    outputFd = open(argv[2], openFlags, filePerms);
   
    //Error opening output file
    if (outputFd == -1){
        printf("Error opening output file %s", argv[2]);
    }
    
    //Copy data until end or error
    while ((numRead = read(inputFd, buf, 1024)) > 0){
        if (write(outputFd, buf, numRead) != numRead){
            printf("File too large to copy");
            return 1;
        }
    }
    
    //Errors with closing input or output
    if (close(inputFd) == -1){
        printf("Error closing input");
        return 1;
    }
    if (close(outputFd) == -1){
        printf("Error closing output");
        return 1;
    }
    
    return 0;
}