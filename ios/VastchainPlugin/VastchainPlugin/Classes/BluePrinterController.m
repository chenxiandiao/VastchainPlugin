//
//  BluePrinterController.m
//  VastchainPlugin
//
//  Created by cxd on 2021/12/30.
//

#import <Foundation/Foundation.h>
#import "BluePrinterController.h"
#import <CoreBluetooth/CoreBluetooth.h>
#import <zicox_ios_sdk/Bluetooth.h>

@interface BluePrinterController ()

@property (strong, nonatomic) Bluetooth* bluetooth;
@property (strong, nonatomic) CBPeripheral* peripheral;
@property (strong, nonatomic) NSMutableArray* listDevices;
@property (strong, nonatomic) NSMutableString* listDeviceInfo;
@end

@implementation BluePrinterController

-(id)init {
    NSLog(@"111");
    self = [super init];
    if (self) {
        self.bluetooth = [[Bluetooth alloc]init];
        self.listDevices = [NSMutableArray array];
    }
    return self;
}

-(void) startScan{
    BLOCK_CALLBACK_SCAN_FIND callback =
    ^( CBPeripheral*peripheral)
    {
        NSLog(@"uuid:%@", peripheral.name);
//        if ([peripheral.name hasPrefix:@"CT"]) {
            if(![self.listDevices containsObject:peripheral]) {
                [self.listDevices addObject:peripheral];
                if([peripheral.name hasSuffix:@"L"]) {
                    [self.blueListener scanResult:[peripheral.identifier UUIDString] name:peripheral.name];
                }
                else {
                    [self.blueListener scanResult:[peripheral.identifier UUIDString] name:[NSString stringWithFormat:@"%@L", peripheral.name]];
                }
            } else {
                if([peripheral.name hasSuffix:@"L"]) {
                    [self.blueListener scanResult:[peripheral.identifier UUIDString] name:peripheral.name];
                } else {
                    [self.blueListener scanResult:[peripheral.identifier UUIDString] name:[NSString stringWithFormat:@"%@L", peripheral.name]];
                }
            }
//        }
    };
    
    [self.bluetooth scanStart:callback];
    
    double delayInSeconds = 10.0;
    dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delayInSeconds* NSEC_PER_SEC));
    dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
        [self.bluetooth scanStop];
    });
}

-(void) stopScan{
    [self.bluetooth scanStop];
}

-(BOOL) connect:(NSString*) deviceName {
    for (CBPeripheral *item in self.listDevices) {
        if([item.name isEqualToString:deviceName] || [deviceName hasPrefix:item.name]) {
            self.peripheral = item;
        }
    }
    return [self.bluetooth open: self.peripheral];
}

- (void)printData:(NSString *)deviceName printModel:(PrintModel *)data {
    for (CBPeripheral *item in self.listDevices) {
        if([item.name isEqualToString:deviceName] || [deviceName hasPrefix:item.name]) {
            self.peripheral = item;
        }
    }
    
    [self.bluetooth open:self.peripheral];
    [self wrapPrintDatas:data];
    [self sendPrintData];

    [self.bluetooth reset];
    [self.bluetooth print_status_detect];
    int status=[self.bluetooth print_status_get:3000];
    if(status==1)
    {
        NSLog(@"打印机缺纸" );
        [self.blueListener printError:-1 message:@"打印机缺纸"];
    }
    if(status==2)
    {
        NSLog(@"打印机开盖" );
        [self.blueListener printError:-1 message:@"打印机开盖"];
    }
    if(status==0)
    {
        NSLog(@"打印机正常" );
        [self.blueListener printSuccess];
    }
    [self.bluetooth close];
}

- (void)wrapPrintDatas:(PrintModel*) data
{
    if([data isWareHouse]) {
        [self printStorehouse:data];
    } else if([data isConfigCommodity]) {
        [self printConfigCommodity:data];
    } else {
        [self printNoConfigCommodity:data];
    }
}

