//
//  FaceViewController.m
//  VastchainPlugin_Example
//
//  Created by cxd on 2021/9/30.
//  Copyright © 2021 chenxiandiao. All rights reserved.
//

#import "FaceViewController.h"
#import "AFNetworking.h"
#import "AVCamPreviewView.h"
#import "Api.h"
#import <AVFoundation/AVFoundation.h>
#import "InterceptChain.h"
#import "VerifyInterceptor.h"
#import "EyeInterceptor.h"
#import "MouthInterceptor.h"
#import "FaceManager.h"

typedef NS_ENUM(NSInteger, AVCamSetupResult) {
    AVCamSetupResultSuccess,
    AVCamSetupResultCameraNotAuthorized,
    AVCamSetupResultSessionConfigurationFailed
};

typedef NS_ENUM(NSInteger, AVCamLivePhotoMode) {
    AVCamLivePhotoModeOn,
    AVCamLivePhotoModeOff
};


@interface FaceViewController ()<AVCaptureMetadataOutputObjectsDelegate,AVCaptureVideoDataOutputSampleBufferDelegate>
@property (nonatomic, weak) IBOutlet AVCamPreviewView* previewView;

@property (nonatomic) AVCaptureSession* session;
@property (nonatomic) dispatch_queue_t sessionQueue;
@property (nonatomic) AVCamSetupResult setupResult;
@property (nonatomic) AVCaptureDeviceInput* videoDeviceInput;
@property (nonatomic) AVCapturePhotoOutput* photoOutput;
@property (nonatomic) NSArray<AVSemanticSegmentationMatteType>* selectedSemanticSegmentationMatteTypes;
@property (nonatomic, strong) AVCaptureMetadataOutput *metadataOutput;
@property (nonatomic, strong) AVCaptureVideoDataOutput *videoDataOutput;
@property (weak, nonatomic) IBOutlet UIImageView *topImageView;
@property (weak, nonatomic) IBOutlet UIImageView *leftImageView;
@property (weak, nonatomic) IBOutlet UIImageView *rightImageView;
@property (weak, nonatomic) IBOutlet UIImageView *bottomImageView;
@property (weak, nonatomic) IBOutlet UIImageView *circleImageView;
@property (weak, nonatomic) IBOutlet UILabel *tipsLabel;

@property InterceptChain *interceptChain;

@end

@implementation FaceViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    //    [self.view setBackgroundColor: [UIColor redColor]];
    self.mouthPhotos = [NSMutableArray arrayWithCapacity:20];
    [self initCamera];
    [self clearDictonory: @"compare"];
    [self clearDictonory: @"mouth"];
    [self clearDictonory: @"eye"];
    
    UIImage *img = [self createImageWithColor:[UIColor colorWithRed:0.0f green:0.0f blue:0.0f alpha:0.56f]];
    if (img == nil) {
        NSLog(@"读取失败");
    }
    
    [self.topImageView setImage:img];
    [self.bottomImageView setImage:img];
    [self.leftImageView setImage:img];
    [self.rightImageView setImage:img];
    
    
    UIImage *circleImage = [UIImage imageNamed:@"FaceCircle"];
    [self.circleImageView setImage:circleImage];
    
    self.tipsLabel.text = @"请正对人脸框";
    [self getSessionId];
}

- (UIImage*) createImageWithColor: (UIColor*) color

{
    CGRect rect=CGRectMake(0.0f, 0.0f, 1.0f, 1.0f);
    
    UIGraphicsBeginImageContext(rect.size);
    
    CGContextRef context = UIGraphicsGetCurrentContext();
    
    CGContextSetFillColorWithColor(context, [color CGColor]);
    
    CGContextFillRect(context, rect);
    
    UIImage *theImage = UIGraphicsGetImageFromCurrentImageContext();
    
    UIGraphicsEndImageContext();
    
    return theImage;
    
}

