//
//  MouthInterceptor.m
//  VastchainPlugin_Example
//
//  Created by cxd on 2021/10/14.
//  Copyright © 2021 chenxiandiao. All rights reserved.
//

#import "MouthInterceptor.h"
#import "FaceManager.h"

@implementation MouthInterceptor




- (id)initWithRequestId:(NSString *)requestId label:(UILabel *)tipsLabel{
    self = [super init];
    if (self) {
        _requestId = requestId;
        _mouthPhotos = [[NSMutableArray alloc]initWithCapacity:20];
        _tipsLabel = tipsLabel;
    }
    return self;
}

- (BOOL)checked {
    return _mouthChecked;
}

- (void)procceed:(NSString *)file chain:(InterceptChain *)chain {
    
    if (self.checked) {
        [chain procced:file];
        return;
    }
    
    if (_mouthPhotos.count == 0) {
        NSLog(@"开始保存张嘴图片");
        [[FaceManager shareManager] playSoundName:@"mouth.wav"];
    }
   
    [_mouthPhotos addObject:file];
    
    if (_mouthPhotos.count == PHOTO_COUNT) {
        NSLog(@"结束保存张嘴图片");
        [FaceManager shareManager].savePhoto = NO;
        
        if([FaceManager shareManager].skipFaceCheck) {
            _mouthChecked = YES;
            if ([chain isLast]) {
                NSLog(@"人脸检测完成");
                _tipsLabel.text = @"人脸检测完成";
            } else {
                [FaceManager shareManager].savePhoto = YES;
                [self showNextTips:chain];
            }
            return;
        }
        
        [super checkLive:_mouthPhotos type:@"mouth" requestId:_requestId completionHandler:^(NSURLResponse * _Nonnull response, id  _Nonnull responseObject, NSError * _Nonnull error) {
            if (error) {
                NSLog(@"Error: %@", error);
                [FaceManager shareManager].savePhoto = YES;
            } else {
                NSLog(@"%@ %@", response, responseObject);
                NSLog(@"%@", [responseObject objectForKey:@"code"]);
                NSLog(@"%@", [responseObject objectForKey:@"msg"]);
                
    //            double deltaTime = [[NSDate date] timeIntervalSinceDate:self.tmpStartData];
    //            NSLog(@"cost time 2= %f", deltaTime);
                NSString *code = [responseObject objectForKey:@"code"];
                if([code isEqualToString:@"Ok"] || [code isEqualToString:@"Pass"]) {
                    _mouthChecked = YES;
                    NSLog(@"人脸检测通过");
                    if ([chain isLast]) {
                        NSLog(@"人脸检测完成");
                        _tipsLabel.text = @"人脸检测完成";
                    } else {
                        [self showNextTips:chain];
                        [self performSelector:@selector(startSavePhoto) withObject:nil afterDelay:1];
                    }
                } else {                
                    _tipsLabel.text = @"请再次，张开嘴巴再合上";
                    [_mouthPhotos removeAllObjects];
                    [FaceManager shareManager].savePhoto = YES;
                }
            }
        }];
        
    }
}

- (void)showNextTips: (InterceptChain*) chain {
    [[chain getNextInterceptor] procceedBegin];
}

- (void)procceedBegin {
    NSLog(@"张嘴检测开始");
    _tipsLabel.text = @"请张开嘴巴再合上";
}

- (void)startSavePhoto{
    [FaceManager shareManager].savePhoto = YES;
}
@end
