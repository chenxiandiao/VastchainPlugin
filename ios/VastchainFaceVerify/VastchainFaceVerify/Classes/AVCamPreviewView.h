/*
See LICENSE folder for this sample’s licensing information.

Abstract:
The camera preview view that displays the capture output.
*/

#import <AVFoundation/AVFoundation.h>

@class AVCaptureSession;

@interface AVCamPreviewView : UIView

@property (nonatomic, readonly) AVCaptureVideoPreviewLayer *videoPreviewLayer;

@property (nonatomic) AVCaptureSession *session;

@end