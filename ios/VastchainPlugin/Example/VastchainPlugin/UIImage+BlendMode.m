//
//  UIImage+BlendMode.m
//  VastchainPlugin_Example
//
//  Created by cxd on 2021/10/12.
//  Copyright © 2021 chenxiandiao. All rights reserved.
//

#import "UIImage+BlendMode.h"

@implementation UIImage (BlendMode)

- (UIImage *)imageWithBlendMode:(CGBlendMode)blendMode tintColor:(UIColor *)tintColor {
    UIImage *img;
    UIGraphicsBeginImageContextWithOptions(self.size, NO, 0); //开始图片上下文绘制

    [tintColor setFill]; //填充颜色
    CGRect newRect = CGRectMake(0, 0, self.size.width, self.size.height);
    UIRectFill(newRect);
    [self drawInRect:newRect blendMode:blendMode alpha:1.0]; //设置绘画透明混合模式和透明度
    if (blendMode == kCGBlendModeOverlay) {
        [self drawInRect:newRect blendMode:kCGBlendModeDestinationIn alpha:1.0]; //能保留透明度信息
    }

    img = UIGraphicsGetImageFromCurrentImageContext();

    UIGraphicsEndImageContext(); //结束图片上下文绘制
    return img;
}

@end
