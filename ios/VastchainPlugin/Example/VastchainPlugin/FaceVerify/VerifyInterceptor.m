//
//  VerfiyInterceptor.m
//  VastchainPlugin_Example
//
//  Created by cxd on 2021/10/13.
//  Copyright © 2021 chenxiandiao. All rights reserved.
//

#import "VerifyInterceptor.h"
#import "AFNetworking.h"
#import "Api.h"
#import "FaceManager.h"
#import <AVFoundation/AVFoundation.h>
@implementation VerifyInterceptor

-(id)init
{
    if (self = [super init]) {
        _compareCount = 20;
    }
    return self;
}

-(id) initWithRequestId:(NSString *)requestId label:(UILabel *)tipsLabel{
    self = [super init];
    if (self) {
        _requestId = requestId;
        _compareCount = 20;
        _tipsLabel = tipsLabel;
    }
    return self;
}

- (void)procceed:(NSString *)file chain:(InterceptChain*)chain {
    if(self.checked) {
        [chain procced:file];
        return;
    }
    
    if([FaceManager shareManager].skipFaceCheck) {
        [FaceManager shareManager].savePhoto = YES;
        _verifySuccess = YES;
        [self showNextTips:chain];
        return;
    }
    
    [FaceManager shareManager].savePhoto = NO;
    
    [self faceComapre:file requestId:_requestId completionHandler:^(NSURLResponse * _Nonnull response, id  _Nonnull responseObject, NSError * _Nonnull error) {
        if (error) {
            NSLog(@"%@", error);
            _count = _count + 1;
            if(_count > _compareCount) {
                NSLog(@"人脸比对失败");
            } else {
                NSLog(@"尝试继续比对");
                [FaceManager shareManager].savePhoto = YES;
            }
        } else {
            NSString *code = [responseObject objectForKey:@"code"];
            NSLog(@"%@ %@", response, responseObject);
            NSLog(@"%@", code);
            NSLog(@"%@", [responseObject objectForKey:@"msg"]);
            if ([code isEqualToString:@"Ok"] || [code isEqualToString:@"Pass"]) {
                _verifySuccess = YES;
                if ([chain isLast]) {
                    NSLog(@"人脸检测完成");
                    _tipsLabel.text = @"人脸检测完成";
                } else {
                    [self showNextTips:chain];
                    [FaceManager shareManager].savePhoto = YES;
                }
                NSLog(@"人脸比对完成");
            } else {
                _count = _count + 1;
                if(_count > _compareCount) {
                    NSLog(@"人脸比对失败");
                } else {
                    [FaceManager shareManager].savePhoto = YES;
                    NSLog(@"尝试继续比对");
                }
            }
        }
    }];
}

- (void)faceComapre:(NSString *)file requestId:(NSString *)requestId completionHandler:(void (^)(NSURLResponse * _Nonnull, id _Nonnull, NSError * _Nonnull))completionHandler {
    
    NSString *verifyCompare = [NSString stringWithFormat:@"%@%@" , SERVER_URL,FACE_COMPARE];
    NSLog(@"%@", @"开始人脸比对");
    NSData *requestIdData =[_requestId dataUsingEncoding:NSUTF8StringEncoding];
    
    
    NSMutableURLRequest *request = [[AFHTTPRequestSerializer serializer] multipartFormRequestWithMethod:@"POST" URLString:verifyCompare parameters:nil constructingBodyWithBlock:^(id<AFMultipartFormData> formData) {
        [formData appendPartWithFormData:requestIdData name:@"request_id"];
        [formData appendPartWithFileURL:[NSURL fileURLWithPath:file] name:@"face_img" fileName:@"face_image.jpg" mimeType:@"image/jpeg" error:nil];
    } error:nil];
    
    AFURLSessionManager *manager = [[AFURLSessionManager alloc] initWithSessionConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration]];
    
    NSURLSessionUploadTask *uploadTask;
    uploadTask = [manager
                  uploadTaskWithStreamedRequest:request
                  progress:nil
                  completionHandler:^(NSURLResponse * _Nonnull response, id  _Nullable responseObject, NSError * _Nullable error) {
        completionHandler(response, responseObject, error);
    }];
    
    [uploadTask resume];
}

- (void)showNextTips: (InterceptChain*) chain {
    [[chain getNextInterceptor] procceedBegin];
}

- (BOOL)checked {
    return _verifySuccess;
}
@end