- (void)printStorehouse:(PrintModel*) data {
    NSLog(@"打印仓库二维码");
    [self.bluetooth StartPage:600 pageHeight:350 skip:false rotate:0];// skip:true为定位到标签，false为不定位
    [self.bluetooth zp_darwQRCode:30 y:40 unit_width:5 text:data.url];
    if (data.storehouseName != nil && [data.storehouseName isEqualToString:@""] == NO) {
        [self.bluetooth zp_drawText:280 y:47 text:data.storehouseName font:12 fontsize:1 bold:0 rotate:0];
    }
    if (data.storehouseOrgName != nil && [data.storehouseOrgName isEqualToString:@""] == NO) {
        [self.bluetooth zp_drawText:280 y:89 text:data.storehouseOrgName font:12 fontsize:1 bold:0 rotate:0];
    }
    if (data.qrCodeId != nil && [data.qrCodeId isEqualToString:@""] == NO) {
        [self.bluetooth zp_drawText:280 y:169 text:data.qrCodeId font:12 fontsize:1 bold:0 rotate:0];
    }
    if (data.orgName != nil && [data.orgName isEqualToString:@""] == NO) {
        [self.bluetooth zp_drawText:280 y:211 text:data.orgName font:12 fontsize:1 bold:0 rotate:0];
    }
    [self.bluetooth end];
}

- (void)printConfigCommodity:(PrintModel*) data {
    NSLog(@"打印配置的商品二维码");
    [self.bluetooth StartPage:600 pageHeight:350 skip:false rotate:0];// skip:true为定位到标签，false为不定位
    [self.bluetooth zp_darwQRCode:30 y:40 unit_width:5 text:data.url];
    if (data.name != nil && [data.name isEqualToString:@""] == NO) {
        [self.bluetooth zp_drawText:280 y:47 text:data.name font:12 fontsize:1 bold:0 rotate:0];
    }
    if (data.totalCount != nil && [data.totalCount isEqualToString:@""] == NO) {
        [self.bluetooth zp_drawText:280 y:89 text:data.totalCount font:12 fontsize:1 bold:0 rotate:0];
    }
    if (data.qrCodeId != nil && [data.qrCodeId isEqualToString:@""] == NO) {
        [self.bluetooth zp_drawText:280 y:169 text:data.qrCodeId font:12 fontsize:1 bold:0 rotate:0];
    }
    if (data.orgName != nil && [data.orgName isEqualToString:@""] == NO) {
        [self.bluetooth zp_drawText:280 y:211 text:data.orgName font:12 fontsize:1 bold:0 rotate:0];
    }
    [self.bluetooth end];
}

- (void)printNoConfigCommodity:(PrintModel*) data {
    NSLog(@"打印无配置二维码");
    [self.bluetooth StartPage:600 pageHeight:350 skip:false rotate:0];// skip:true为定位到标签，false为不定位
    [self.bluetooth zp_darwQRCode:30 y:40 unit_width:5 text:data.url];
    if (data.qrCodeId != nil || [data.qrCodeId isEqualToString:@""] == NO) {
        [self.bluetooth zp_drawText:280 y:47 text:data.qrCodeId font:12 fontsize:1 bold:0 rotate:0];
    }
    if (data.orgName != nil || [data.orgName isEqualToString:@""] == NO) {
        [self.bluetooth zp_drawText:280 y:211 text:data.orgName font:12 fontsize:1 bold:0 rotate:0];
    }
    [self.bluetooth end];
}
    
-(void) sendPrintData{
    int r = self.bluetooth.dataLength;
    NSData *data = [self.bluetooth getData:r];
    NSString * str  =[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    NSLog(@"文案：%@", str);
    
//    NSString * str =@"! 0 200 200 300 1\nTEXT 4 3 10 0 8.14\nPRINT\n";
//    NSData *data =[str dataUsingEncoding:NSUTF8StringEncoding];
    [self.bluetooth writeData:data];
}
@end