- (void) clearDictonory:(NSString*) prefix {
    NSString *dictonory = [NSString stringWithFormat:@"%@%@", NSHomeDirectory(), [NSString stringWithFormat: @"/Documents/%@", prefix]];
    NSError *error;
    NSLog(@"%@", dictonory);
    
    
    NSArray* tempArray = [NSFileManager.defaultManager contentsOfDirectoryAtPath:dictonory error:nil];
    
    for (NSString* fileName in tempArray) {
        
        BOOL flag = NO;
        
        NSString* fullPath = [dictonory stringByAppendingPathComponent:fileName];
        
        if ([NSFileManager.defaultManager fileExistsAtPath:fullPath isDirectory:&flag]) {
//            NSLog(@"%@", fullPath);
            BOOL result = [NSFileManager.defaultManager removeItemAtPath:fullPath error:&error];
            //            if (error != nil) {
            //                NSLog(@"删除文件出错");
            //                NSLog(@"%@", [error localizedDescription]);
            //            }
            //            if (result) {
            //                NSLog(@"删除文件成功");
            //            } else {
            //                NSLog(@"删除文件出错");
            //            }
        }
    }
    
    //    if([NSFileManager.defaultManager fileExistsAtPath:dictonory]) {
    //        NSLog(@"文件夹存在");
    //    } else {
    //        NSLog(@"文件夹不存在");
    //    }
}

- (void) getSessionId {
    
    if ([FaceManager shareManager].skipFaceCheck) {
        [FaceManager shareManager].savePhoto = YES;
        NSArray *interceptors = [[NSArray  alloc]initWithObjects:
                                 [[VerifyInterceptor alloc]initWithRequestId:_requestId label:_tipsLabel],
                                 [[EyeInterceptor  alloc]initWithRequestId:_requestId label:_tipsLabel],
                                 [[MouthInterceptor alloc]initWithRequestId:_requestId label:_tipsLabel],
                                 nil];
        self.interceptChain = [[InterceptChain alloc]init:interceptors index:0];
        return;
    }
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    manager.securityPolicy.allowInvalidCertificates = YES;
    manager.securityPolicy.validatesDomainName = NO;
    NSString *getSessionURL = [NSString stringWithFormat:@"%@%@" , SERVER_URL,GET_SESSION_ID];
    NSLog(@"%@", getSessionURL);
    NSString *name = @"陈贤雕";
    //    NSString *idCard = @"330327199203162872";
    NSString *idCard = @"001";
    NSString *appid = @"AFA72CFB6FED0343F81FC94BB3D3FFC3";
    //拼接get访问URL请求参数
    NSDictionary *dict = @{@"app_id" : appid,
                           @"id_card"  : idCard,
                           @"user_name" : name};
    [manager GET: getSessionURL parameters:dict headers:nil progress:nil success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        NSLog(@"获取session成功");
        _requestId = [responseObject objectForKey:@"request_id"];
        NSLog(@"%@", _requestId);
        
        [FaceManager shareManager].savePhoto = YES;
        NSMutableArray *interceptors = [[NSMutableArray alloc]init];
        [interceptors addObject:[[VerifyInterceptor alloc]initWithRequestId:_requestId label:_tipsLabel]];
        if(_needEyeCheck) {
            [interceptors addObject:[[EyeInterceptor  alloc]initWithRequestId:_requestId label:_tipsLabel]];
        }
        if(_needMouthCheck) {
            [interceptors addObject:[[MouthInterceptor  alloc]initWithRequestId:_requestId label:_tipsLabel]];
        }
        
//        NSArray *interceptors = [[NSArray  alloc]initWithObjects:
//                                 [[VerifyInterceptor alloc]initWithRequestId:_requestId],
//                                 [[EyeInterceptor  alloc]initWithRequestId:_requestId label:_tipsLabel],
//                                 [[MouthInterceptor alloc]initWithRequestId:_requestId label:_tipsLabel],
//                                 nil];
        
        self.interceptChain = [[InterceptChain alloc]init:interceptors index:0];
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        [self.navigationController popViewControllerAnimated:true];
        NSLog(@"获取session失败");
    }];
}

