//
//  IJsBridge.m
//  AFNetworking
//
//  Created by cxd on 2022/2/23.
//


#import <Foundation/Foundation.h>
#import "IJsBridge.h"
#import "JsApi.h"

#define MESSAGE_SUCCESS @"成功"
#define MESSAGE_FAIL @"失败"

@interface IJsBridge ()

@end

@implementation IJsBridge

- (id)initWithWebView:(WKWebView *)webview {
    myWebView = webview;
    return self;
}

- (NSMutableDictionary*)successObj:(NSString*) message {
    NSMutableDictionary *data = [NSMutableDictionary dictionaryWithCapacity:10];
    [data setObject:@0 forKey:@"code"];
    [data setObject:message forKey:@"message"];
    return data;
}

- (NSString*) dictToJson:(NSMutableDictionary*) data {
    NSError *parseError = nil;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:data options:NSJSONWritingPrettyPrinted error:&parseError];
    NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    return jsonString;
}


- (NSString*)successCode: (NSInteger)code message: (NSString*) message {
    NSMutableDictionary *data = [NSMutableDictionary dictionaryWithCapacity:10];
    [data setObject: [NSNumber numberWithInteger:code] forKey:@"code"];
    [data setObject:message forKey:@"message"];
    return [self dictToJson:data];
}

- (NSString*)success:(NSString*) message {
    NSMutableDictionary *data = [NSMutableDictionary dictionaryWithCapacity:10];
    [data setObject:@0 forKey:@"code"];
    [data setObject:message forKey:@"message"];
    return [self dictToJson:data];
}

- (NSString*)success:(NSString*) message innerData:(NSDictionary*) innerData {
    NSMutableDictionary *data = [NSMutableDictionary dictionaryWithCapacity:10];
    [data setObject:@0 forKey:@"code"];
    [data setObject:message forKey:@"message"];
    [data setObject:innerData forKey:@"data"];
    return [self dictToJson:data];
}

- (NSString*)error:(NSString*) message {
    NSMutableDictionary *data = [NSMutableDictionary dictionaryWithCapacity:10];
    [data setObject:@-1 forKey:@"code"];
    [data setObject:message forKey:@"message"];
    return [self dictToJson:data];
}

- (void)navigateBack{
    NSString* response = [self success:MESSAGE_SUCCESS];
    [self invoke:NavigateBack data:response];
}

- (void) getAppInfo:(NSDictionary*) data {
    NSString* response = [self success:MESSAGE_SUCCESS innerData:data];
    [self invoke:JS_GET_APP_INFO data:response];
}

- (void) invoke: (NSString*) method data: (NSString*) data{
    NSString *callback = [NSString stringWithFormat:@"window.jsBridge.receiveMessage(\"%@\",%@)",method, data];
    NSLog(@"callback:%@", callback);
    [myWebView evaluateJavaScript:callback completionHandler:^(id _Nullable , NSError * _Nullable error) {
        NSLog(@"js返回");
    }];
}


@end

