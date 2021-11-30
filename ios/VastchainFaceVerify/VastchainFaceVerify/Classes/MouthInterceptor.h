//
//  MouthInterceptor.h
//  VastchainPlugin_Example
//
//  Created by cxd on 2021/10/14.
//  Copyright © 2021 chenxiandiao. All rights reserved.
//

#import "LiveInterceptor.h"
#import "InterceptChain.h"
#import <AVFoundation/AVFoundation.h>

NS_ASSUME_NONNULL_BEGIN

#define PHOTO_COUNT 20
#define COMPARE_COUNT 5

@interface MouthInterceptor : LiveInterceptor


@property NSMutableArray *mouthPhotos;
@property BOOL mouthChecked;
@property NSString *requestId;
@property UILabel *tipsLabel;
@property NSInteger tryCount;
@property (weak, nonatomic) id<FaceDelegate> delegate;

-(id) initWithRequestId:(NSString *)requestId label:(UILabel *)tipsLabel;

@end

NS_ASSUME_NONNULL_END
