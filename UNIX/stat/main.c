#include <stdio.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <time.h>
#include <string.h>
#include <pwd.h>
#include <grp.h>

int stat(const char *pathname, struct stat *statbuf);
int lstat(const char*pathname, struct stat *statbuf);
int fstat(int fd, struct stat *statbuf);

int main(int argc, char **argv){
    
    //Create a stat struct
    struct stat sb;
    
    size_t optind;
    char *format = NULL;
    
    //Gets flag value and sets it to argv[optind]
    for (optind = 1; optind < argc && argv[optind][0] == '-'; optind++) {
        switch (argv[optind][1]) {
            case 'c': optind++; format = argv[optind]; break;
            default:
                fprintf(stderr, "Usage: %s -c --format pathname\n", argv[0]);
                return 1;
        }
    }
    
    if(format != NULL){
        //Assigns the stat struct to the appropriate argument
        int sbRet = stat(argv[3], &sb);
        
        //Returns requested value
        //Device
        if (strcmp(format, "--d") == 0) {
            printf("Device: %ldh/%ldd\n", (long) major(sb.st_dev), (long) minor(sb.st_dev));
            return 0;
        }
        //Size
        if (strcmp(format, "--s") == 0) {
            printf("Size: %lld\n", (long long) sb.st_size);
            return 0;
        }
        //Blocks
        if (strcmp(format, "--b") == 0) {
            printf("Blocks: %lld\n",(long long) sb.st_blocks);
            return 0;
        }
        //Links
        if (strcmp(format, "--l") == 0) {
            printf("Links: %ld\n", (long) sb.st_nlink);
            return 0;
        }
        //Inode
        if (strcmp(format, "--i") == 0) {
            printf("Inode: %ld\n", (long) sb.st_ino);
            return 0;
        } else {
            printf("Valid commands: Device --d, Size --s, Blocks --b, Links --l, Inode --i\n");
            return 0;
        }
        
    } else {
        
        //Catches improper format
        if (argc != 2) {
            fprintf(stderr, "Usage: %s <pathname>\n", argv[0]);
            return 1;
        }
        
        //Catches if file does not exist
        if (stat(argv[1], &sb) == -1) {
            perror("stat");
            return 1;
        }
        
        //Prints file name/address
        printf("File: '%s'\n", argv[1]);
        
        //Prints size, blocks allocated, and blocks size on one line
        printf("Size: %lld          ", (long long) sb.st_size);
        printf("Blocks: %lld          ",(long long) sb.st_blocks);
        printf("IO Block: %ld          ", (long) sb.st_blksize);
        
        char type;
        
        //Defines and prints file type
        switch (sb.st_mode & S_IFMT) {
            case S_IFBLK:  type = 'b'; printf("Block Device\n");        break;
            case S_IFCHR:  type = 'c'; printf("Character Device\n");    break;
            case S_IFDIR:  type = 'd'; printf("Directory\n");           break;
            case S_IFIFO:  type = 'p'; printf("FIFO/Pipe\n");           break;
            case S_IFLNK:  type = 'l'; printf("Symlink\n");             break;
            case S_IFREG:  type = '-'; printf("Regular File\n");        break;
            case S_IFSOCK: type = 's'; printf("Socket\n");              break;
            default:       type = '?'; printf("Unknown Type\n");        break;
        }
        
        //Prints device, inode, and links on one line
        printf("Device: %ldh/%ldd    ", (long) major(sb.st_dev), (long) minor(sb.st_dev));
        printf("Inode: %ld    ", (long) sb.st_ino);
        printf("Links: %ld\n", (long) sb.st_nlink);
        
        //Prints access rights and ownership to on one line
        printf("Access: (%lo/", (unsigned long) sb.st_mode);
        
        //Creates a passwd struct to retrieve username from
        struct passwd *pwd;
        pwd = getpwuid(sb.st_uid);
        
        //Creates a group struct to retrieve groupname from
        struct group *grp;
        grp = getgrgid(sb.st_gid);
        
        //Prints permissions
        printf("%c", type);
        printf( (sb.st_mode & S_IRUSR) ? "r" : "-");
        printf( (sb.st_mode & S_IWUSR) ? "w" : "-");
        printf( (sb.st_mode & S_IXUSR) ? "x" : "-");
        printf( (sb.st_mode & S_IRGRP) ? "r" : "-");
        printf( (sb.st_mode & S_IWGRP) ? "w" : "-");
        printf( (sb.st_mode & S_IXGRP) ? "x" : "-");
        printf( (sb.st_mode & S_IROTH) ? "r" : "-");
        printf( (sb.st_mode & S_IWOTH) ? "w" : "-");
        printf( (sb.st_mode & S_IXOTH) ? "x" : "-");
        
        //Prints uid/name and gid/name
        printf(")  Uid: (%ld/ %s)  Gid: (%ld/ %s)\n",(long) sb.st_uid, pwd->pw_name,(long) sb.st_gid, grp->gr_name);
        
        //Prints access, modify, and change dates on one line
        printf("Access: %s", ctime(&sb.st_atime));
        printf("Modify: %s", ctime(&sb.st_mtime));
        printf("Change: %s", ctime(&sb.st_ctime));
    }
    
    return 0;
}
