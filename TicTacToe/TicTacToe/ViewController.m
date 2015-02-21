//
//  ViewController.m
//  TicTacToe
//
//  Created by aK on 2/21/14.
//  Copyright (c) 2014 aK. All rights reserved.
//

#import "ViewController.h"
#import <AudioToolbox/AudioToolbox.h>
#import <AVFoundation/AVFoundation.h>

@interface ViewController ()

- (void)touchReleaseAction:(CGPoint)releasePoint;

@end

@implementation ViewController

- (void)viewDidLoad
{
    [super viewDidLoad];

    _whoseTurn.alpha = 0;
    
    oImg = [UIImage imageNamed: @"blueO.png"];
    xImg = [UIImage imageNamed: @"redX.png"];
    
    [self addGestureRecognizersToLetter:_xPan];
    [self addGestureRecognizersToLetter:_oPan];
    
    xOrigin = _xPan.center;
    oOrigin = _oPan.center;
    
    //X goes first, X is 1
    playerToken = 1;
    _oPan.alpha = .25;
    _oPan.userInteractionEnabled = FALSE;
    _whoseTurn.text = @"X";
    
    squares = [[NSMutableArray alloc] init];
    [squares addObject:_square1];
    [squares addObject:_square2];
    [squares addObject:_square3];
    [squares addObject:_square4];
    [squares addObject:_square5];
    [squares addObject:_square6];
    [squares addObject:_square7];
    [squares addObject:_square8];
    [squares addObject:_square9];
}

-(void) viewWillAppear:(BOOL)animated{
    [self sizeChange]; 
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void) updatePlayerInfo{
    if([self checkForWin]){
        NSString *winner;
        if(playerToken == 1){
            winner = @"X is the winner!";
        } else {
            winner = @"O is the winner!";
        }
        
    UIAlertView *win = [[UIAlertView alloc] initWithTitle: winner message: @"Congratulations!"delegate: self cancelButtonTitle: @"New Game" otherButtonTitles: nil];
        
        [win show];
        [self playSoundEffect:@"win"];
        [self resetBoard];
        playerToken = 2;
    }
    
    if ([self checkForTie]) {
        UIAlertView *tie = [[UIAlertView alloc] initWithTitle: @"It's a draw!" message: @"Give it another shot!"delegate: self cancelButtonTitle: @"New Game" otherButtonTitles: nil];
        
        [tie show];
        [self playSoundEffect:@"tie"];
        [self resetBoard];
        playerToken = 2;
    }
    
    if(playerToken == 1) {
        playerToken= 2;
        _oPan.alpha = 1;
        _xPan.center = xOrigin;
        _xPan.alpha = .25;
        _oPan.userInteractionEnabled = TRUE;
        _xPan.userInteractionEnabled = FALSE;
        _whoseTurn.text = @"O";
    } else {
        if(playerToken == 2) {
            playerToken = 1;
            _xPan.alpha = 1;
            _oPan.alpha = .25;
            _oPan.center = oOrigin;
            _xPan.userInteractionEnabled = TRUE;
            _oPan.userInteractionEnabled = FALSE;
            _whoseTurn.text = @"X";
        }
    }
}

- (void) resetBoard{
    
    _square1.image = NULL;
    _square2.image = NULL;
    _square3.image = NULL;
    _square4.image = NULL;
    _square5.image = NULL;
    _square6.image = NULL;
    _square7.image = NULL;
    _square8.image = NULL;
    _square9.image = NULL;
    
    playerToken = 1;
    _xPan.alpha = 1;
    _oPan.alpha = .25;
    _oPan.center = oOrigin;
    _xPan.center = xOrigin;
    _whoseTurn.text =@"X";
    _xPan.userInteractionEnabled = TRUE;
    _oPan.userInteractionEnabled = FALSE;
    [self sizeChange];
}

- (BOOL) checkForTie{
    if(_square1.image && _square2.image && _square3.image && _square4.image && _square5.image && _square6.image && _square7.image && _square8.image && _square9.image != NULL) {
        return YES;
    }
    
    return NO;
}

- (BOOL) checkForWin{
    // HORIZONTAL WINS
    if((_square1.image == _square2.image) & (_square2.image == _square3.image) & (_square1.image != NULL)){
        return YES;
    }
    if((_square4.image == _square5.image) & (_square5.image == _square6.image) & (_square4.image != NULL)){
        return YES;
    }
    if((_square7.image == _square8.image) & (_square8.image == _square9.image) & (_square7.image != NULL)){
        return YES;
    }
    
    // VERTICAL WINS
    if((_square1.image == _square4.image) & (_square4.image == _square7.image) & (_square1.image!= NULL)){
        return YES;
    }
    if((_square2.image == _square5.image) & (_square5.image == _square8.image) & (_square2.image != NULL)){
        return YES;
    }
    if((_square3.image == _square6.image) & (_square6.image == _square9.image) & (_square3.image != NULL)){
        return YES;
    }
    
    // DIAGONAL WINS
    if((_square1.image == _square5.image) & (_square5.image == _square9.image) & (_square1.image != NULL)){
        return YES;
    }
    if((_square3.image == _square5.image) & (_square5.image == _square7.image) & (_square3.image != NULL)){
        return YES;
    }
    return NO;
}