- (NSString*) saveImage: (UIImage *)image filePrefix:(NSString*) filePrefix{
    
    NSString *directory = [NSString stringWithFormat:@"%@%@", NSHomeDirectory(), [NSString stringWithFormat: @"/Documents/%@", filePrefix]];
    NSError *error;
    if([NSFileManager.defaultManager fileExistsAtPath:directory] == NO) {
        [NSFileManager.defaultManager createDirectoryAtPath:directory withIntermediateDirectories:YES attributes:nil error:&error];
        NSLog(@"saveImage 文件夹重新创建");
    } else {
        //        NSLog(@"saveImage 文件夹已存在");
    }
    if(error != nil) {
        NSLog(@"创建文件夹失败");
    }
    
    NSDate *senddate = [NSDate date];
    
    NSString *fileName = [NSString stringWithFormat:@"%f",[senddate timeIntervalSince1970]];
    
    NSString *filePath = [NSString stringWithFormat:@"%@/%@.jpg", directory, fileName];
    
    //    NSURL *url = [NSURL fileURLWithPath:filepath];
    BOOL result = [UIImageJPEGRepresentation(image, 0.01) writeToFile:filePath atomically:YES];
    if (result) {
        return filePath;
    } else {
        return @"";
    }
}


- (void)verifyCompare {
    NSString *verifyCompare = [NSString stringWithFormat:@"%@%@" , SERVER_URL,FACE_COMPARE];
    NSLog(@"%@", @"开始人脸比对");
    NSString *requestId = self.requestId;
    NSData *requestIdData =[requestId dataUsingEncoding:NSUTF8StringEncoding];
    
    
    NSMutableURLRequest *request = [[AFHTTPRequestSerializer serializer] multipartFormRequestWithMethod:@"POST" URLString:verifyCompare parameters:nil constructingBodyWithBlock:^(id<AFMultipartFormData> formData) {
        [formData appendPartWithFormData:requestIdData name:@"request_id"];
        [formData appendPartWithFileURL:[NSURL fileURLWithPath:self.comparePhoto] name:@"face_img" fileName:@"face_image.jpg" mimeType:@"image/jpeg" error:nil];
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
        if (error) {
            NSLog(@"Error: %@", error);
            self.savePhoto = YES;
        } else {
            NSLog(@"%@ %@", response, responseObject);
            NSLog(@"%@", [responseObject objectForKey:@"code"]);
            NSLog(@"%@", [responseObject objectForKey:@"msg"]);
            
            self.savePhoto = YES;
            self.faceCompareSuccess = YES;
        }
    }];
    
    [uploadTask resume];
}

