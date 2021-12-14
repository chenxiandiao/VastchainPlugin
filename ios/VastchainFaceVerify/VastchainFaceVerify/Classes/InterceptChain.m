//
//  InterceptChain.m
//  VastchainPlugin_Example
//
//  Created by cxd on 2021/10/13.
//  Copyright © 2021 chenxiandiao. All rights reserved.
//

#import "InterceptChain.h"
#import "VerifyInterceptor.h"
#import "EyeInterceptor.h"
#import "MouthInterceptor.h"

@implementation InterceptChain

- (id) init:(NSArray *)intercepotrs index: (NSInteger)index {
    self = [super init];
    self.interceptors = intercepotrs;
    self.index = index;
    return self;
}

- (void)procced:(NSString *)file {
    if (self.index >= self.interceptors.count) {
        return;
    }
    NSLog(@"procced");
    InterceptChain *next = [[InterceptChain alloc]init:self.interceptors index:self.index+1];
    Interceptor* interceptor = self.interceptors[self.index];
    [interceptor procceed:file chain:next];
}

- (NSString *)currentType {
    for (Interceptor *interceptor in _interceptors) {
        if (![interceptor checked]) {
            if ([interceptor isKindOfClass:[VerifyInterceptor class]]) {
                return @"compare";
            } else if ([interceptor isKindOfClass:[EyeInterceptor class]]) {
                return @"eye";
            } else if ([interceptor isKindOfClass:[MouthInterceptor class]]) {
                return @"mouth";
            }
            return @"compare";
        }
    }
    return @"compare";
}


- (BOOL)isLast {
    if (self.index>=self.interceptors.count) {
        return YES;
    }
    return NO;
}

- (Interceptor *)getNextInterceptor {
    if (self.index>=self.interceptors.count) {
        return nil;
    }
    return self.interceptors[self.index];
}

@end
