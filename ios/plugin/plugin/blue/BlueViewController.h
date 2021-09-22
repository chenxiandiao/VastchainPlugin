//
//  ViewController.h
//  bluetooth
//
//  Created by cxd on 2021/9/16.
//

#import <UIKit/UIKit.h>
#import <CoreBluetooth/CoreBluetooth.h>
#import <WebKit/WebKit.h>

@interface BlueViewController : UIViewController<CBCentralManagerDelegate,CBPeripheralDelegate,WKScriptMessageHandler>{
}
@property(nonatomic,strong)CBCentralManager *myCentralManager;
@property(nonatomic,strong)CBPeripheral *peripheral;
@property(nonatomic,strong)CBCharacteristic *characteristic;
@property WKWebView *myWebView;

- (void) initWebView;
- (void) initBlue;
- (void) scan;
- (void) stopScan;
- (void) stopScan:(BOOL) manual;
- (void) connectPeripheral:(CBPeripheral*)peripheral;
- (void) cancelPeripheral:(CBPeripheral*)peripheral;

- (void) write;

- (BOOL)isBluetoothAvailabel;

//十六进制字符串转NSData
- (NSData *)convertHexStrToData:(NSString *)str;

//NSData转十六进制字符串
- (NSString *)convertDataToHexStr:(NSData *)data;


@end

