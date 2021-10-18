//
//  ImageViewController.m
//  VastchainPlugin_Example
//
//  Created by cxd on 2021/10/9.
//  Copyright © 2021 chenxiandiao. All rights reserved.
//

#import "ImageViewController.h"
#import "HWCircleView.h"
#import "UIImage+BlendMode.h"


@interface ImageViewController ()


@property BOOL extensionFlag;

@property (weak, nonatomic) IBOutlet UIImageView *circleImageView;
@property (weak, nonatomic) IBOutlet UIImageView *bottomImageView;
@property (weak, nonatomic) IBOutlet UIImageView *topImageView;
@property (weak, nonatomic) IBOutlet UIImageView *leftImageView;
@property (weak, nonatomic) IBOutlet UIImageView *rightImageView;

- (void) extensionMethod;

@end

@implementation ImageViewController


- (void)extensionMethod {
    NSLog(@"extensionMethod 1");
    [self innerMethod];
}

- (void)innerMethod{
    NSLog(@"innerMethod 1");
    [self extensionMethod];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    //    [self.view setBackgroundColor: [UIColor redColor]];
    // Do any additional setup after loading the view.
    
    //    NSString *filePath = @"/var/mobile/Containers/Data/Application/8DCD5BA1-BFDD-4526-9D40-C9E0EBB65C5C/Documents/1633763065.jpg";
    
    //    NSString *directory = [NSString stringWithFormat:@"%@%@", NSHomeDirectory(), @"/Documents"];
    //    if([NSFileManager.defaultManager fileExistsAtPath:directory] == NO) {
    //        [NSFileManager.defaultManager createDirectoryAtPath:directory withIntermediateDirectories:YES attributes:nil error:nil];
    //    }
    //    NSString *filePath = [NSString stringWithFormat:@"%@/%@.jpg", directory, @"1633766026"];
    //
    //    NSLog(@"%@", filePath);
    //    UIImage *img = [UIImage imageWithContentsOfFile:filePath];
    UIImage *img = [self createImageWithColor:[UIColor colorWithRed:0.0f green:0.0f blue:0.0f alpha:0.56f]];
    if (img == nil) {
        NSLog(@"读取失败");
    }
    
    [self.topImageView setImage:img];
    [self.bottomImageView setImage:img];
    [self.leftImageView setImage:img];
    [self.rightImageView setImage:img];
    
    
    
    
    UIImage *circleImage = [UIImage imageNamed:@"FaceCircle"];
    [self.circleImageView setImage:circleImage];
    
    
    //    [self.view setBackgroundColor: [UIColor redColor]];
    //    //创建控件
    //    HWCircleView *circleView = [[HWCircleView alloc] initWithFrame:CGRectMake(50, 200, 150, 150)];
    //    [self.view addSubview:circleView];
}

- (UIImage*) createImageWithColor: (UIColor*) color

{
    CGRect rect=CGRectMake(0.0f, 0.0f, 1.0f, 1.0f);
    
    UIGraphicsBeginImageContext(rect.size);
    
    CGContextRef context = UIGraphicsGetCurrentContext();
    
    CGContextSetFillColorWithColor(context, [color CGColor]);
    
    CGContextFillRect(context, rect);
    
    UIImage *theImage = UIGraphicsGetImageFromCurrentImageContext();
    
    UIGraphicsEndImageContext();
    
    return theImage;
    
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
