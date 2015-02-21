//
//  ViewController.m
//  MixerOne
//
//  Created by aK on 3/4/14.
//  Copyright (c) 2014 aK. All rights reserved.
//

#import "ViewController.h"
#import "PlaylistViewController.h"

@interface ViewController ()
@property (strong, nonatomic) NSMutableArray *songsList;
@property (strong, nonatomic) AVPlayer *audioDeckA;
@property (strong, nonatomic) AVPlayer *audioDeckB;
@property (strong, nonatomic) NSIndexPath *path;
@property (strong, nonatomic) MPMediaItem *currentSongDeckA;
@property (strong, nonatomic) MPMediaItem *currentSongDeckB;
@property (strong, nonatomic) NSString *initialLaunch;
@property(nonatomic, retain) UIColor *maximumTrackTintColor;

@end

@implementation ViewController

- (void)viewDidLoad
{
    [super viewDidLoad];

    //This will run on the first launch to record the initial launch date to NSUserDefaults
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    if (![defaults stringForKey:@"firstLaunch"]) {
    
        NSDate *today = [NSDate date];
        NSDateFormatter *dateFormat = [[NSDateFormatter alloc] init];
        [dateFormat setDateFormat:@"MM/dd/yyyy"];
        _initialLaunch = [dateFormat stringFromDate:today];
        
        [defaults setObject:_initialLaunch forKey:@"firstLaunch"];
        [defaults synchronize];

    } else {
        
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        _initialLaunch = [[defaults stringForKey:@"firstLaunch"] copy];
    }
    
    NSLog(@"First launch was on %@", _initialLaunch);
    
    [_crossfader setThumbImage:[UIImage imageNamed:@"line1.png"] forState:UIControlStateNormal];
    [_progressDeckA setThumbImage:[UIImage imageNamed:@"rLine.png"] forState:UIControlStateNormal];
    [_progressDeckB setThumbImage:[UIImage imageNamed:@"bLine.png"] forState:UIControlStateNormal];

    _maximumTrackTintColor = [UIColor colorWithRed:(255.0/255.0) green:(0.0/255.0) blue:(0.0/255.0) alpha:1] ;
    [_crossfader setMaximumTrackTintColor:_maximumTrackTintColor];
    
    self.tableView.dataSource =self;
    self.tableView.delegate = self;

    //AVPlayers for each deck
    self.audioDeckA = [[AVPlayer alloc] init];
    self.audioDeckB = [[AVPlayer alloc] init];
 
    //Runs query for local media
    MPMediaQuery *everything = [[MPMediaQuery alloc] init];
    NSArray *itemsFromGenericQuery = [everything items];
    self.songsList = [NSMutableArray arrayWithArray:itemsFromGenericQuery];
    
    [self.tableView reloadData];
    
    //Crossfader starts at mid so each track starts at 50% volume
    [self.audioDeckA setVolume:.50];
    [self.audioDeckB setVolume:.50];
    
    //start the configure player
    [self configurePlayer];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

//Tracks where the slider is and keeps it in check with the song
- (IBAction)progressDeckAAction:(id)sender {
    
    [self.audioDeckA seekToTime:CMTimeMakeWithSeconds((int)(self.progressDeckA.value) , 1)];
}

- (IBAction)progressDeckBAction:(id)sender {
    
    [self.audioDeckB seekToTime:CMTimeMakeWithSeconds((int)(self.progressDeckB.value) , 1)];
}

//Sets Deck A status to play or pause
- (IBAction)playPauseButtonDeckA:(id)sender {
    
    NSLog(@"Tapped Play/Pause for Deck A");
    if(self.playPauseDeckA.selected) {
        [self.audioDeckA pause];
        [self.playPauseDeckA setSelected:NO];
    } else {
        [self.audioDeckA play];
        [self.playPauseDeckA setSelected:YES];
    }
}

//Sets Deck B status to play or pause
- (IBAction)playPauseButtonDeckB:(id)sender {
    
    NSLog(@"Tapped Play/Pause for Deck B");
    if(self.playPauseDeckB.selected) {
        [self.audioDeckB pause];
        [self.playPauseDeckB setSelected:NO];
    } else {
        [self.audioDeckB play];
        [self.playPauseDeckB setSelected:YES];
    }
}

//Sets the volume for each Deck as the fader is moved, slider values are 0-1
- (IBAction)crossfaderAction:(id)sender {
   
    [self.crossfader setMaximumValue:1];
    
    float crossfaderValue = [self.crossfader value];
    
    [self.audioDeckA setVolume:1.0 - crossfaderValue];
    [self.audioDeckB setVolume:crossfaderValue];
}

//Create an info sheet with instructions on how to operate app
- (IBAction)infoAction:(id)sender {
   
    NSLog(@"Tapped Info");
    UIActionSheet *msg = [[UIActionSheet alloc] initWithTitle: @"MIXER\n1. Select a song from your library\n2. Choose to send it to Deck A or Deck B\n3. Use the Fader slider to transition in between Deck A and B\n\nPLAYLIST\n1. Select a song and send it to the Playlist\n2. Manually reorder, shuffle, or leave playlist in alphabetical order\n3. Press 'Play All' to play your playlist"
                                                     delegate:nil cancelButtonTitle:nil destructiveButtonTitle:nil otherButtonTitles:@"Dismiss", nil];
    
    [msg showInView:self.view];

}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.songsList.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *cellIdentifier = @"songCell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier];
    MPMediaItem *song = [self.songsList objectAtIndex:indexPath.row];
    NSString *songTitle = [song valueForProperty: MPMediaItemPropertyTitle];
    NSString *durationLabel = [song valueForProperty: MPMediaItemPropertyArtist];
    cell.textLabel.text = songTitle;
    cell.detailTextLabel.text = durationLabel;
    
    return cell;
}

