//
//  IBlueListener.m
//  bluetooth
//
//  Created by cxd on 2021/9/17.
//

#import <Foundation/Foundation.h>
#import "IBlueListener.h"
#import "BlueJsApi.h"

@interface IBlueListener ()

@end

@implementation IBlueListener

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

- (NSString*)error:(NSString*) message {
    NSMutableDictionary *data = [NSMutableDictionary dictionaryWithCapacity:10];
    [data setObject:@-1 forKey:@"code"];
    [data setObject:message forKey:@"message"];
    return [self dictToJson:data];
}

- (void)setUpSuccess{
    NSString* response = [self success:MESSAGE_SUCCESS];
    [self invoke:SET_UP data:response];
}

- (void) setUpFail{
    NSString* response = [self success:MESSAGE_FAIL];
    [self invoke:SET_UP data:response];
}

- (void) unConnect{
    
}

- (void) scanResult: (NSString*) address name: (NSString*) name{
    NSMutableDictionary *responsObj = [self successObj:@"扫描结果"];
    NSMutableDictionary *data = [NSMutableDictionary dictionaryWithObjectsAndKeys:address,@"deviceId", name, @"name", nil];
    [responsObj setObject:data forKey:@"data"];
    NSString *response =  [self dictToJson:responsObj];
    NSLog(@"respons:%@",response);
    [self invoke:SCAN data:response];
}

- (void)stopScanManual {
    NSString* response = [self success:MESSAGE_SUCCESS];
    [self invoke:SCAN data:response];
}

- (void)stopScanTimeOut {
    NSString* response = [self successCode:1 message:MESSAGE_SUCCESS];
    [self invoke:SCAN data:response];
}

- (void) connectSuccess{
    NSString* response = [self success:MESSAGE_SUCCESS];
    [self invoke:CONNECT data:response];
}

- (void) connectFail: (NSInteger) errCode message: (NSString*) message{
    NSString* response = [self error:message];
    [self invoke:CONNECT data:response];
}

- (void) disconnectSuccess{
    NSString* response = [self success:MESSAGE_SUCCESS];
    [self invoke:DISCONNECT data:response];
}

// 暂未调用
- (void) writeSuccess{
    NSString* response = [self success:MESSAGE_SUCCESS];
    [self invoke:WRITE data:response];
}

// 暂未调用
- (void) writeFail{
    NSString* response = [self success:MESSAGE_FAIL];
    [self invoke:WRITE data:response];
}

- (void) readCallback: (NSString*) data{
    NSMutableDictionary *responsObj = [self successObj:@"读取数据结果"];
    [responsObj setObject:data forKey:@"data"];
    NSString *response = [self dictToJson:responsObj];
    [self invoke:READ data:response];
}

- (void)qrScanResult:(NSString *)data {
    NSMutableDictionary *responsObj = [self successObj:@"扫描结果"];
    [responsObj setObject:data forKey:@"data"];
    NSString *response = [self dictToJson:responsObj];
    [self invoke:SCAN_QR_CODE data:response];
}

- (void) invoke: (NSString*) method data: (NSString*) data{
    NSString *callback = [NSString stringWithFormat:@"blueCallback(\"%@\",%@)",method, data];
    NSLog(@"callback:%@", callback);
    [myWebView evaluateJavaScript:callback completionHandler:^(id _Nullable , NSError * _Nullable error) {
        NSLog(@"js返回");
    }];
}

- (void)printSuccess {
    NSString* response = [self success:MESSAGE_SUCCESS];
    [self invoke:PRITN_DATA data:response];
}

- (void)printError:(NSInteger)errCode message:(NSString *)message {
    NSString* response = [self error:MESSAGE_FAIL];
    [self invoke:PRITN_DATA data:response];
}
@end

