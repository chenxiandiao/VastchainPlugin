//
//  FaceIndexViewController.m
//  VastchainPlugin_Example
//
//  Created by cxd on 2021/10/15.
//  Copyright © 2021 chenxiandiao. All rights reserved.
//

#import "FaceIndexViewController.h"
#import "FaceViewController.h"

@interface FaceIndexViewController ()

@end

@implementation FaceIndexViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}


- (IBAction)faceCheckWityEyeMouth:(id)sender {
    UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:[NSBundle mainBundle]];
    FaceViewController *controller = [storyboard instantiateViewControllerWithIdentifier:@"faceViewController"];
    controller.needEyeCheck = YES;
    controller.needMouthCheck = YES;
    [self.navigationController pushViewController:controller animated:YES];
}


- (IBAction)faceCheckWithEye:(id)sender {
    UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:[NSBundle mainBundle]];
    FaceViewController *controller = [storyboard instantiateViewControllerWithIdentifier:@"faceViewController"];
    controller.needEyeCheck = YES;
    [self.navigationController pushViewController:controller animated:YES];
}


- (IBAction)faceCheckWithMouth:(id)sender {
    UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:[NSBundle mainBundle]];
    FaceViewController *controller = [storyboard instantiateViewControllerWithIdentifier:@"faceViewController"];
    controller.needMouthCheck = YES;
    [self.navigationController pushViewController:controller animated:YES];
}


- (IBAction)faceCheck:(id)sender {
    UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:[NSBundle mainBundle]];
    FaceViewController *controller = [storyboard instantiateViewControllerWithIdentifier:@"faceViewController"];
    [self.navigationController pushViewController:controller animated:YES];
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