- (void) checkMouth {
    
    self.savePhoto = NO;
    
    //    NSDate* tmpStartData = [NSDate date];
    double deltaTime = [[NSDate date] timeIntervalSinceDate:self.tmpStartData];
    NSLog(@"cost time 1 = %f", deltaTime);
    
    NSString *verifyCompare = [NSString stringWithFormat:@"%@%@" , SERVER_URL,LIVE_CHECK];
    NSString *requestId = self.requestId;
    NSData *requestIdData =[requestId dataUsingEncoding:NSUTF8StringEncoding];
    NSData *type = [@"mouth" dataUsingEncoding:NSUTF8StringEncoding];
    
    NSMutableURLRequest *request = [[AFHTTPRequestSerializer serializer] multipartFormRequestWithMethod:@"POST" URLString:verifyCompare parameters:nil constructingBodyWithBlock:^(id<AFMultipartFormData> formData) {
        [formData appendPartWithFormData:requestIdData name:@"request_id"];
        [formData appendPartWithFormData:type name:@"type"];
        int i = 0;
        for (NSString *filePath in self.mouthPhotos) {
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
        if (error) {
            NSLog(@"Error: %@", error);
        } else {
            NSLog(@"%@ %@", response, responseObject);
            NSLog(@"%@", [responseObject objectForKey:@"code"]);
            NSLog(@"%@", [responseObject objectForKey:@"msg"]);
            
            double deltaTime = [[NSDate date] timeIntervalSinceDate:self.tmpStartData];
            NSLog(@"cost time 2= %f", deltaTime);
            NSString *code = [responseObject objectForKey:@"code"];
            if([code isEqualToString:@"Ok"] || [code isEqualToString:@"Pass"]) {
                NSLog(@"人脸检测通过");
                self.tipsLabel.text = @"人脸检测通过";
                self.mouthChecked = YES;
            } else {
                self.savePhoto = YES;
            }
        }
    }];
    
    [uploadTask resume];
}


- (void) initCamera {
    // Create the AVCaptureSession.
    self.session = [[AVCaptureSession alloc] init];
    // Set up the preview view.
    self.previewView.session = self.session;
    
    // Communicate with the session and other session objects on this queue.
    self.sessionQueue = dispatch_queue_create("session queue", DISPATCH_QUEUE_SERIAL);
    
    self.setupResult = AVCamSetupResultSuccess;
    
    switch ([AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeVideo])
    {
        case AVAuthorizationStatusAuthorized:
        {
            // The user has previously granted access to the camera.
            NSLog(@"已允许摄像头权限");
            break;
        }
        case AVAuthorizationStatusNotDetermined:
        {
            /*
             The user has not yet been presented with the option to grant
             video access. We suspend the session queue to delay session
             setup until the access request has completed.
             
             Note that audio access will be implicitly requested when we
             create an AVCaptureDeviceInput for audio during session setup.
             */
            dispatch_suspend(self.sessionQueue);
            [AVCaptureDevice requestAccessForMediaType:AVMediaTypeVideo completionHandler:^(BOOL granted) {
                if (!granted) {
                    self.setupResult = AVCamSetupResultCameraNotAuthorized;
                }
                dispatch_resume(self.sessionQueue);
            }];
            break;
        }
        default:
        {
            // The user has previously denied access.
            self.setupResult = AVCamSetupResultCameraNotAuthorized;
            break;
        }
    }
    
    dispatch_async(self.sessionQueue, ^{
        [self configureSession];
    });
    
    //    [self.session addOutput:self.metadataOutput];
    [self.session addOutput:self.videoDataOutput];
}

- (void) viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    dispatch_async(self.sessionQueue, ^{
        switch (self.setupResult)
        {
            case AVCamSetupResultSuccess:
            {
                NSLog(@"打开摄像头");
                //                [self addObservers];
                [self.session startRunning];
                //                self.sessionRunning = self.session.isRunning;
                break;
            }
            case AVCamSetupResultCameraNotAuthorized:
            {
                dispatch_async(dispatch_get_main_queue(), ^{
                    NSString* message = NSLocalizedString(@"AVCam doesn't have permission to use the camera, please change privacy settings", @"Alert message when the user has denied access to the camera");
                    UIAlertController* alertController = [UIAlertController alertControllerWithTitle:@"AVCam" message:message preferredStyle:UIAlertControllerStyleAlert];
                    UIAlertAction* cancelAction = [UIAlertAction actionWithTitle:NSLocalizedString(@"OK", @"Alert OK button") style:UIAlertActionStyleCancel handler:nil];
                    [alertController addAction:cancelAction];
                    // Provide quick access to Settings.
                    UIAlertAction* settingsAction = [UIAlertAction actionWithTitle:NSLocalizedString(@"Settings", @"Alert button to open Settings") style:UIAlertActionStyleDefault handler:^(UIAlertAction* action) {
                        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:UIApplicationOpenSettingsURLString] options:@{} completionHandler:nil];
                    }];
                    [alertController addAction:settingsAction];
                    [self presentViewController:alertController animated:YES completion:nil];
                });
                break;
            }
            case AVCamSetupResultSessionConfigurationFailed:
            {
                dispatch_async(dispatch_get_main_queue(), ^{
                    NSString* message = NSLocalizedString(@"Unable to capture media", @"Alert message when something goes wrong during capture session configuration");
                    UIAlertController* alertController = [UIAlertController alertControllerWithTitle:@"AVCam" message:message preferredStyle:UIAlertControllerStyleAlert];
                    UIAlertAction* cancelAction = [UIAlertAction actionWithTitle:NSLocalizedString(@"OK", @"Alert OK button") style:UIAlertActionStyleCancel handler:nil];
                    [alertController addAction:cancelAction];
                    [self presentViewController:alertController animated:YES completion:nil];
                });
                break;
            }
        }
    });
}

