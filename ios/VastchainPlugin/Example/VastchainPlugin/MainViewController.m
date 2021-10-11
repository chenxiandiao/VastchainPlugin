//
//  MAINViewController.m
//  VastchainPlugin
//
//  Created by chenxiandiao on 09/22/2021.
//  Copyright (c) 2021 chenxiandiao. All rights reserved.
//

#import "MainViewController.h"
#import "FaceViewController.h"
#import "ImageViewController.h"
#import "VastchainPlugin/BlueViewController.h"
#import "VastchainPlugin/WCQRCodeVC.h"

@interface MainViewController ()
@property (weak, nonatomic) IBOutlet UIButton *goBtn;
@property (weak, nonatomic) IBOutlet UIButton *readImageBtn;

@end

@implementation MainViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
}

- (IBAction)clickGoBtn:(id)sender {
    //    [self openBlue];
    //    [self openScan];
    [self openTest];
}
- (IBAction)clickReadImageBtn:(id)sender {
    UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:[NSBundle mainBundle]];
    ImageViewController *controller = [storyboard instantiateViewControllerWithIdentifier:@"imageViewController"];
    
    [self.navigationController pushViewController:controller animated:YES];
}

- (void) openTest {
    UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:[NSBundle mainBundle]];
    FaceViewController *controller = [storyboard instantiateViewControllerWithIdentifier:@"faceViewController"];
    [self.navigationController pushViewController:controller animated:YES];
}

- (void) openBlue {
    BlueViewController *viewController = [[BlueViewController alloc]initWithUrl:@"http://10.144.1.116:8000"];
    [self.navigationController pushViewController:viewController animated:NO];
}

- (void) openScan {
    WCQRCodeVC *WCVC = [[WCQRCodeVC alloc] init];
    [self.navigationController pushViewController:WCVC animated:YES];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