#pragma mark - TableView Delegate Methods


-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
    self.path = indexPath;
    NSString *whatsPlaying, *temp;
    
    //Informs user if a deck is playing a song and they can decide to overwrite it or not
    if(self.playPauseDeckA.selected){
        temp = @"Deck A is currently playing\n";
    } else {
        temp = @"Deck A is not playing\n";
    }
   
    if(self.playPauseDeckB.selected){
        whatsPlaying = [temp stringByAppendingString:@"Deck B is currently playing"];
    } else {
        whatsPlaying = [temp stringByAppendingString:@"Deck B is not playing"];
    }
    
    //Creates the UIAlertView with options on what to do with a song
    UIAlertView *deckSelect = [[UIAlertView alloc] initWithTitle: @"Select a deck to load track"
                                                         message: whatsPlaying
                                                        delegate: self cancelButtonTitle: @"Cancel"
                                               otherButtonTitles:@"Deck A", @"Deck B", @"+Playlist", nil];
    [deckSelect show];
}

//Carries out the user commands from the UIAlertView
- (void)alertView:(UIAlertView *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {
    
    if(buttonIndex == 1){
        
        //Moves selected track to Deck A, updates on screen info, and autoplays
        NSLog(@"Deck A: %d", buttonIndex);
        [self.audioDeckA pause];
        MPMediaItem *song = [self.songsList objectAtIndex:_path.row];
        AVPlayerItem * currentItem = [AVPlayerItem playerItemWithURL:[song valueForProperty:MPMediaItemPropertyAssetURL]];
        
        //Moves selected song into Deck A and plays it, also sets all labels and slider accordingly
        [self.audioDeckA replaceCurrentItemWithPlayerItem:currentItem];
        [self.audioDeckA play];
        [self.playPauseDeckA setSelected:YES];
        _currentSongDeckA = [self.songsList objectAtIndex:_path.row];
        NSString *songTitle = [_currentSongDeckA valueForProperty: MPMediaItemPropertyTitle];
        self.songTitleA.text = songTitle;
        
        //Gets the length of the song being played
        NSNumber *duration=[_currentSongDeckA valueForProperty:MPMediaItemPropertyPlaybackDuration];
        
        //Sets the progress slider to be the length of the song
        [self.progressDeckA setMaximumValue:duration.floatValue];
        
    } else if(buttonIndex == 2){
        
        //Moves selected track to Deck B, updates on screen info, and autoplays
        NSLog(@"Deck B: %d", buttonIndex);
        [self.audioDeckB pause];
        MPMediaItem *song = [self.songsList objectAtIndex:_path.row];
        AVPlayerItem * currentItem = [AVPlayerItem playerItemWithURL:[song valueForProperty:MPMediaItemPropertyAssetURL]];
        
        [self.audioDeckB replaceCurrentItemWithPlayerItem:currentItem];
        [self.audioDeckB play];
        [self.playPauseDeckB setSelected:YES];
        _currentSongDeckB = [self.songsList objectAtIndex:_path.row];
        NSString *songTitle = [_currentSongDeckB valueForProperty: MPMediaItemPropertyTitle];
        self.songTitleB.text = songTitle;
        
        NSNumber *duration=[_currentSongDeckB valueForProperty:MPMediaItemPropertyPlaybackDuration];
        [self.progressDeckB setMaximumValue:duration.floatValue];
    
    } else if(buttonIndex == 3){
        
        //Moves song to playlist if conditions are met
        NSArray * controllerArray = [[self tabBarController] viewControllers];
        PlaylistViewController *p = controllerArray[1];
        
        //Won't allow adding song to playlist if playlist is currently playing
        if (p.playPausePlayAll.selected) {
            UIAlertView *error = [[UIAlertView alloc] initWithTitle: @"Cannot edit while playing" message: @"Stop your playlist if you'd like to edit" delegate: self cancelButtonTitle: @"Ok" otherButtonTitles: nil];
            
            [error show];
            return;
        } else {
        
            //Adds song to playlist and turns shuffle off so the track won't be skipped over
            [p.shuffleSwitch setOn:NO animated:NO];
            MPMediaItem *song = [self.songsList objectAtIndex:_path.row];
            [p.playlistSongsList addObject:song];
        }
        
    } else {
        
        //Nothing happens here, just a cancel
        NSLog(@"Cancel: %d", buttonIndex);
        return;
    }
}

//Two blocks that runs the second by second update of timers and progress sliders
//They also handle the task of a song ending
-(void) configurePlayer {
    __block ViewController * weakSelf = self;
    [self.audioDeckA addPeriodicTimeObserverForInterval:CMTimeMakeWithSeconds(1, 1)
                                                   queue:NULL
                                              usingBlock:^(CMTime time) {
                                                  if(!time.value) {
                                                      return;
                                                  }
                                                  
                                                  //Gets current time
                                                  int currentTime = (int)((weakSelf.audioDeckA.currentTime.value)/weakSelf.audioDeckA.currentTime.timescale);
                                                  int currentMins = (int)(currentTime/60);
                                                  int currentSec  = (int)(currentTime%60);
                                                  
                                                  //Sets labels and moves progress slider
                                                  NSString * durationLabel =
                                                  [NSString stringWithFormat:@"%02d:%02d",currentMins,currentSec];
                                                  weakSelf.durationDeckA.text = durationLabel;
                                                  weakSelf.progressDeckA.value = currentTime;
                                                  
                                                  //Handles the track ending, just pauses and turns play button off
                                                  NSNumber *duration=[weakSelf.currentSongDeckA valueForProperty:MPMediaItemPropertyPlaybackDuration];
                                                  if (currentTime == duration.intValue) {
                                                      NSLog(@"End of Deck A");
                                                      [weakSelf.audioDeckA pause];
                                                      [weakSelf.playPauseDeckA setSelected:NO];
                                                  }
                                              }];
    
    [self.audioDeckB addPeriodicTimeObserverForInterval:CMTimeMakeWithSeconds(1, 1)
                                                   queue:NULL
                                              usingBlock:^(CMTime time) {
                                                  if(!time.value) {
                                                      return;
                                                  }
                                                  
                                                  int currentTime = (int)((weakSelf.audioDeckB.currentTime.value)/weakSelf.audioDeckB.currentTime.timescale);
                                                  int currentMins = (int)(currentTime/60);
                                                  int currentSec  = (int)(currentTime%60);
                                                  
                                                  NSString * durationLabel =
                                                  [NSString stringWithFormat:@"%02d:%02d",currentMins,currentSec];
                                                  weakSelf.durationDeckB.text = durationLabel;
                                                  weakSelf.progressDeckB.value = currentTime;
                                                  
                                                  NSNumber *duration=[weakSelf.currentSongDeckB valueForProperty:MPMediaItemPropertyPlaybackDuration];
                                                  if (currentTime == duration.intValue) {
                                                      NSLog(@"End of Deck B");
                                                      [weakSelf.audioDeckB pause];
                                                      [weakSelf.playPauseDeckB setSelected:NO];
                                                  }
                                              }];

}

@end
