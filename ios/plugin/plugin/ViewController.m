//
//  ViewController.m
//  bluetooth
//
//  Created by cxd on 2021/9/16.
//


#import "ViewController.h"
#import "BlueViewController.h"

@interface ViewController () {
    
    __weak IBOutlet UIButton *blueBtn;
    
}



@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
}

- (IBAction)clickBtn:(id)sender {
    NSLog(@"点击");
    BlueViewController *blueViewController = [[BlueViewController alloc]initWithUrl:@"http://www.baidu.com"];
    [self.navigationController pushViewController:blueViewController animated:NO];
}

@end
