//
//  FaceManager.h
//  VastchainPlugin_Example
//
//  Created by cxd on 2021/10/15.
//  Copyright © 2021 chenxiandiao. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface FaceManager : NSObject

+ (instancetype)shareManager;

@property BOOL savePhoto;
@property BOOL skipFaceCheck;

@end

