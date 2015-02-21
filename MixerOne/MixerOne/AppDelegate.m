//
//  AppDelegate.m
//  MixerOne
//
//  Created by aK on 3/4/14.
//  Copyright (c) 2014 aK. All rights reserved.
//

#import "AppDelegate.h"
#import "ViewController.h"
#import "PlaylistViewController.h"


@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    // Override point for customization after application launch.
    return YES;
}
							
- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

//Saves playlist to NSUserDefaults as app enters the background
//Also creates an excpetion that allows music to continue playing when app enters background and/or device is locked
- (void)applicationDidEnterBackground:(UIApplication *)application
{

    AVAudioSession *audioSession = [AVAudioSession sharedInstance];
    
    NSError *setCategoryError = nil;
    BOOL success = [audioSession setCategory:AVAudioSessionCategoryPlayback error:&setCategoryError];
    
    NSError *activationError = nil;
    success = [audioSession setActive:YES error:&activationError];
    
    UITabBarController *v = (UITabBarController *)self.window.rootViewController;
    NSArray *controllerArray = [v viewControllers];
    PlaylistViewController *p = (PlaylistViewController *)controllerArray[1];
    [p savePlaylist];

}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}



@end
