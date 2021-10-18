//
//  FaceViewController.h
//  VastchainPlugin_Example
//
//  Created by cxd on 2021/9/30.
//  Copyright © 2021 chenxiandiao. All rights reserved.
//

@import AVFoundation;
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN


@interface FaceViewController : UIViewController


@property int count;
@property BOOL savePhoto;
@property BOOL faceCompareSuccess;
@property BOOL mouthChecked;
@property NSString *requestId;
@property NSString *comparePhoto;
@property NSMutableArray *mouthPhotos;
@property BOOL startMouthCheckFlag;

@property NSDate* tmpStartData;

@property BOOL needEyeCheck;
@property BOOL needMouthCheck;


@end

NS_ASSUME_NONNULL_END
