//
//  UIImage+BlendMode.h
//  VastchainPlugin_Example
//
//  Created by cxd on 2021/10/12.
//  Copyright © 2021 chenxiandiao. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface UIImage (BlendMode)
 - (UIImage *)imageWithBlendMode:(CGBlendMode)blendMode tintColor:(UIColor *)tintColor;

@end