- (void) viewDidDisappear:(BOOL)animated
{
    dispatch_async(self.sessionQueue, ^{
        if (self.setupResult == AVCamSetupResultSuccess) {
            [self.session stopRunning];
            //            [self removeObservers];
        }
    });
    
    [super viewDidDisappear:animated];
}

- (void) configureSession
{
    if (self.setupResult != AVCamSetupResultSuccess) {
        return;
    }
    
    NSError* error = nil;
    
    [self.session beginConfiguration];
    
    /*
     We do not create an AVCaptureMovieFileOutput when setting up the session because
     Live Photo is not supported when AVCaptureMovieFileOutput is added to the session.
     */
    self.session.sessionPreset = AVCaptureSessionPreset640x480;
    
    // Add video input.
    
    // Choose the back dual camera if available, otherwise default to a wide angle camera.
    AVCaptureDevice* videoDevice = [AVCaptureDevice defaultDeviceWithDeviceType:AVCaptureDeviceTypeBuiltInDualCamera mediaType:AVMediaTypeVideo position:AVCaptureDevicePositionFront];
    if (!videoDevice) {
        // If a rear dual camera is not available, default to the rear dual wide angle camera.
        if (@available(iOS 13.0, *)) {
            videoDevice = [AVCaptureDevice defaultDeviceWithDeviceType:AVCaptureDeviceTypeBuiltInDualWideCamera mediaType:AVMediaTypeVideo position:AVCaptureDevicePositionFront];
        } else {
            // Fallback on earlier versions
        }
    }
    if (!videoDevice) {
        // If a rear dual wide camera is not available, default to the rear wide angle camera.
        videoDevice = [AVCaptureDevice defaultDeviceWithDeviceType:AVCaptureDeviceTypeBuiltInWideAngleCamera mediaType:AVMediaTypeVideo position:AVCaptureDevicePositionFront];
    }
    if (!videoDevice) {
        // If a rear wide angle camera is not available, default to the front wide angle camera.
        videoDevice = [AVCaptureDevice defaultDeviceWithDeviceType:AVCaptureDeviceTypeBuiltInWideAngleCamera mediaType:AVMediaTypeVideo position:AVCaptureDevicePositionFront];
    }
    AVCaptureDeviceInput* videoDeviceInput = [AVCaptureDeviceInput deviceInputWithDevice:videoDevice error:&error];
    if (!videoDeviceInput) {
        NSLog(@"Could not create video device input: %@", error);
        self.setupResult = AVCamSetupResultSessionConfigurationFailed;
        [self.session commitConfiguration];
        return;
    }
    if ([self.session canAddInput:videoDeviceInput]) {
        [self.session addInput:videoDeviceInput];
        self.videoDeviceInput = videoDeviceInput;
        
        dispatch_async(dispatch_get_main_queue(), ^{
            /*
             Dispatch video streaming to the main queue because AVCaptureVideoPreviewLayer is the backing layer for PreviewView.
             You can manipulate UIView only on the main thread.
             Note: As an exception to the above rule, it is not necessary to serialize video orientation changes
             on the AVCaptureVideoPreviewLayer’s connection with other session manipulation.
             
             Use the status bar orientation as the initial video orientation. Subsequent orientation changes are
             handled by CameraViewController.viewWillTransition(to:with:).
             */
            AVCaptureVideoOrientation initialVideoOrientation = AVCaptureVideoOrientationPortrait;
            if (self.windowOrientation != UIInterfaceOrientationUnknown) {
                initialVideoOrientation = (AVCaptureVideoOrientation)self.windowOrientation;
            }
            
            self.previewView.videoPreviewLayer.connection.videoOrientation = initialVideoOrientation;
            self.previewView.videoPreviewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill;
        });
    }
    else {
        NSLog(@"Could not add video device input to the session");
        self.setupResult = AVCamSetupResultSessionConfigurationFailed;
        [self.session commitConfiguration];
        return;
    }
    
    // Add photo output.
    //    AVCapturePhotoOutput* photoOutput = [[AVCapturePhotoOutput alloc] init];
    //    if ([self.session canAddOutput:photoOutput]) {
    //        [self.session addOutput:photoOutput];
    //        self.photoOutput = photoOutput;
    //
    //        self.photoOutput.highResolutionCaptureEnabled = YES;
    //        self.photoOutput.livePhotoCaptureEnabled = self.photoOutput.livePhotoCaptureSupported;
    //        self.photoOutput.depthDataDeliveryEnabled = self.photoOutput.depthDataDeliverySupported;
    //        self.photoOutput.portraitEffectsMatteDeliveryEnabled = self.photoOutput.portraitEffectsMatteDeliverySupported;
    //        self.photoOutput.enabledSemanticSegmentationMatteTypes = self.photoOutput.availableSemanticSegmentationMatteTypes;
    //        self.selectedSemanticSegmentationMatteTypes = self.photoOutput.enabledSemanticSegmentationMatteTypes;
    //        self.photoOutput.maxPhotoQualityPrioritization = AVCapturePhotoQualityPrioritizationQuality;
    //
    //        self.livePhotoMode = self.photoOutput.livePhotoCaptureSupported ? AVCamLivePhotoModeOn : AVCamLivePhotoModeOff;
    //        self.depthDataDeliveryMode = self.photoOutput.depthDataDeliverySupported ? AVCamDepthDataDeliveryModeOn : AVCamDepthDataDeliveryModeOff;
    //        self.portraitEffectsMatteDeliveryMode = self.photoOutput.portraitEffectsMatteDeliverySupported ? AVCamPortraitEffectsMatteDeliveryModeOn : AVCamPortraitEffectsMatteDeliveryModeOff;
    //        self.photoQualityPrioritizationMode = AVCapturePhotoQualityPrioritizationBalanced;
    //
    //        self.inProgressPhotoCaptureDelegates = [NSMutableDictionary dictionary];
    //        self.inProgressLivePhotoCapturesCount = 0;
    //    }
    //    else {
    //        NSLog(@"Could not add photo output to the session");
    //        self.setupResult = AVCamSetupResultSessionConfigurationFailed;
    //        [self.session commitConfiguration];
    //        return;
    //    }
    
    //    self.backgroundRecordingID = UIBackgroundTaskInvalid;
    //    self.selectedMovieMode10BitDeviceFormat = nil;
    
    [self.session commitConfiguration];
}


