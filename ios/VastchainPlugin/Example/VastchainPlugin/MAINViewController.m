//
//  MAINViewController.m
//  VastchainPlugin
//
//  Created by chenxiandiao on 09/22/2021.
//  Copyright (c) 2021 chenxiandiao. All rights reserved.
//

#import "MAINViewController.h"
#import "BlueViewController.h"

@interface MAINViewController ()
@property (weak, nonatomic) IBOutlet UIButton *goBtn;

@end

@implementation MAINViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
}

- (IBAction)clickGoBtn:(id)sender {
    BlueViewController *viewController = [[BlueViewController alloc]init];
    [self.navigationController pushViewController:viewController animated:NO];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
