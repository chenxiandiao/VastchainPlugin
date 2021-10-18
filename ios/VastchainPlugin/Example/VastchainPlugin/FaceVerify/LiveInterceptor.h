//
//  LiveInterceptor.h
//  VastchainPlugin_Example
//
//  Created by cxd on 2021/10/13.
//  Copyright © 2021 chenxiandiao. All rights reserved.
//

#import "Interceptor.h"

NS_ASSUME_NONNULL_BEGIN

@interface LiveInterceptor : Interceptor

-(void) checkLive: (NSArray *)photos type:(NSString *)type requestId: (NSString *)requestId completionHandler:(void (^)(NSURLResponse *response, id responseObject, NSError *error))completionHandler;

@end

NS_ASSUME_NONNULL_END
