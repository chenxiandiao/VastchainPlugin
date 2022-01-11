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
        if ([peripheral.name hasPrefix:@"CT"]) {
            if(![self.listDevices containsObject:peripheral]) {
                [self.listDevices addObject:peripheral];
                [self.blueListener scanResult:[peripheral.identifier UUIDString] name:peripheral.name];
            }
        }
    };
    
    [self.bluetooth scanStart:callback];
    
    double delayInSeconds = 5.0;
    dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delayInSeconds* NSEC_PER_SEC));
    dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
        [self.bluetooth scanStop];
    });
}


- (void)printData:(NSString *)address printModel:(PrintModel *)data {
    
    for (CBPeripheral *item in self.listDevices) {
        if([[item.identifier UUIDString] isEqualToString:address]) {
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
        
    }
    if(status==2)
    {
        NSLog(@"打印机开盖" );
        
    }
    if(status==0)
    {
        NSLog(@"打印机正常" );
        
    }
    [self.bluetooth close];
}

- (void)wrapPrintDatas:(PrintModel*) data
{
    [self.bluetooth StartPage:576 pageHeight:720 skip:false rotate:0];// skip:true为定位到标签，false为不定位 ;==--p0p-p---rotate:0\90\180\270
    [self.bluetooth zp_darwRect:2 top:2 right:400 bottom:600 width:1];
    [self.bluetooth zp_drawText:0 y:20 text:@"上海芝柯打印技术" font:24 fontsize:2 bold:0 rotate:0];
    [self.bluetooth zp_drawLine:2 startPiontY:200 endPointX:400 endPointY:200 width:2 ];
    [self.bluetooth zp_darw1D_barcode:10 y:240 height:100 text:@"1234567890"];
    [self.bluetooth zp_darwQRCode:10 y:400 unit_width:5 text:@"12234567890"];
    [self.bluetooth end];

}
    
-(void) sendPrintData{
    int r = self.bluetooth.dataLength;
    NSData *data = [self.bluetooth getData:r];
    [self.bluetooth writeData:data];
  /*
    Byte a[2] ;
    a[0]=0x1d;a[1]=0x0c;
    NSData *adata = [[NSData alloc] initWithBytes:a length:2];
    [ self.bluetooth writeData:adata];
  */
}
@end
