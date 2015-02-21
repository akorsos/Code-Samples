//
//  PlaylistViewController.m
//  MixerOne
//
//  Created by aK on 3/7/14.
//  Copyright (c) 2014 aK. All rights reserved.
//

#import "PlaylistViewController.h"
#import "ViewController.h"

@interface PlaylistViewController ()
@property (strong, nonatomic) AVPlayer *audioPlayAll;
@property (strong, nonatomic) MPMediaItem *currentSong;
@property (strong, nonatomic) MPMediaItem *nextSong;
@property (strong, nonatomic) NSMutableArray *shuffledIndices;
@property (strong, nonatomic) NSNumber *playing;

@end

@implementation PlaylistViewController

/*
 I'm using this method because it takes priority. I needed to run populatePlaylist before the tableView began to create itself. Both viewWillAppear and viewDidLoad are called after the tableView has been formed.
*/
- (id)initWithCoder:(NSCoder *)aDecoder {
    
    self = [super initWithCoder:aDecoder];
    _playlistSongsList = [[NSMutableArray alloc] init];
    [self populatePlaylist];
    _shuffledIndices = [[NSMutableArray alloc] init];
    return self;
}

- (void)viewDidLoad {
    
    [super viewDidLoad];
    
    [_progressPlayAll setThumbImage:[UIImage imageNamed:@"mLine.png"] forState:UIControlStateNormal];
    
    self.tableView.dataSource = self;
    self.tableView.delegate = self;
    
    self.audioPlayAll = [[AVPlayer alloc] init];

    [self.tableView reloadData];
    
    NSLog(@"viewDidLoad\n");
    
    [self configurePlayer];
}

- (void) viewWillAppear:(BOOL)animated {
    
    [self.tableView reloadData];
    NSLog(@"viewWillAppear\n");
}

//Decode and fill the playlist with the songs saved in NSUserDefaults
-(void) populatePlaylist {
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSMutableArray *encodedPlaylistSongsList;
    
    if (![defaults objectForKey:@"playlist"]) {
        encodedPlaylistSongsList = [[NSMutableArray alloc] init];
    } else {
        encodedPlaylistSongsList = [[defaults objectForKey:@"playlist"] mutableCopy];
    }
    
    [_playlistSongsList removeAllObjects];
    
    for(int i=0; i < encodedPlaylistSongsList.count; i++) {
        NSData *encodedSong = encodedPlaylistSongsList[i];
        MPMediaItem *song = [NSKeyedUnarchiver unarchiveObjectWithData:encodedSong];
        [_playlistSongsList addObject:song];
    }
    
    NSLog(@"Loaded songs in playlist from NSUserDefaults: %d\n", [_playlistSongsList count]);
}

//Encodes and write the playlist to NSUserDefaults, this is called in AppDelegate when app enters the background
- (void)savePlaylist {
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSMutableArray *encodedPlaylist = [[NSMutableArray alloc] init];
    
    for(int i=0; i<_playlistSongsList.count; i++) {
        MPMediaItem *song = _playlistSongsList[i];
        NSData *encodedSong = [NSKeyedArchiver archivedDataWithRootObject:song];
        [encodedPlaylist addObject:encodedSong];
    }
    
    NSLog(@"Saved songs in playlist to NSUserDefaults: %d\n", [encodedPlaylist count]);
    
    [defaults setObject:encodedPlaylist forKey:@"playlist"];
    [defaults synchronize];
}

- (void)didReceiveMemoryWarning {
    
    [super didReceiveMemoryWarning];
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    return _playlistSongsList.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *cellIdentifier = @"playlistCell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier];
    
    MPMediaItem *song = [self.playlistSongsList objectAtIndex:indexPath.row];
    NSString *songTitle = [song valueForProperty: MPMediaItemPropertyTitle];
    NSString *durationLabel = [song valueForProperty: MPMediaItemPropertyArtist];
    
    cell.textLabel.text = songTitle;
    cell.detailTextLabel.text = durationLabel;
    
    return cell;
}