- (NSString *)canSetSessionPreset:(AVCaptureDevice*) device {
    if ([device supportsAVCaptureSessionPreset:AVCaptureSessionPreset3840x2160]) {
        NSLog(@"AVCaptureSessionPreset3840x2160");
        return AVCaptureSessionPreset3840x2160;
    }
    if ([device supportsAVCaptureSessionPreset:AVCaptureSessionPreset1920x1080]) {
        NSLog(@"AVCaptureSessionPreset1920x1080");
        return AVCaptureSessionPreset1920x1080;
    }
    if ([device supportsAVCaptureSessionPreset:AVCaptureSessionPreset1280x720]) {
        NSLog(@"AVCaptureSessionPreset1280x720");
        return AVCaptureSessionPreset1280x720;
    }
    if ([device supportsAVCaptureSessionPreset:AVCaptureSessionPreset640x480]) {
        NSLog(@"AVCaptureSessionPreset640x480");
        return AVCaptureSessionPreset640x480;
    }
    if ([device supportsAVCaptureSessionPreset:AVCaptureSessionPreset352x288]) {
        NSLog(@"AVCaptureSessionPreset352x288");
        return AVCaptureSessionPreset352x288;
    }
    if ([device supportsAVCaptureSessionPreset:AVCaptureSessionPresetHigh]) {
        NSLog(@"AVCaptureSessionPresetHigh");
        return AVCaptureSessionPresetHigh;
    }
    if ([device supportsAVCaptureSessionPreset:AVCaptureSessionPresetMedium]) {
        NSLog(@"AVCaptureSessionPresetMedium");
        return AVCaptureSessionPresetMedium;
    }
    NSLog(@"AVCaptureSessionPresetLow");
    return AVCaptureSessionPresetLow;
}

