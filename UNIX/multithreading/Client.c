#include <stdio.h>
#include <sys/socket.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>
#include <arpa/inet.h>

int main(int argc, char* argv[]){
    struct sockaddr_in serverAddress;
    struct hostent* host;
    struct in_addr inaddr;
    
    char originalWord[BUFSIZ];
    char recommendedWords[BUFSIZ];
    char commandlineWord[BUFSIZ];
    
    int port, sock;
    
    //Catches improper use
    if(argc != 3){
        printf("Usage: %s host port\n",argv[0]);
        return 1;
    }
    
    //The second argument is taken as the port
    port = atoi(argv[2]);
    
    //Allocate memory using bzero
    bzero((char*)&serverAddress, sizeof(serverAddress));
    bzero(originalWord, BUFSIZ);
    bzero(commandlineWord, BUFSIZ);
    bzero(recommendedWords, BUFSIZ);
    
    if(inet_aton(argv[1], &inaddr)){
        host = gethostbyaddr((char *) &inaddr, sizeof(inaddr), AF_INET);
    } else {
        host = gethostbyname(argv[1]);
    }
    
    //Catches if host is invalid
    if (!host){
        printf("Host Error");
    }
    
    //Create a socket and catches improper use
    if((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0){
        printf("Socket Error");
    }
    
    //Set server addresses
    bcopy((char*)host->h_addr, (char*)&serverAddress.sin_addr.s_addr, host->h_length);
    serverAddress.sin_family = AF_INET;
    serverAddress.sin_port = htons(port);
    
    //Create a connection
    if (connect(sock, (struct sockaddr *) &serverAddress, sizeof(serverAddress)) < 0){
        printf("Cannot connect to server\n");
        return 1;
    }
    
    //Run infinitely
    while(1){
        printf("\nEnter a word: ");
        
        //take in word at commandline, copy and trim it
        fgets(commandlineWord, 255, stdin);
        strncpy(originalWord, commandlineWord, strlen(commandlineWord) - 1);
        
        int n;
        //Write to Server
        if((n = write(sock , originalWord ,strlen(originalWord))) < 0){
            printf("Write Error");
        }
        
        //Read from Server
        if((n = read(sock, recommendedWords, BUFSIZ)) < 0){
            printf("Read Error");
        }
        
        //Prints list of recommended words or declares word is correct
        if(strcmp(recommendedWords, "0") != 0){
            printf("The word %s is spelled incorrectly\n", originalWord);
            printf("Suggestions: %s\n", recommendedWords);
        } else {
            if(strcmp(recommendedWords, "0") == 0){
                printf("The word %s is spelled correctly\n", originalWord);
            }
        }
        
        //Clear memory
        bzero(recommendedWords, BUFSIZ);
        bzero(originalWord, BUFSIZ);
        bzero(commandlineWord, BUFSIZ);
    }
    
    close(sock);
    
    return 0;
}