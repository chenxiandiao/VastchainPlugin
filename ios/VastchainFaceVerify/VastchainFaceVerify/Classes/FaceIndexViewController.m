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
//    dispatch_async(dispatch_get_global_queue(QOS_CLASS_BACKGROUND, 0), ^{
//            NSLog(@"hello");
//               NSURL *url = [[NSBundle mainBundle] URLForResource:@"VastchainFaceVerify" withExtension:@"bundle"];
//               if (!url) {
//                   NSLog(@"image bundle 组件化");
//                   url = [[NSBundle bundleForClass:[self class]] URLForResource:@"VastchainFaceVerify" withExtension:@"bundle"];
//               }
//               NSBundle *resource_bundle = [NSBundle bundleWithURL:url];
//               UIImage *image = [UIImage imageNamed:@"Person" inBundle:resource_bundle compatibleWithTraitCollection:nil] ;
//               [self detectFace:image];
//    });
}

//- (BOOL) detectFace: (UIImage*)image{
//    NSDate* tmpStartData = [NSDate date];
//    CIContext *context = [CIContext context];
//    NSDictionary *opts = @{
//        CIDetectorAccuracy: CIDetectorAccuracyHigh,
//    };
//    CIDetector *detector = [CIDetector detectorOfType:CIDetectorTypeFace context:context options:opts];
//    double deltaTime = [[NSDate date] timeIntervalSinceDate:tmpStartData];
//    NSLog(@"cost time 1 = %f", deltaTime);
//    CIImage *myImage = [[CIImage alloc] initWithImage:image options:nil];
//    double deltaTime2 = [[NSDate date] timeIntervalSinceDate:tmpStartData];
//    NSLog(@"cost time 2 = %f", deltaTime2);
//    
//    NSArray *features = [detector featuresInImage:myImage];
//    double deltaTime3 = [[NSDate date] timeIntervalSinceDate:tmpStartData];
//    NSLog(@"cost time 3 = %f", deltaTime3);
//    if (features.count>0) {
//        NSLog(@"检测人脸");
//        return YES;
//    }
//    return NO;
//}


- (IBAction)faceCheckWityEyeMouth:(id)sender {
    NSURL *url = [[NSBundle bundleForClass:[self class]] URLForResource:@"VastchainFaceVerify" withExtension:@"bundle"];
    NSBundle *resource_bundle = [NSBundle bundleWithURL:url];
    UIStoryboard *storyboard =[UIStoryboard storyboardWithName:@"Face" bundle:resource_bundle];
    FaceViewController *controller = [storyboard instantiateViewControllerWithIdentifier:@"faceViewController"];
    controller.needEyeCheck = YES;
    controller.needMouthCheck = YES;
    controller.idCard = _idCard;
    controller.name = _name;
    [self saveInfo:_name idCard:_idCard];
    [self.navigationController pushViewController:controller animated:YES];
}


- (IBAction)faceCheckWithEye:(id)sender {
    NSURL *url = [[NSBundle bundleForClass:[self class]] URLForResource:@"VastchainFaceVerify" withExtension:@"bundle"];
    NSBundle *resource_bundle = [NSBundle bundleWithURL:url];
    UIStoryboard *storyboard =[UIStoryboard storyboardWithName:@"Face" bundle:resource_bundle];
    FaceViewController *controller = [storyboard instantiateViewControllerWithIdentifier:@"faceViewController"];
    controller.needEyeCheck = YES;
    controller.idCard = _idCard;
    controller.name = _name;
    [self saveInfo:_name idCard:_idCard];
    [self.navigationController pushViewController:controller animated:YES];
}


- (IBAction)faceCheckWithMouth:(id)sender {
    NSURL *url = [[NSBundle bundleForClass:[self class]] URLForResource:@"VastchainFaceVerify" withExtension:@"bundle"];
    NSBundle *resource_bundle = [NSBundle bundleWithURL:url];
    UIStoryboard *storyboard =[UIStoryboard storyboardWithName:@"Face" bundle:resource_bundle];
    FaceViewController *controller = [storyboard instantiateViewControllerWithIdentifier:@"faceViewController"];
    controller.needMouthCheck = YES;
    controller.idCard = _idCard;
    controller.name = _name;
    [self saveInfo:_name idCard:_idCard];
    [self.navigationController pushViewController:controller animated:YES];
}


- (IBAction)faceCheck:(id)sender {
//    UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Face" bundle:[NSBundle bundleForClass:[self class]]];
    
    NSURL *url = [[NSBundle bundleForClass:[self class]] URLForResource:@"VastchainFaceVerify" withExtension:@"bundle"];
    NSBundle *resource_bundle = [NSBundle bundleWithURL:url];
    UIStoryboard *storyboard =[UIStoryboard storyboardWithName:@"Face" bundle:resource_bundle];
        
    FaceViewController *controller = [storyboard instantiateViewControllerWithIdentifier:@"faceViewController"];
    controller.idCard = _idCard;
    controller.name = _name;
    [self saveInfo:_name idCard:_idCard];
    [self.navigationController pushViewController:controller animated:YES];
}


- (void)saveInfo:(NSString *)name idCard:(NSString *)idCard {
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    [userDefault setObject:name forKey:@"name"];
    [userDefault setObject:idCard forKey:@"idCard"];
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
