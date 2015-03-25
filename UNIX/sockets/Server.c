#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>

//Substring struct, used to allocate memory for each word in the dictionary
void substr(char* word){
    int length = strlen(word);
    char* newWord = (char*)malloc(length  * sizeof(char));
    
    int i;
    for(i=0; i<length; i++){
        newWord[i] = word[i];
    }
    
    newWord[length] = '\0';
    bzero(word, sizeof(word));
    strcpy(word, newWord);
}

int main(int argc, char* argv[]){
    struct sockaddr_in serverAddress;
    struct sockaddr_in clientAddress;
    
    int sock, connection, port;
    
    char wordSearched[BUFSIZ];
    char word[BUFSIZ];
    char reply[BUFSIZ];
    char* replyList[10];
    char dictionary[100000][30];
    
    FILE* dict;
    
    //Catches improper use
    if(argc != 2){
        printf("Usage: %s port\n", argv[0]);
        return 1;
    }
    
    //Allocate enough memory to hold an array of ten words
    int i;
    for(i = 0; i < 10; i++){
        replyList[i] = (char*)malloc(10 * sizeof(char));
    }

    //Allocate memory using bzero
    bzero((char*)&serverAddress, sizeof(serverAddress));
    bzero((char*)&clientAddress, sizeof(clientAddress));
    bzero(wordSearched, BUFSIZ);
    bzero(reply, BUFSIZ);
    bzero(word, BUFSIZ);

    //Create socket
    port = atoi(argv[1]);
    sock = socket(AF_INET, SOCK_STREAM, 0);

    //Set server addresses
    serverAddress.sin_family = AF_INET;
    serverAddress.sin_addr.s_addr = htonl(INADDR_ANY);
    serverAddress.sin_port = htons(port);
    
    //Bind socket to port and catch improper use
    if(bind(sock, (struct sockaddr *) & serverAddress, sizeof(serverAddress))){
        printf("Error binding socket to port\n");
        return 1;
    }

    printf("Waiting for Client connection...\n");
    
    //Begin listening for incoming connections
    if(listen(sock,5)){
        printf("Error listening for connection\n");
    }

    int length;
    length = sizeof(clientAddress);
    
    //Accept an incoming connection and catch improper use
    if((connection = accept(sock, (struct sockaddr*) &clientAddress, (socklen_t *) &length)) < 0){
        printf("Error accepting connection\n");
    }
    printf("Connection Accepted\n");
    
    //Opens dictionary file
    if((dict = fopen("words.txt", "r")) < 0){
        printf("Error opening dictionary file\n");
    }
    printf("Dictionary Opened\n");

    //Read from dictionary and store individual words in an array
    int dictionaryCount = 0;
    while(fscanf(dict, "%s", dictionary[dictionaryCount]) != EOF){
        dictionaryCount++;
    }
    printf("Dictionary Read\n");
    
    for(i=0; i<dictionaryCount; i++){
        substr(dictionary[i]);
    }
    
    //Read from the connection
    int n, count, find;
    while((n = recv(connection, wordSearched, sizeof(wordSearched), 0)) > 0){
        printf("\nWord searched: %s\n", wordSearched);
        find = 1;
        count = 0;
        
        //Iterate over all words in dictionary
        int k;
        for(k = 0; k < dictionaryCount; k++){
            
            //Catches if word is spelled correctly
            if(strcmp(dictionary[k], wordSearched) == 0){
                find = 0;
                printf("Found %s in dictionary\n", dictionary[k]);
                break;
            }
            
            int j, similar;
            j = 0;
            i = 0;
            similar = 0;
            
            //Iterates over characters in word searched looking for matching consonats and skipping over vowels, got the idea from Nick during his office hours
            while(i < strlen(wordSearched)){
                if(wordSearched[i] == dictionary[k][j]){
                    j++;
                    i++;
                
                } else {
                
                    //Skipping over vowels in the searched word
                    if(wordSearched[i] == 'a' || wordSearched[i] == 'o' || wordSearched[i] == 'e' || wordSearched[i] == 'i' || wordSearched[i] == 'u'){
                    i++;
                    
                    } else {
                    
                        //Skipping over vowels in the dictionary word
                        if(dictionary[k][j] == 'a' || dictionary[k][j] == 'o' || dictionary[k][j] == 'e' || dictionary[k][j] == 'i' || dictionary[k][j] == 'u'){
                            j++;
                        
                        } else{
                    
                            //Found a matching consonant
                            similar = 1;
                            break;
                        }
                    }
                }
            }
            
            if(j < strlen(dictionary[k])){
                similar = 1;
            }
            
            //Gathers a list of ten similar words
            if(similar == 0 && count < 10){
                strcpy(replyList[count], dictionary[k]);
                count++;
            }
        }
        
        //Case where word is spelled incorrectly and ten words have been gathered
        if(find != 0){
            bzero(reply, BUFSIZ);
            reply[0] = '\0';
            
            for(i = 0; i < count; i++){
                printf("Suggestion: %s\n", replyList[i]);
                strcat(reply, replyList[i]);
                
                //Prevents trailing comma
                if (i != count-1) {
                    strcat(reply, ", ");
                }
            }
            
            //Print suggested words
            printf("Reply: %s\n", reply);
            
            //Write response to socket
            write(connection, reply, strlen(reply));
        
        } else {
            
            //Case where word is spelled correctly
            bzero(reply, BUFSIZ);
            sprintf(reply, "%d", find);
            write(connection, reply, strlen(reply));
        }
        
        //Clear memory
        bzero(wordSearched, BUFSIZ);
        bzero(reply, BUFSIZ);
    }
    
    printf("Closing connection...\n");
    
    close(connection);
    close(sock);
    fclose(dict);
    
    return 0;
}