//Handles the action for the Play All button and the initial step of the Shuffle feature
- (IBAction)playPausePlayAllAction:(id)sender {
    
    NSLog(@"Tapped Play All");
    [self.tableView setEditing:NO];
    [self.editPlaylist setSelected:NO];
    
    NSLog(@"Songs in playlist: %d", _playlistSongsList.count);
    if(self.playPausePlayAll.selected) {
        [self.audioPlayAll pause];
        [self.playPausePlayAll setSelected:NO];
    } else {
        
        //If the playlist is empty, displays a UIAlertVIew asking user to add songs
        if (_playlistSongsList.count == 0) {
            UIAlertView *error = [[UIAlertView alloc] initWithTitle: @"Playlist is empty!" message: @"Add some songs to the playlist" delegate: self cancelButtonTitle: @"Ok" otherButtonTitles: nil];
            
            [error show];
            return;
        }
        
        [self.audioPlayAll pause];
        _playing = 0;
        
        MPMediaItem *song;
        AVPlayerItem *currentItem;
        
        //If shuffle is turned on, the first song selected is the song at the number index at shuffledIndices[0]
        if (_shuffleSwitch.selected) {
            
            NSNumber *temp = [_shuffledIndices objectAtIndex:_playing.intValue];
            uint shuffledPlaying = [temp integerValue];
            
            NSLog(@"This index: %u, Size of list: %u\n", shuffledPlaying, [_playlistSongsList count]);
            song = _playlistSongsList[shuffledPlaying];
            _currentSong = [_playlistSongsList objectAtIndex:shuffledPlaying];
          
        //...otherwise the first song is whatevers at playlistSongsList[0]
        } else {
            
            song = self.playlistSongsList[_playing.intValue];
            _currentSong = [self.playlistSongsList objectAtIndex:_playing.intValue];
        }
        
        currentItem = [AVPlayerItem playerItemWithURL:[song valueForProperty:MPMediaItemPropertyAssetURL]];
        
        [self.audioPlayAll replaceCurrentItemWithPlayerItem:currentItem];
        [self.audioPlayAll play];
        [self.playPausePlayAll setSelected:YES];
        NSString *songTitle = [_currentSong valueForProperty: MPMediaItemPropertyTitle];
        self.nextTrackLabel.text = songTitle;
            
        NSNumber *duration=[_currentSong valueForProperty:MPMediaItemPropertyPlaybackDuration];
        [self.progressPlayAll setMaximumValue:duration.floatValue];
    }
}

//Handles the action when the user touches the slider ball
- (IBAction)progressPlayAllAction:(id)sender {
    
    [self.audioPlayAll seekToTime:CMTimeMakeWithSeconds((int)(self.progressPlayAll.value) , 1)];
}

//Handles the action when the user taps the Edit button
- (IBAction)editPlaylistAction:(id)sender {
    
    NSLog(@"Tapped Edit");
    
    //Won't allow editing while playlist is playing
    if(_playPausePlayAll.selected){
        UIAlertView *error = [[UIAlertView alloc] initWithTitle: @"Cannot edit while playing" message: @"Stop your playlist if you'd like to edit" delegate: self cancelButtonTitle: @"Ok" otherButtonTitles: nil];
        
        [error show];
        return;
        
    //..otherwise it will!
    } else {
        
        if(self.editPlaylist.selected){
            [self.tableView setEditing:NO];
            [self.editPlaylist setSelected:NO];
       
        } else {
            [self.tableView setEditing:TRUE];
            [self.editPlaylist setSelected:YES];
        }
    }
}

//Toggles the Shuffle feature and creates an array of swapped elements
- (IBAction)shuffleSwitchAction:(id)sender {
    
    //Pauses the track currently playing so that all songs in the playlist can be shuffled
    [_audioPlayAll pause];
    [_playPausePlayAll setSelected:FALSE];
    
    if ([sender isOn]) {
       
        NSLog(@"Shuffle On");
        [self.shuffleSwitch setSelected:TRUE];
        
        //Fills an array with randomly swapped numbers
        NSUInteger numSongs = [_playlistSongsList count];
        [_shuffledIndices removeAllObjects];
        
        for (NSUInteger i = 0; i < numSongs; i++) {
            
            [_shuffledIndices addObject:[NSNumber numberWithUnsignedInteger:i]];
        }
        
        for (uint i = 0; i<numSongs; ++i){
            
            // Select a random element between i and end of array to swap with
            uint nElements = numSongs - i;
            uint n = arc4random_uniform(nElements) + i;
            [_shuffledIndices exchangeObjectAtIndex:i withObjectAtIndex:n];
        }
        
        NSLog(@"Shuffle Order: %@", _shuffledIndices);
        
    } else {
       
        NSLog(@"Shuffle Off");
        [self.shuffleSwitch setSelected:FALSE];
    }
}

//Toggles the ability for a playlist to be looped indefinitely instead of just ending after the final song
- (IBAction)loopSwitchAction:(id)sender {
    if ([sender isOn]) {
        NSLog(@"Loop On");
        [self.loopSwitch setSelected:TRUE];
        
    } else {
        NSLog(@"Loop Off");
        [self.loopSwitch setSelected:FALSE];
    }
}

- (void)tableView:(UITableView *)tableView
                    moveRowAtIndexPath:(NSIndexPath *)sourceIndexPath
                    toIndexPath:(NSIndexPath *)destinationIndexPath {
    
    MPMediaItem *sourceItem = _playlistSongsList[sourceIndexPath.row];
    [_playlistSongsList removeObjectAtIndex:sourceIndexPath.row];
    [_playlistSongsList insertObject:sourceItem atIndex:destinationIndexPath.row];
}

- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath{
    
    return YES;
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath{
    
    return YES;
}

- (void)tableView:(UITableView *)tableView
                    commitEditingStyle:(UITableViewCellEditingStyle)editingStyle
                    forRowAtIndexPath:(NSIndexPath *)indexPath {
    
    //Will not allow deleting while playlist is playing
    if (_playPausePlayAll.selected) {
       
        UIAlertView *error = [[UIAlertView alloc] initWithTitle: @"Cannot edit while playing" message: @"Stop your playlist if you'd like to edit" delegate: self cancelButtonTitle: @"Ok" otherButtonTitles: nil];
        
        [error show];
        return;
        
    } else {
    
        // If row is deleted, remove it from the list.
        if (editingStyle == UITableViewCellEditingStyleDelete) {
            [_playlistSongsList removeObjectAtIndex:indexPath.row];
            // Animate the deletion from the table.
            [self.tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath]
                                  withRowAnimation:UITableViewRowAnimationFade
             ];
        }
    }
}

//A block that runs the second by second update of the timer and progress slider
//Also houses the actual operation of the Loop and Shuffle features
-(void) configurePlayer {
    __block PlaylistViewController * weakSelf = self;
    [self.audioPlayAll addPeriodicTimeObserverForInterval:CMTimeMakeWithSeconds(1, 1)
                       queue:NULL
             usingBlock:^(CMTime time) {
                 if(!time.value) {
                     return;
                 }

                 int currentTime = (int)((weakSelf.audioPlayAll.currentTime.value)/weakSelf.audioPlayAll.currentTime.timescale);
                 int currentMins = (int)(currentTime/60);
                 int currentSec  = (int)(currentTime%60);
                 
                 NSString * durationLabel =
                 [NSString stringWithFormat:@"%02d:%02d",currentMins,currentSec];
                 weakSelf.durationPlayAll.text = durationLabel;
                 weakSelf.progressPlayAll.value = currentTime;
                 
                 NSNumber *duration=[weakSelf.currentSong valueForProperty:MPMediaItemPropertyPlaybackDuration];

                 if (currentTime == duration.intValue) {
                     
                     //"playing" denotes what element of shuffledIndicies or playlistSongsList the player is on
                     //This line increments playing so it will move to the song corresponding to the next element
                     weakSelf.playing = [NSNumber numberWithInt:[weakSelf.playing intValue] + 1];
                     
                     //This is how the Loop feature works, it changes playing back to 0 so that the playlist will go back to the first song
                     if(weakSelf.playing.intValue == weakSelf.playlistSongsList.count){
                         [weakSelf.audioPlayAll pause];
                         if (weakSelf.loopSwitch.selected) {
                             weakSelf.playing = 0;
                         } else {
                             [weakSelf.audioPlayAll replaceCurrentItemWithPlayerItem:NULL];
                             [weakSelf.playPausePlayAll setSelected:FALSE];
                             return;
                         }
                     }
                     
                     NSLog(@"End of track");
                     
                     MPMediaItem *song;
                     
                     //This is where the shuffle feature operates
                     if(weakSelf.shuffleSwitch.selected) {
                         
                         //Shuffle just changes the song variable to the next element in shuffledIndices
                         NSNumber *temp = [weakSelf.shuffledIndices objectAtIndex:weakSelf.playing.intValue];
                         uint shuffledPlaying = [temp integerValue];
                         
                         NSLog(@"This index: %u, Size of list: %u\n", shuffledPlaying, [weakSelf.playlistSongsList count]);
                         song = weakSelf.playlistSongsList[shuffledPlaying];
                         weakSelf.currentSong = [weakSelf.playlistSongsList objectAtIndex:shuffledPlaying];

                     } else {
                         
                         //Plays in order
                         song = weakSelf.playlistSongsList[weakSelf.playing.intValue];
                         weakSelf.currentSong = [weakSelf.playlistSongsList objectAtIndex:weakSelf.playing.intValue];
                     }
                     
                     AVPlayerItem *currentItem = [AVPlayerItem playerItemWithURL:[song valueForProperty:MPMediaItemPropertyAssetURL]];
                     
                     [weakSelf.audioPlayAll replaceCurrentItemWithPlayerItem:currentItem];
                     [weakSelf.audioPlayAll play];
                     [weakSelf.playPausePlayAll setSelected:YES];
                     
                     NSString *songTitle = [weakSelf.currentSong valueForProperty: MPMediaItemPropertyTitle];
                     weakSelf.nextTrackLabel.text = songTitle;
                     
                     NSNumber *duration=[weakSelf.currentSong valueForProperty:MPMediaItemPropertyPlaybackDuration];
                     [weakSelf.progressPlayAll setMaximumValue:duration.floatValue];
                 }
             }];
}

@end
