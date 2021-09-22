//
//  WebViewController.m
//  bluetooth
//
//  Created by cxd on 2021/9/18.
//

#import <Foundation/Foundation.h>
#import "WebViewController.h"

@implementation WebViewController


- (void)viewDidLoad {
    [self.view setBackgroundColor: [UIColor whiteColor]];
    
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

- (void) goBack {
    NSLog(@"返回");
}
@end
