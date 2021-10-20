//
//  FACEViewController.m
//  VastchainFaceVerify
//
//  Created by chenxiandiao on 10/19/2021.
//  Copyright (c) 2021 chenxiandiao. All rights reserved.
//

#import "FACEViewController.h"
#import "VastchainFaceVerify/FaceIndexViewController.h"

@interface FACEViewController ()

@end

@implementation FACEViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
}
- (IBAction)clickFaceBtn:(id)sender {
    NSURL *url = [[NSBundle bundleForClass:[FaceIndexViewController class]] URLForResource:@"VastchainFaceVerify" withExtension:@"bundle"];
    if (url == nil)  {
        NSLog(@"null");
    } else {
        NSBundle *resource_bundle = [NSBundle bundleWithURL:url];
        UIStoryboard *faceStoryboard =[UIStoryboard storyboardWithName:@"Face" bundle:resource_bundle];
        [self.navigationController pushViewController: [faceStoryboard instantiateInitialViewController] animated:YES];
    }
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
