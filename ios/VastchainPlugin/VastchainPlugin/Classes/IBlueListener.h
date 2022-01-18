//
//  IBlueListener.h
//  bluetooth
//
//  Created by cxd on 2021/9/17.
//

#ifndef IBlueListener_h
#define IBlueListener_h


#endif /* IBlueListener_h */

#import <Foundation/Foundation.h>
#import <WebKit/WebKit.h>

@interface IBlueListener : NSObject {
    WKWebView *myWebView;
}
- (id) initWithWebView: (WKWebView*) webview;
- (void) setUpSuccess;
- (void) setUpFail;
- (void) unConnect;
- (void) scanResult: (NSString*) address name: (NSString*) name;
- (void) stopScanManual;
- (void) stopScanTimeOut;
- (void) connectSuccess;
- (void) connectFail: (NSInteger) errCode message: (NSString*) message;
- (void) disconnectSuccess;
- (void) writeSuccess;
- (void) writeFail;
- (void) readCallback: (NSString*) data;
- (void) qrScanResult: (NSString*) data;
- (void) invoke: (NSString*) method data: (NSString*) data;

- (void) printSuccess;
- (void) printError:(NSInteger) errCode message: (NSString*) message;
@end
