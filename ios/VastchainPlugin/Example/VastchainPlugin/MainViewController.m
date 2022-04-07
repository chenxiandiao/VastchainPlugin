//
//  MAINViewController.m
//  VastchainPlugin
//
//  Created by chenxiandiao on 09/22/2021.
//  Copyright (c) 2021 chenxiandiao. All rights reserved.
//

#import "MainViewController.h"
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
//    NSURL *url = [[NSBundle bundleForClass:[FaceTestViewController class]] URLForResource:@"VastchainPlugin" withExtension:@"bundle"];
//    if (url == nil)  {
//        NSLog(@"null");
//    } else {
//        NSBundle *resource_bundle = [NSBundle bundleWithURL:url];
//        UIStoryboard *faceStoryboard =[UIStoryboard storyboardWithName:@"Face" bundle:resource_bundle];
//        [self.navigationController pushViewController: [faceStoryboard instantiateInitialViewController] animated:YES];
//    }
  
}

- (IBAction)clickOpenWebView:(id)sender {
    [self openBlue];
}

- (void) openBlue {
//    BlueViewController *viewController = [[BlueViewController alloc]initWithUrl:@"http://10.144.1.116/#/qrCode/home"];
    BlueViewController *viewController = [[BlueViewController alloc]initWithUrl:@"http://10.144.2.167:8089/#/storehouse/basic/useBackWarehousing?token=FuZ-eZsufWu02Xx3vkqvAEOWzqjFty8L&org_id=271955190993424384&place_id=283978561106186240&commodity_id=&qr_code_id=&warehouse_id=273606059880853504&scene_function_type=C,B,A"];
//    %E9%99%%E8%B4%A4%E9%9B%95
//    BlueViewController *viewController = [[BlueViewController alloc]initWithUrl:@"http://10.144.1.116:8080/examples/webgl_loader_gltf.html"];
//    BlueViewController *viewController = [[BlueViewController alloc]initWithUrl:@"http://10.144.1.116:8080/examples/webgl_loader_gltf_compressed.html"];
    [self.navigationController pushViewController:viewController animated:NO];
    
//http://10.144.3.113:8089/#/storehouse/basic/exWarehousingSell?token=R5MK0IGgx6naRUa_UN5dHqE-xccaRUE9&org_id=271955190993424384&place_id=283978561106186240&commodity_id=&qr_code_id=&warehouse_id=273606059880853504&scene_function_type=C,B,A
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
