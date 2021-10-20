#ifdef __OBJC__
#import <UIKit/UIKit.h>
#else
#ifndef FOUNDATION_EXPORT
#if defined(__cplusplus)
#define FOUNDATION_EXPORT extern "C"
#else
#define FOUNDATION_EXPORT extern
#endif
#endif
#endif

#import "Api.h"
#import "AVCamPreviewView.h"
#import "EyeInterceptor.h"
#import "FaceIndexViewController.h"
#import "FaceManager.h"
#import "FaceViewController.h"
#import "IdentityViewController.h"
#import "InterceptChain.h"
#import "Interceptor.h"
#import "LiveInterceptor.h"
#import "MouthInterceptor.h"
#import "VerifyInterceptor.h"

FOUNDATION_EXPORT double VastchainFaceVerifyVersionNumber;
FOUNDATION_EXPORT const unsigned char VastchainFaceVerifyVersionString[];

