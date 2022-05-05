//
//  IJsBridge.h
//  Pods
//
//  Created by cxd on 2022/2/23.
//

#ifndef IJsBridge_h
#define IJsBridge_h


#endif /* IJsBridge_h */

#import <Foundation/Foundation.h>
#import <WebKit/WebKit.h>

@interface IJsBridge : NSObject {
    WKWebView *myWebView;
}
- (id) initWithWebView: (WKWebView*) webview;
- (void) navigateBack;

- (void) getAppInfo:(NSDictionary*) data;
@end
