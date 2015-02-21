//
//  ViewController.h
//  TicTacToe
//
//  Created by aK on 2/21/14.
//  Copyright (c) 2014 aK. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ViewController : UIViewController{
    NSMutableArray *squares;
    IBOutlet UIImage* oImg;
    IBOutlet UIImage* xImg;
    NSInteger playerToken;
    CGPoint xOrigin;
    CGPoint oOrigin;
}

@property (weak, nonatomic) IBOutlet UIImageView *square1;
@property (weak, nonatomic) IBOutlet UIImageView *square2;
@property (weak, nonatomic) IBOutlet UIImageView *square3;
@property (weak, nonatomic) IBOutlet UIImageView *square4;
@property (weak, nonatomic) IBOutlet UIImageView *square5;
@property (weak, nonatomic) IBOutlet UIImageView *square6;
@property (weak, nonatomic) IBOutlet UIImageView *square7;
@property (weak, nonatomic) IBOutlet UIImageView *square8;
@property (weak, nonatomic) IBOutlet UIImageView *square9;

@property (weak, nonatomic) IBOutlet UILabel *whoseTurn;
@property (weak, nonatomic) IBOutlet UIImageView *board;
@property (weak, nonatomic) IBOutlet UIButton *resetButton;
@property (weak, nonatomic) IBOutlet UIImageView *xPan;
@property (weak, nonatomic) IBOutlet UIImageView *oPan;

- (IBAction)buttonReset:(UIButton *)sender;
- (IBAction)buttonInfo:(UIButton *)sender;

-(void) updatePlayerInfo;
-(void) resetBoard;
-(BOOL) checkForWin;


@end