- (UIInterfaceOrientation)windowOrientation {
    if (@available(iOS 13.0, *)) {
        return self.view.window.windowScene.interfaceOrientation;
    } else {
        // Fallback on earlier versions
        return 0;
    }
}


- (AVCaptureMetadataOutput *)metadataOutput {
    if (!_metadataOutput) {
        _metadataOutput = [[AVCaptureMetadataOutput alloc] init];
        [_metadataOutput setMetadataObjectsDelegate:self queue:dispatch_get_main_queue()];
    }
    return _metadataOutput;
}

- (AVCaptureVideoDataOutput *)videoDataOutput {
    if (!_videoDataOutput) {
        _videoDataOutput = [[AVCaptureVideoDataOutput alloc] init];
        [_videoDataOutput setVideoSettings:[NSDictionary dictionaryWithObject:[NSNumber numberWithInt:kCVPixelFormatType_32BGRA] forKey:(id)kCVPixelBufferPixelFormatTypeKey]];
        [_videoDataOutput setSampleBufferDelegate:self queue:dispatch_get_main_queue()];
    }
    return _videoDataOutput;
}

- (void)captureOutput:(AVCaptureOutput *)output didOutputSampleBuffer:(CMSampleBufferRef)sampleBuffer fromConnection:(AVCaptureConnection *)connection {
    //    NSLog(@"摄像头数据");
    if ([FaceManager shareManager].savePhoto) {
        self.count = self.count + 1;
        //跳一帧
        if (self.count >= 40 && self.count%2 == 0) {
            NSString* prefix = [self.interceptChain currentType];
            NSString *savedFile = [self saveImage:[self mirrorImage:[self imageFromSampleBuffer: sampleBuffer]] filePrefix:prefix];
            [_interceptChain procced:savedFile];
            
            //        if (self.savePhoto) {
            //            if (!self.faceCompareSuccess) {
            //                self.savePhoto = NO;
            //                self.comparePhoto = [self saveImage:[self mirrorImage:[self imageFromSampleBuffer: sampleBuffer]] filePrefix:@"compare"];
            //                [self verifyCompare];
            //                NSDictionary * attributes = [NSFileManager.defaultManager attributesOfItemAtPath:self.comparePhoto error:nil];
            //                NSNumber *fileSize = [attributes objectForKey:NSFileSize];
            //                NSLog(@"%@", fileSize);
            //            } else {
            //                if (!self.mouthChecked) {
            //                    self.tipsLabel.text = @"请张开嘴巴后合上";
            //                    if (self.startMouthCheckFlag) {
            //                        NSLog(@"开始保存图片");
            //                        NSString *filePath = [self saveImage:[self mirrorImage:[self imageFromSampleBuffer: sampleBuffer]] filePrefix:@"mouth"];
            //                        NSLog(@"保存图片完成");
            //                        if (filePath.length != 0) {
            //                            if([self.mouthPhotos count] == 20) {
            //                                [self checkMouth];
            //                            } else {
            //                                [self.mouthPhotos addObject: filePath];
            //                            }
            //                        }
            //                    } else {
            //                        [self performSelector:@selector(startMoutchCheck) withObject:nil afterDelay:1];
            //                    }
            //                }
            //
            //
            //            }
            //        }
        }
    }
}

