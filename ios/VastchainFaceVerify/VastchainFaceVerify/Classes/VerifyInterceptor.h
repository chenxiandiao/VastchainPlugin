//
//  VerfiyInterceptor.h
//  VastchainPlugin_Example
//
//  Created by cxd on 2021/10/13.
//  Copyright © 2021 chenxiandiao. All rights reserved.
//

#import "Interceptor.h"
#import "InterceptChain.h"

NS_ASSUME_NONNULL_BEGIN

#define COMPARE_COUNT 10

@class InterceptChain;

@interface VerifyInterceptor : Interceptor


@property BOOL verifySuccess;
@property NSInteger tryCount;
@property NSInteger compareCount;
@property NSString *requestId;
@property UILabel *tipsLabel;

-(id) initWithRequestId:(NSString *)requestId label:(UILabel *)tipsLabel;

-(void) faceComapre: (NSString *)file
          requestId: (NSString *)requestId
  completionHandler:(void (^)(NSURLResponse *response, id responseObject, NSError *error))completionHandler;


@end

NS_ASSUME_NONNULL_END
