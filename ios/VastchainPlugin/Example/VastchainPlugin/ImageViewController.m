//
//  ImageViewController.m
//  VastchainPlugin_Example
//
//  Created by cxd on 2021/10/9.
//  Copyright © 2021 chenxiandiao. All rights reserved.
//

#import "ImageViewController.h"

@interface ImageViewController ()

@property (weak, nonatomic) IBOutlet UIImageView *savedImage;

@end

@implementation ImageViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
//    NSString *filePath = @"/var/mobile/Containers/Data/Application/8DCD5BA1-BFDD-4526-9D40-C9E0EBB65C5C/Documents/1633763065.jpg";
    
    NSString *directory = [NSString stringWithFormat:@"%@%@", NSHomeDirectory(), @"/Documents"];
    if([NSFileManager.defaultManager fileExistsAtPath:directory] == NO) {
        [NSFileManager.defaultManager createDirectoryAtPath:directory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    NSString *filePath = [NSString stringWithFormat:@"%@/%@.jpg", directory, @"1633766026"];
    
    NSLog(@"%@", filePath);
    UIImage *img = [UIImage imageWithContentsOfFile:filePath];
    if (img == nil) {
        NSLog(@"读取失败");
    }
    [self.savedImage setImage:img];
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
