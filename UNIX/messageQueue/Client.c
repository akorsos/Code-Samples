#include <stdio.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/msg.h>

struct message{
    long int msg_type;
    char content[256];
};

int main( int argc, char* argv[] ){
    int msgid, repid;
    struct message msg;
    struct message reply;
    int retval;
    
    // create message queue
    msgid = msgget( (key_t)73707, 0666 | IPC_CREAT );
    repid = msgget( (key_t)33445, 0666 | IPC_CREAT );
    if( msgid == -1 ){
        fprintf( stderr, "msgget from client failed\n" );
        exit( EXIT_FAILURE);
    }
    if( repid == -1 ){
        fprintf( stderr, "msget from server failed\n" );
    }
    while( 1 ){
        printf( "Insert message to send to server:" );
        fgets( msg.content, 256, stdin );
        /*
         if( strcmp( msg.content, "exit") == 0 || strcmp( msg.content, "EXIT") == 0 ){
         exit(0);}
         */
        msg.msg_type = getpid();
        // send message to message queue
        if( msgsnd( msgid, (void*)&msg, sizeof( struct message ), 0) == -1 ){
            fprintf( stderr, "msgsnd failed\n" );
            exit(EXIT_FAILURE);
        }
        // get message from message queue
        for( ; ; ){
            if ((retval = msgrcv( repid, (void*)&reply, sizeof( struct message ), getpid(), 0 )) != -1){
                printf("Message processed: %s\n", reply.content );
                break;
            }
        }
    }
}