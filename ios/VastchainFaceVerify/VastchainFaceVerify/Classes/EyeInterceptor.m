//
//  MouthInterceptor.m
//  VastchainPlugin_Example
//
//  Created by cxd on 2021/10/14.
//  Copyright © 2021 chenxiandiao. All rights reserved.
//

#import "EyeInterceptor.h"
#import "FaceManager.h"

@implementation EyeInterceptor




- (id)initWithRequestId:(NSString *)requestId label:(UILabel *)tipsLabel{
    self = [super init];
    if (self) {
        _requestId = requestId;
        _eyePhotos = [[NSMutableArray alloc]initWithCapacity:20];
        _tipsLabel = tipsLabel;
    }
    return self;
}

- (BOOL)checked {
    return _eyeChecked;
}

- (void)procceed:(NSString *)file chain:(InterceptChain *)chain {
    
    if (self.checked) {
        [chain procced:file];
        return;
    }
    
    if (_eyePhotos.count == 0) {
        NSLog(@"开始保存张嘴图片");
        [[FaceManager shareManager] playSoundName:@"eye.wav"];
    }
   
    [_eyePhotos addObject:file];
    if (_eyePhotos.count == PHOTO_COUNT) {
        NSLog(@"结束保存张嘴图片");
        [FaceManager shareManager].savePhoto = NO;
        
        if([FaceManager shareManager].skipFaceCheck) {
            _eyeChecked = YES;
            if ([chain isLast]) {
                NSLog(@"人脸检测完成");
                _tipsLabel.text = @"人脸检测完成";
            } else {
                [FaceManager shareManager].savePhoto = YES;
                [self showNextTips:chain];
            }
            return;
        }
        
        [super checkLive:_eyePhotos type:@"eye" requestId:_requestId completionHandler:^(NSURLResponse * _Nonnull response, id  _Nonnull responseObject, NSError * _Nonnull error) {
            if (error) {
                NSLog(@"Error: %@", error);
                self->_tryCount++;
                if(self->_tryCount > COMPARE_COUNT) {
                    NSLog(@"人脸比对失败");
                    self->_tipsLabel.text = @"人脸识别失败,请稍后重试";
                } else {
                    NSLog(@"尝试继续比对");
                    [FaceManager shareManager].savePhoto = YES;
                }
            } else {
                NSLog(@"%@ %@", response, responseObject);
                NSLog(@"%@", [responseObject objectForKey:@"code"]);
                NSLog(@"%@", [responseObject objectForKey:@"msg"]);

                NSString *code = [responseObject objectForKey:@"code"];
                if([code isEqualToString:@"Ok"] || [code isEqualToString:@"Pass"]) {
                    self->_eyeChecked = YES;
                    NSLog(@"人脸检测通过");
                    if ([chain isLast]) {
                        NSLog(@"人脸检测完成");
                        self->_tipsLabel.text = @"人脸检测完成";
                    } else {                        
                        [self showNextTips:chain];
                        [self performSelector:@selector(startSavePhoto) withObject:nil afterDelay:1];
                    }
                } else {
                    if (self->_tryCount <= COMPARE_COUNT) {
                        self->_tipsLabel.text = @"请再次，闭眼后缓慢睁开";
                        [self->_eyePhotos removeAllObjects];
                        [FaceManager shareManager].savePhoto = YES;
                    } else {
                        self->_tipsLabel.text = @"人脸识别失败,请稍后重试";
                    }
                  
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
    _tipsLabel.text = @"请闭眼后再缓慢睁开";
}

- (void)startSavePhoto{
    [FaceManager shareManager].savePhoto = YES;
}

@end
