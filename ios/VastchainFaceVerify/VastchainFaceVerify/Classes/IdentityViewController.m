//
//  IdentityViewController.m
//  VastchainPlugin_Example
//
//  Created by cxd on 2021/10/18.
//  Copyright © 2021 chenxiandiao. All rights reserved.
//

#import "IdentityViewController.h"
#import "FaceIndexViewController.h"

@interface IdentityViewController ()
@property (weak, nonatomic) IBOutlet UITextField *etIdCard;
@property (weak, nonatomic) IBOutlet UITextField *etName;

@end

@implementation IdentityViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    
//    self.navigationItem.backBarButtonItem = [[UIBarButtonItem alloc]initWithTitle:@"22" style:UIBarButtonItemStylePlain target:self action:nil];
    
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
//    if ([segue.identifier isEqualToString:@"faceIndexViewController"]) {
        FaceIndexViewController *controller = (FaceIndexViewController *)segue.destinationViewController;
        controller.idCard = _etIdCard.text;
        controller.name = _etName.text;
//    } else {
//        NSLog(@"EEE");
//
//    }
    NSLog(@"%@", segue.identifier);
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    //隐藏返回按钮的文字
    NSArray *viewControllerArr = [self.navigationController viewControllers];
    long previousViewControllerIndex = [viewControllerArr indexOfObject:self] - 1;
    UIViewController *previous;
    if (previousViewControllerIndex >= 0) {
        previous = [viewControllerArr objectAtIndex:previousViewControllerIndex];
        previous.navigationItem.backBarButtonItem = [[UIBarButtonItem alloc]
                                                 initWithTitle:@"Back"
                                                 style:UIBarButtonItemStylePlain
                                                 target:self
                                                 action:nil];
    }
//    self.navigationController.navigationBar.tintColor = [UIColor whiteColor];
}

@end
