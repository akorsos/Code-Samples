//
//  PlaylistViewController.h
//  MixerOne
//
//  Created by aK on 3/7/14.
//  Copyright (c) 2014 aK. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MediaPlayer/MediaPlayer.h>
#import <AVFoundation/AVFoundation.h>

@interface PlaylistViewController : UIViewController<UITableViewDelegate, UITableViewDataSource>

@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UIButton *playPausePlayAll;
@property (weak, nonatomic) IBOutlet UISlider *progressPlayAll;
@property (weak, nonatomic) IBOutlet UILabel *durationPlayAll;
@property (weak, nonatomic) IBOutlet UILabel *nextTrackLabel;
@property (weak, nonatomic) IBOutlet UIButton *editPlaylist;
@property (weak, nonatomic) IBOutlet UISwitch *shuffleSwitch;
@property (weak, nonatomic) IBOutlet UISwitch *loopSwitch;

@property(nonatomic, getter=isEditing) BOOL editing;

- (void) populatePlaylist;
- (void) savePlaylist;
- (IBAction)playPausePlayAllAction:(id)sender;
- (IBAction)progressPlayAllAction:(id)sender;
- (IBAction)editPlaylistAction:(id)sender;
- (IBAction)shuffleSwitchAction:(id)sender;
- (IBAction)loopSwitchAction:(id)sender;

@property (strong, nonatomic, retain) NSMutableArray *playlistSongsList;

@end
