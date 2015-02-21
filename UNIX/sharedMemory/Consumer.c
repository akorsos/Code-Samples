#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

int main(int argc, char *argv[]){
    int shmid;
    int *shm;
    key_t key;
    
    if(argc < 2){
        printf("Usage: %s key\n", argv[0]);
        return 0;
    }
    
    key = atoi(argv[1]);
    
    printf("Trying shared memory %d\n", key);
    
    shmid = shmget(key, 1000, SHM_R | SHM_W);
    
    if (shmid < 0){
        perror("shmget");
        shmid = key;
    }
    
    /*
     else {
     printf("shared memory: %d\n", shmid);
     }
     */
    
    shm = (int*)shmat(shmid, 0, 0);
    
    if (shm == (int*) -1) {
        perror("shmat");
        exit(1);
    }
    
    // printf("shared memory: %p\n", shm);
    
    if (shm != 0) {
        printf("Before shopping, there are %d items on the shelf\n", *shm);
        
        if(*shm == 0)
            printf( "Blasted!! I knew I should have gone to Target.\n" );
        
    } else{
        
            (*shm) --;
            printf( "There are %d item on the shelf\n", *shm );
    }
    

    sleep(1);
    return 0;
}