- (IBAction)buttonReset:(UIButton *)sender {
    [self resetBoard];
}

- (IBAction)buttonInfo:(UIButton *)sender {
    UIActionSheet *msg = [[UIActionSheet alloc] initWithTitle: @"1. Drag your letter to an empty square.\n2. Try to get three in a row." delegate:nil cancelButtonTitle:nil destructiveButtonTitle:nil otherButtonTitles:@"Dismiss", nil];
    
    [msg showInView:self.view];
}

- (void)touchReleaseAction:(CGPoint)releasePoint{
    // check to see which UIImage view was touched
    for (UIImageView *square in squares) {
        if(CGRectContainsPoint([square frame], releasePoint)){
            
            if (square.image != NULL) {
                [self animate];
                [self playSoundEffect:@"illegalMove"];
            } else {
            
                if(playerToken == 1){
                    square.image = xImg;
                }
                if(playerToken == 2){
                    square.image = oImg;
                }
                
                [self playSoundEffect:@"legalMove"];
                [self updatePlayerInfo];
                [self sizeChange];
            }
        }
    }
    
    //NSLog(@"touchReleaseAction called\n");
}

- (void)panPiece:(UIPanGestureRecognizer *)gestureRecognizer
{
    UIView *piece = [gestureRecognizer view];
    [[piece superview] bringSubviewToFront:piece];

    if ([gestureRecognizer state] == UIGestureRecognizerStateBegan ||
        [gestureRecognizer state] == UIGestureRecognizerStateChanged) {
        
        CGPoint translation = [gestureRecognizer translationInView:[piece superview]];
        
        [piece setCenter:CGPointMake([piece center].x + translation.x,
                                     [piece center].y + translation.y)];
        
        [gestureRecognizer setTranslation:CGPointZero inView:[piece superview]];
    }
    
    if ([gestureRecognizer state] == UIGestureRecognizerStateEnded) {
        CGPoint releasePoint = [piece center];
        [self touchReleaseAction: releasePoint];
    }
    //[self playSoundEffect:@"pan"];

    
}

- (void)addGestureRecognizersToLetter:(UIView *)piece{
    UIPanGestureRecognizer *panGesture = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(panPiece:)];
    [panGesture setMaximumNumberOfTouches:1];
    //[panGesture setDelegate:self];
    [piece addGestureRecognizer:panGesture];
}

- (void)playSoundEffect:(NSString*)soundName{
    NSString *soundPath = [[NSBundle mainBundle] pathForResource:soundName ofType:@"aiff"];
    NSURL *soundURL = [NSURL fileURLWithPath:soundPath];
    SystemSoundID soundID;
    AudioServicesCreateSystemSoundID((__bridge CFURLRef)soundURL, &soundID);
    AudioServicesPlaySystemSound(soundID);
}

- (void)animate{
    [UIView animateWithDuration:0.5 delay:0.05 options:UIViewAnimationOptionCurveEaseInOut
         animations:^{
             if (playerToken == 1) {
                 _xPan.center = xOrigin;
             } else {
                 _oPan.center = oOrigin;
             }
         }
         completion:^(BOOL completed) {
             //NSLog(@">>>> Moved it back");
         }
    ];
}

- (void)sizeChange{
    [UIView animateWithDuration:0.5 delay:0.05 options:UIViewAnimationOptionCurveEaseInOut
         animations:^{
             
             if (playerToken == 1) {
                 _xPan.transform = CGAffineTransformMakeScale(1.5,1.5);
             } else {
                 _oPan.transform = CGAffineTransformMakeScale(1.5,1.5);
             }
             _oPan.transform = CGAffineTransformMakeScale(1,1);
             _xPan.transform = CGAffineTransformMakeScale(1,1);

         }
         completion:^(BOOL completed) {
             //NSLog(@">>>> changed size");
         }
     ];
}

//- (void)animateOffScreen{
//    [UIView animateWithDuration:0.5 delay:0.05 options:UIViewAnimationOptionCurveEaseInOut
//         animations:^{
//             _square1.transform = CGAffineTransformMakeScale(1.5,1.5);
//             _square1.transform = CGAffineTransformMakeScale(1,1);
//
//             _square2.transform = CGAffineTransformMakeScale(1.5,1.5);
//             _square3.transform = CGAffineTransformMakeScale(1.5,1.5);
//             _square4.transform = CGAffineTransformMakeScale(1.5,1.5);
//             _square5.transform = CGAffineTransformMakeScale(1.5,1.5);
//             _square6.transform = CGAffineTransformMakeScale(1.5,1.5);
//             _square7.transform = CGAffineTransformMakeScale(1.5,1.5);
//             _square8.transform = CGAffineTransformMakeScale(1.5,1.5);
//             _square9.transform = CGAffineTransformMakeScale(1.5,1.5);
//             
//             //There's an issue with operations called after the function, they seem to interupt this function.
//             
//         }
//         completion:^(BOOL completed) {
//             NSLog(@">>>> animated off screen");
//         }
//     ];
//}

@end
