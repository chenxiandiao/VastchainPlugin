//
//  InterceptChain.h
//  VastchainPlugin_Example
//
//  Created by cxd on 2021/10/13.
//  Copyright © 2021 chenxiandiao. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Interceptor.h"



@interface InterceptChain : NSObject



@property NSArray *interceptors;
@property NSInteger index;
- (id) init:(NSArray *)intercepotrs index: (NSInteger)index;
- (void) procced: (NSString *) file;
- (BOOL) isLast;
- (Interceptor *) getNextInterceptor;
- (NSString*) currentType;
@end




