//
//  MAINViewController.m
//  VastchainPlugin
//
//  Created by chenxiandiao on 09/22/2021.
//  Copyright (c) 2021 chenxiandiao. All rights reserved.
//

#import "MainViewController.h"
#import "FaceIndexViewController.h"
#import "VastchainPlugin/FaceTestViewController.h"
#import "ImageViewController.h"
#import "VastchainPlugin/BlueViewController.h"
#import "VastchainPlugin/WCQRCodeVC.h"

@interface MainViewController ()
@property (weak, nonatomic) IBOutlet UIButton *openQrScanbtn;

@property (weak, nonatomic) IBOutlet UIButton *faceBtn;
@property (weak, nonatomic) IBOutlet UIButton *readImageBtn;

@end

@implementation MainViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
}

- (IBAction)clickFaceCompareBtn:(id)sender {
    UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:[NSBundle mainBundle]];
    FaceIndexViewController *controller = [storyboard instantiateViewControllerWithIdentifier:@"faceIndexViewController"];
    [self.navigationController pushViewController:controller animated:YES];
}
- (IBAction)clickOpenQrScan:(id)sender {
    WCQRCodeVC *WCVC = [[WCQRCodeVC alloc] init];
    [self.navigationController pushViewController:WCVC animated:YES];
}
- (IBAction)clickReadImageBtn:(id)sender {
    UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:[NSBundle mainBundle]];
    ImageViewController *controller = [storyboard instantiateViewControllerWithIdentifier:@"imageViewController"];
    
    [self.navigationController pushViewController:controller animated:YES];
}

- (IBAction)clickStoryBoardTest:(id)sender {
    NSURL *url = [[NSBundle bundleForClass:[FaceTestViewController class]] URLForResource:@"VastchainPlugin" withExtension:@"bundle"];
    if (url == nil)  {
        NSLog(@"null");
    } else {
        NSBundle *resource_bundle = [NSBundle bundleWithURL:url];
        UIStoryboard *faceStoryboard =[UIStoryboard storyboardWithName:@"Face" bundle:resource_bundle];
        [self.navigationController pushViewController: [faceStoryboard instantiateInitialViewController] animated:YES];
    }
  
}

- (void) openBlue {
    BlueViewController *viewController = [[BlueViewController alloc]initWithUrl:@"http://10.144.1.116:8000"];
    [self.navigationController pushViewController:viewController animated:NO];
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
