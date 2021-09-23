//
//  BlueViewController.m
//  VastchainPlugin_Example
//
//  Created by cxd on 2021/9/23.
//  Copyright © 2021 chenxiandiao. All rights reserved.
//

#import "BlueViewController.h"

@interface BlueViewController ()

@end

@implementation BlueViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self.view setBackgroundColor:[UIColor whiteColor]];
    UIImageView *backBtn = [[UIImageView alloc]initWithFrame:CGRectMake(0,110,24,24)];
    [backBtn setImage:[UIImage imageNamed:@"BackIcon"]];
    [self.view addSubview:backBtn];
    backBtn.userInteractionEnabled = YES;
    UITapGestureRecognizer *singleTap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(goBack)];
    [backBtn addGestureRecognizer:singleTap];
    
    UILabel *aLabel = [[UILabel alloc]initWithFrame:CGRectMake(30, 100,[UIScreen mainScreen].bounds.size.width-60, 44)];
        aLabel.numberOfLines = 0;
        aLabel.textColor = [UIColor blueColor];
        aLabel.backgroundColor = [UIColor clearColor];
        aLabel.textAlignment = NSTextAlignmentCenter;
        aLabel.text = @"蓝牙打卡";
        [self.view addSubview:aLabel];
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
