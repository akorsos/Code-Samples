#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>

int main(int argc, char *argv[]){
    int shmid;
    int *shm;
    int flag = SHM_R | SHM_W;
    int count = 5;
    key_t key = IPC_PRIVATE;

    
    if(argc >= 2){
        key = atoi(argv[1]);
    }
    
    shmid = shmget(key, 1000, flag);
    
    // printf("shared memory for key %d: %d\n", key, shmid);
    
    if(shmid < 0){
        perror("shmget");
        printf("Try to create this segment\n");
        
        shmid = shmget(key, 1000, flag | IPC_CREAT);
        
        if(shmid < 0){
            perror("shmget | IPC_CREAT");
        }
    }
    
    shm = (int*)shmat(shmid, NULL, 0);
    
    if(shm == (int*) -1){
        perror("shmat");
        return 1;
    }
    
    *shm = count;
    
    printf("There are %d items on the shelf.\n", *shm);
    
    while(1){
        
        if( (*shm) < 5 ){
            (*shm)++;
            
            printf( "Add one item on the shelf, there are %d items on the shelf now.\n", *shm );
        }
        
        sleep(5);
    }
    
    return 0;
}