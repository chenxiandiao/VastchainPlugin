//
//  HWCircleView.h
//  VastchainPlugin_Example
//
//  Created by cxd on 2021/10/12.
//  Copyright © 2021 chenxiandiao. All rights reserved.
//
#import <UIKit/UIKit.h>

@interface HWCircleView : UIView

@property (nonatomic, assign) CGFloat progress;

//进度条颜色
@property(nonatomic,strong) UIColor *progerssColor;
//进度条背景颜色
@property(nonatomic,strong) UIColor *progerssBackgroundColor;
//进度条的宽度
@property(nonatomic,assign) CGFloat progerWidth;
//进度数据字体大小
@property(nonatomic,assign)CGFloat percentageFontSize;
//进度数字颜色
@property(nonatomic,strong) UIColor *percentFontColor;

@end


