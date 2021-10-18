//
//  FaceManager.m
//  VastchainPlugin_Example
//
//  Created by cxd on 2021/10/15.
//  Copyright © 2021 chenxiandiao. All rights reserved.
//

#import "FaceManager.h"

@implementation FaceManager


static FaceManager *manager = nil;

/*
 创建单利,单利的唯一性
 */
+ (instancetype)shareManager{

    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        manager = [[FaceManager alloc]init];
        manager.skipFaceCheck = NO;
    });
    return manager;
}


/*
 覆盖该方法主要确保 alloc init方法创建对象的唯一性
 */
+ (instancetype)allocWithZone:(struct _NSZone *)zone{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        manager = [super allocWithZone:zone];
    });
    return manager;
}

/*
 确保通过copy产生对象的唯一性
 */
- (id)copy{
    return self;
}

/*
 确保通过mutableCopy产生对象的唯一性
 */
- (id)mutableCopy{
    return self;
}
@end

