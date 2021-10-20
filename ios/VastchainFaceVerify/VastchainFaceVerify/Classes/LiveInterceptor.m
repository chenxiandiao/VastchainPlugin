//
//  LiveInterceptor.m
//  VastchainPlugin_Example
//
//  Created by cxd on 2021/10/13.
//  Copyright © 2021 chenxiandiao. All rights reserved.
//

#import "LiveInterceptor.h"
#import "Api.h"
#import "AFNetworking.h"

@implementation LiveInterceptor

- (void)checkLive:(NSArray *)photos type:(NSString *)type requestId:(NSString *)requestId completionHandler:(void (^)(NSURLResponse *response, id responseObject, NSError *error))completionHandler {
    
    NSLog(@"开始活体检测");
    
    NSString *verifyCompare = [NSString stringWithFormat:@"%@%@" , SERVER_URL,LIVE_CHECK];
    NSData *requestIdData =[requestId dataUsingEncoding:NSUTF8StringEncoding];
    NSData *typeData = [type dataUsingEncoding:NSUTF8StringEncoding];

    NSMutableURLRequest *request = [[AFHTTPRequestSerializer serializer] multipartFormRequestWithMethod:@"POST" URLString:verifyCompare parameters:nil constructingBodyWithBlock:^(id<AFMultipartFormData> formData) {
        [formData appendPartWithFormData:requestIdData name:@"request_id"];
        [formData appendPartWithFormData:typeData name:@"type"];
        int i = 0;
        for (NSString *filePath in photos) {
            i++;
            [formData appendPartWithFileURL:[NSURL fileURLWithPath:filePath] name:@"face_imgs" fileName:[NSString stringWithFormat: @"face_image_[%d].jpg", i] mimeType:@"image/jpeg" error:nil];
        }
    } error:nil];



    AFURLSessionManager *manager = [[AFURLSessionManager alloc] initWithSessionConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration]];

    NSURLSessionUploadTask *uploadTask;
    uploadTask = [manager
                  uploadTaskWithStreamedRequest:request
                  progress:^(NSProgress * _Nonnull uploadProgress) {
        // This is not called back on the main queue.
        // You are responsible for dispatching to the main queue for UI updates
        dispatch_async(dispatch_get_main_queue(), ^{
            //Update the progress view
        });
    }
                  completionHandler:^(NSURLResponse * _Nonnull response, id  _Nullable responseObject, NSError * _Nullable error) {
        
        completionHandler(response, responseObject, error);
    }];

    [uploadTask resume];
}
@end
