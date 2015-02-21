#include <sys/types.h>
#include <sys/ipc.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <sys/msg.h>

struct message{
    long int msg_type;
    char content[256];
};

/* convert upper case to lower case or vise versa */
void conv(char *msg, int size)
{
    int i;
    for (i=0; i<size; ++i)
        if (islower(msg[i]))
            msg[i] =  toupper(msg[i]);
        else if (isupper(msg[i]))
            msg[i] =  tolower(msg[i]);
}

int main( int argc, char* argv[] ){
    struct message msg;
    struct message reply;
    int retval;
    int msgid;
    int repid;
    //create two message queue, one from client to server
    //and the other from server to client
    msgid = msgget( (key_t)73707, 0666 | IPC_CREAT );
    repid = msgget( (key_t)33445, 0666 | IPC_CREAT );
    if( msgid == -1 ){
        fprintf( stderr, "client msgget failed\n" );
        exit(EXIT_FAILURE);
    }
    if( repid == -1 ){
        fprintf( stderr, "server msgget failed\n" );
        exit(EXIT_FAILURE);
    }
    //get message from user to server
    for( ; ; ){
        retval = msgrcv( msgid, (void*)&msg, sizeof( struct message ), 0, 0 );
        if( retval != -1 ){
            strcpy( reply.content, msg.content );
            conv( reply.content, 256 );
            reply.msg_type = msg.msg_type;
            // send message from server to client
            if( msgsnd( repid, (void*)&reply, sizeof( struct message ), 0) == -1){
                fprintf( stderr, "reply failed\n" );
                //exit(EXIT_FAILURE);
            }
        }
    }
}