- (void) startMoutchCheck{
    self.startMouthCheckFlag = YES;
    self.tmpStartData = [NSDate date];
}


- (void)captureOutput:(AVCaptureOutput *)output didOutputMetadataObjects:(NSArray *)metadataObjects fromConnection:(AVCaptureConnection *)connection {
    //    NSLog(@"回调数据2");
    //    NSLog(@"摄像头数据2");
}


- (UIImage *) imageFromSampleBuffer:(CMSampleBufferRef) sampleBuffer {
    //    // 为媒体数据设置一个CMSampleBuffer的Core Video图像缓存对象
    //    CVImageBufferRef imageBuffer = CMSampleBufferGetImageBuffer(sampleBuffer);
    //    // 锁定pixel buffer的基地址
    //    CVPixelBufferLockBaseAddress(imageBuffer, 0);
    //
    //    // 得到pixel buffer的基地址
    //    void *baseAddress = CVPixelBufferGetBaseAddress(imageBuffer);
    //
    //    // 得到pixel buffer的行字节数
    //    size_t bytesPerRow = CVPixelBufferGetBytesPerRow(imageBuffer);
    //    // 得到pixel buffer的宽和高
    //    size_t width = CVPixelBufferGetWidth(imageBuffer);
    //    size_t height = CVPixelBufferGetHeight(imageBuffer);
    //
    //    // 创建一个依赖于设备的RGB颜色空间
    //    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
    //
    //    // 用抽样缓存的数据创建一个位图格式的图形上下文（graphics context）对象
    //    CGContextRef context = CGBitmapContextCreate(baseAddress, width, height, 8,
    //                                                 bytesPerRow, colorSpace, kCGBitmapByteOrder32Little | kCGImageAlphaPremultipliedFirst);
    //    // 根据这个位图context中的像素数据创建一个Quartz image对象
    //    CGImageRef quartzImage = CGBitmapContextCreateImage(context);
    //    // 解锁pixel buffer
    //    CVPixelBufferUnlockBaseAddress(imageBuffer,0);
    //
    //    // 释放context和颜色空间
    //    CGContextRelease(context);
    //    CGColorSpaceRelease(colorSpace);
    //
    //    // 用Quartz image创建一个UIImage对象image
    //    UIImage *image = [UIImage imageWithCGImage:quartzImage];
    //
    //    // 释放Quartz image对象
    //    CGImageRelease(quartzImage);
    //
    //    return (image);
    
    
    
    CVImageBufferRef imageBuffer = CMSampleBufferGetImageBuffer(sampleBuffer);
    CIImage *ciImage = [CIImage imageWithCVPixelBuffer:imageBuffer];
    CIContext *temporaryContext = [CIContext contextWithOptions:nil];
    CGImageRef videoImage = [temporaryContext createCGImage:ciImage fromRect:CGRectMake(0, 0, CVPixelBufferGetWidth(imageBuffer), CVPixelBufferGetHeight(imageBuffer))];
    
    UIImage *image = [[UIImage alloc] initWithCGImage:videoImage];
    CGImageRelease(videoImage);
    
    return image;
    
}

- (UIImage *)mirrorImage:(UIImage *)originImage{
    return [UIImage imageWithCGImage:originImage.CGImage scale:originImage.scale orientation:UIImageOrientationLeftMirrored];
}

@end
