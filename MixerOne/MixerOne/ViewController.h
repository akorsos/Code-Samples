//
//  ViewController.h
//  MixerOne
//
//  Created by aK on 3/4/14.
//  Copyright (c) 2014 aK. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MediaPlayer/MediaPlayer.h>
#import <AVFoundation/AVFoundation.h>


@interface ViewController : UIViewController<UITableViewDelegate, UITableViewDataSource, UIAlertViewDelegate>

@property (weak, nonatomic) IBOutlet UIButton *playPauseDeckA;
@property (weak, nonatomic) IBOutlet UIButton *playPauseDeckB;
@property (weak, nonatomic) IBOutlet UILabel *songTitleA;
@property (weak, nonatomic) IBOutlet UILabel *songTitleB;
@property (weak, nonatomic) IBOutlet UILabel *durationDeckA;
@property (weak, nonatomic) IBOutlet UILabel *durationDeckB;
@property (weak, nonatomic) IBOutlet UISlider *crossfader;
@property (weak, nonatomic) IBOutlet UISlider *progressDeckA;
@property (weak, nonatomic) IBOutlet UISlider *progressDeckB;
@property (weak, nonatomic) IBOutlet UITableView *tableView;

- (IBAction)progressDeckAAction:(id)sender;
- (IBAction)progressDeckBAction:(id)sender;
- (IBAction)playPauseButtonDeckA:(id)sender;
- (IBAction)playPauseButtonDeckB:(id)sender;
- (IBAction)crossfaderAction:(id)sender;
- (IBAction)infoAction:(id)sender;


@property (strong, nonatomic) NSMutableArray *playList;

@end
