//
//  ViewController.m
//  bluetooth
//
//  Created by cxd on 2021/9/16.
//


#import "BlueViewController.h"
#import "IBlueListener.h"
#import "BlueJsApi.h"
#import <JavaScriptCore/JavaScriptCore.h>

@interface BlueViewController () {
    NSMutableArray *peripheralDataArray;
}

@property(nonatomic,copy)void(^connectBlock)(BOOL,NSError*);

@property(nonatomic,strong)IBlueListener *blueListener;

@end

@implementation BlueViewController


- (id)initWithUrl:(NSString *)url {
    self = [super init];
    if (self){
        mUrl = url;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationController.navigationBarHidden = YES;
    [self.view setBackgroundColor: [UIColor whiteColor]];
    [self initToolBar];
    [self initWebView];
    [self initListener];
    peripheralDataArray = [[NSMutableArray alloc]init];
}



- (void) initToolBar{
    CGFloat height = [[UIApplication sharedApplication] statusBarFrame].size.height;
    UIImageView *backBtn = [[UIImageView alloc]initWithFrame:CGRectMake(10,height+10,24,24)];
    [backBtn setImage:[UIImage imageNamed:@"BackIcon"]];
    [self.view addSubview:backBtn];
    backBtn.userInteractionEnabled = YES;
    UITapGestureRecognizer *singleTap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(goBack)];
    [backBtn addGestureRecognizer:singleTap];
    
    UILabel *aLabel = [[UILabel alloc]initWithFrame:CGRectMake(40, height,[UIScreen mainScreen].bounds.size.width-60, 44)];
    aLabel.numberOfLines = 0;
    aLabel.textColor = [UIColor blueColor];
    aLabel.backgroundColor = [UIColor clearColor];
    aLabel.textAlignment = NSTextAlignmentCenter;
    aLabel.text = @"蓝牙打卡";
    [self.view addSubview:aLabel];
    
}

- (void)initWebView {
    //    self.myWebView = [[WKWebView alloc]initWithFrame:self.view.bounds];
    CGFloat height = [[UIApplication sharedApplication] statusBarFrame].size.height;
    self.myWebView = [[WKWebView alloc]initWithFrame:CGRectMake(0, height+44, self.view.bounds.size.width, self.view.bounds.size.height -44)];
    //    NSString *url = @"http://10.159.179.214:8000";
    //    NSString *url = @"http://10.150.229.13:8000";
    //    NSString *url = @"http://10.155.87.121:10086/#/subPackage/warehouseManage/pages/wareHouseOperation/index?token=MmoXuOXOnvy8_r0Qstk4al1pHgdq-mmH&orgID=139723245184659456";
    //    NSString *url = @"http://www.baidu.com";
    NSString *url = mUrl;
    NSString *jspath = [[NSBundle mainBundle]pathForResource:@"log.js" ofType:nil];
    NSString *javaScriptSource = [NSString stringWithContentsOfFile:jspath encoding:NSUTF8StringEncoding error:nil];
    NSLog(@"%@", javaScriptSource);
    
    WKUserScript *userScript = [[WKUserScript alloc] initWithSource:javaScriptSource injectionTime:WKUserScriptInjectionTimeAtDocumentEnd forMainFrameOnly:YES];
    [self.myWebView.configuration.userContentController addUserScript:userScript];
    [self.myWebView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:url]]];
    [self.view addSubview:self.myWebView];
    [self.myWebView.configuration.userContentController addScriptMessageHandler:self name:@"BlueJSBridge"];
}

- (void)initListener{
    self.blueListener = [[IBlueListener alloc]initWithWebView:self.myWebView];
}

- (void)userContentController:(WKUserContentController *)userContentController didReceiveScriptMessage:(WKScriptMessage *)message {
    if (![message.name  isEqual: @"BlueJSBridge"]) {
        return;
    }
    NSLog(@"body:%@", message.body);
    
    if ([message.body hasPrefix:@"log:"]) {
        NSLog(@"h5日志:%@", message.body);
        return;
    }
    
    NSDictionary *body = [self dictionaryWithJsonString:message.body];
    NSString *method = [body objectForKey:@"method"];
    NSLog(@"method:%@", method);
    NSDictionary *params = [body objectForKey:@"params"];
    NSLog(@"params:%@", params);
    if ([method isEqualToString: SET_UP]) {
        [self initBlue];
    } else if ([method isEqualToString:SCAN]) {
        NSInteger timeOut = [[params objectForKey:@"timeout"] integerValue]/1000;
        NSLog(@"超时时间:%@", params);
        [self scan];
        [self performSelector:@selector(stopScan) withObject:nil afterDelay:timeOut];
    } else if ([method isEqualToString:CONNECT]) {
        NSString *uuid = [params objectForKey:@"deviceId"];
        NSLog(@"uuid:%@", uuid);
        [self connect:uuid];
    } else if ([method isEqualToString:DISCONNECT]) {
        
    }
    else if([method isEqualToString:WRITE]) {
        NSString *data = [params objectForKey:@"data"];
        [self write:data];
    } else if([method isEqualToString:STOP_SCAN]) {
        [self stopScan:YES];
    }
}

- (NSDictionary *)dictionaryWithJsonString:(NSString *)jsonString {
    if (jsonString == nil) {
        return nil;
    }
    NSData *jsonData = [jsonString dataUsingEncoding:NSUTF8StringEncoding];
    NSError *err;
    NSDictionary *dic = [NSJSONSerialization JSONObjectWithData:jsonData
                                                        options:NSJSONReadingMutableContainers
                                                          error:&err];
    if(err){
        NSLog(@"json解析失败：%@",err);
        return nil;
    }
    return dic;
}

- (void)initBlue {
    self.myCentralManager =
    [[CBCentralManager alloc] initWithDelegate:self queue:nil options:nil];
}

- (void)centralManagerDidUpdateState:(nonnull CBCentralManager *)central {
    NSLog(@"centralManagerDidUpdateState");
    if([self isBluetoothAvailabel]) {
        [self.blueListener setUpSuccess];
        //        [self scan];
    }
    
}


//获取蓝牙授权状态 isBluetoothAvailabel
- (BOOL)isBluetoothAvailabel{
    BOOL flag = NO;
    /*
     CBManagerStateUnknown = 0,
     CBManagerStateResetting,
     CBManagerStateUnsupported,
     CBManagerStateUnauthorized,
     CBManagerStatePoweredOff,
     CBManagerStatePoweredOn,
     */
    switch (self.myCentralManager.state) {
        case CBManagerStateUnknown:
            NSLog(@"未知状态");
            flag = YES;
            break;
        case CBManagerStateResetting:
            NSLog(@"连接断开 即将重置");
            break;
        case CBManagerStateUnsupported:
            NSLog(@"当前设备不支持蓝牙");
            break;
        case CBManagerStateUnauthorized:
            NSLog(@"未授权");
            break;
        case CBManagerStatePoweredOff:
            NSLog(@"蓝牙未开启");
            break;
        case CBManagerStatePoweredOn:
            NSLog(@"蓝牙可用");
            flag = YES;
            break;
    }
    return  flag;
}

- (void)scan {
    [self.myCentralManager scanForPeripheralsWithServices:nil options:nil];
}

- (void)stopScan:(BOOL)manual {
    [self.myCentralManager stopScan];
    [self.blueListener stopScanTimeOut];
}

- (void)stopScan {
    NSLog(@"扫描停止");
    [self.myCentralManager stopScan];
    [self.blueListener stopScanManual];
}


- (void)centralManager:(CBCentralManager *)central didDiscoverPeripheral:(CBPeripheral *)peripheral advertisementData:(NSDictionary<NSString *,id> *)advertisementData RSSI:(NSNumber *)RSSI {
    //    if (peripheral.name !=NULL) {
    //        NSLog(@"Discovered %@", peripheral.name);
    //    }
    
    if ([peripheral.name hasSuffix:@"2034"] || [peripheral.name hasSuffix:@"23E"]) {
        if(![peripheralDataArray containsObject:peripheral]) {
            [peripheralDataArray addObject:peripheral];
            NSLog(@"uuid:%@", [peripheral.identifier UUIDString]);
            [self.blueListener scanResult:[peripheral.identifier UUIDString] name:peripheral.name];
        }
    }
    
    //    if ([peripheral.name hasSuffix:@"2034"] || [peripheral.name hasSuffix:@"23E"]) {
    //        NSLog(@"发现要的蓝牙%@", peripheral.name);
    //        self.peripheral = peripheral;
    //
    //        [self.blueListener scanResult:peripheral name:<#(NSString *)#>]
    //
    //        //        TODO REMOVE
    //        [self stopScan];
    //
    //        //        TODO REMOVE
    //        [self connectPeripheral:peripheral Completion:^(BOOL, NSError *) {
    //            NSLog(@"等待回掉");
    //        }];
    //    }
}

- (void) connect: (NSString*) uuid {
    BOOL hit = NO;
    for(CBPeripheral *peripheral in peripheralDataArray) {
        if ([[peripheral.identifier UUIDString] isEqual:uuid]) {
            self.peripheral = peripheral;
            hit = YES;
            break;
        }
    }
    if (!hit) {
        NSInteger errcode = -1;
        [self.blueListener connectFail:errcode message:@"连接失败"];
    } else {
        [self connectPeripheral:self.peripheral];
    }
    
}

- (void)connectPeripheral:(CBPeripheral *)peripheral {
    //1.保存block
    //    self.connectBlock = completion;
    //2.连接外设 centralManager   connectPeripheral
    [self.myCentralManager connectPeripheral:peripheral options:nil];
}

- (void)cancelPeripheral:(CBPeripheral *)peripheral {
    [self.myCentralManager cancelPeripheralConnection:self.peripheral];
}

- (void)centralManager:(CBCentralManager *)central didDisconnectPeripheral:(CBPeripheral *)peripheral error:(NSError *)error {
    [self.blueListener disconnectSuccess];
    NSLog(@"断开蓝牙连接成功");
}

// 连接成功
- (void)centralManager:(CBCentralManager *)central didConnectPeripheral:(CBPeripheral *)peripheral {
    NSLog(@"Peripheral connected");
    self.peripheral = peripheral;
    self.peripheral.delegate = self;
    [self.peripheral discoverServices:nil];
}


- (void)centralManager:(CBCentralManager *)central didFailToConnectPeripheral:(CBPeripheral *)peripheral error:(NSError *)error {
    NSInteger errcode = -1;
    [self.blueListener connectFail:errcode message:@"连接失败"];
    //    [self.connectBlock(NO, error)];
}

- (void)peripheral:(CBPeripheral *)peripheral didDiscoverServices:(NSError *)error {
    for (CBService *service in peripheral.services) {
        
        NSLog(@"UUID:%@", service.UUID.UUIDString);
        //没有寻找服务特征之前服务是没有特征的
        //        NSLog(@"没有寻找服务特征之前服务是没有特征的:%@",service.characteristics);
        
        //第一个参数 寻找指定的特征 为nil表示寻找所有特征
        //第二个参数: 寻找哪个服务的特征
        [self.peripheral discoverCharacteristics:nil forService:service];
    }
}

- (void)peripheral:(CBPeripheral *)peripheral didUpdateNotificationStateForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error {
    NSLog(@"发现服务的特征%@",characteristic);
    //    self.characteristic = characteristic;
    //    [self write];
}

- (void)peripheral:(CBPeripheral *)peripheral didWriteValueForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error {
    NSLog(@"写入数据成功");
}

- (void)peripheral:(CBPeripheral *)peripheral didUpdateValueForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error {
    if (error) {
        NSLog(@"Error changing notification state: %@",
              [error localizedDescription]);
        return;
    }
    NSData *data = characteristic.value;
    NSString* response = [self convertDataToHexStr:data];
    [self.blueListener readCallback:response];
    NSLog(@"读取到的数据%@",response);
}

//发现服务的特征会调用的方法
-(void)peripheral:(CBPeripheral *)peripheral didDiscoverCharacteristicsForService:(CBService *)service error:(NSError *)error{
    
    NSLog(@"发现服务的特征%@",service.characteristics);
    
    for (CBCharacteristic *c in service.characteristics) {
        if(c.properties & CBCharacteristicPropertyNotify) {
            NSLog(@"设置notify");
            [self.peripheral setNotifyValue:YES forCharacteristic:c];
        }
        if(c.properties & CBCharacteristicPropertyWriteWithoutResponse) {
            NSLog(@"可写");
            self.characteristic = c;
        }
    }
    [self.blueListener connectSuccess];
    //    [self write];
    //        [self performSelector:@selector(write) withObject:nil afterDelay:2];
}

- (void)write:(NSString*) data {
    if (self.characteristic!=NULL) {
        NSLog(@"开始写数据");
        NSData *sendData =  [self convertHexStrToData:data];
        [self.peripheral writeValue:sendData forCharacteristic:self.characteristic type:CBCharacteristicWriteWithoutResponse];
    }
}

- (void)write {
    if (self.characteristic!=NULL) {
        NSLog(@"开始写数据");
        NSString *data = @"9003000010";
        NSData *sendData =  [self convertHexStrToData:data];
        [self.peripheral writeValue:sendData forCharacteristic:self.characteristic type:CBCharacteristicWriteWithoutResponse];
    }
}

//十六进制字符串转NSData
- (NSData *)convertHexStrToData:(NSString *)str {
    if (!str || [str length] == 0) {
        return nil;
    }
    
    NSMutableData *hexData = [[NSMutableData alloc] initWithCapacity:8];
    NSRange range;
    if ([str length] % 2 == 0) {
        range = NSMakeRange(0, 2);
    } else {
        range = NSMakeRange(0, 1);
    }
    for (NSInteger i = range.location; i < [str length]; i += 2) {
        unsigned int anInt;
        NSString *hexCharStr = [str substringWithRange:range];
        NSScanner *scanner = [[NSScanner alloc] initWithString:hexCharStr];
        
        [scanner scanHexInt:&anInt];
        NSData *entity = [[NSData alloc] initWithBytes:&anInt length:1];
        [hexData appendData:entity];
        
        range.location += range.length;
        range.length = 2;
    }
    
    //    NSLog(@"hexdata: %@", hexData);
    return hexData;
}

//NSData转十六进制字符串
- (NSString *)convertDataToHexStr:(NSData *)data {
    if (!data || [data length] == 0) {
        return @"";
    }
    NSMutableString *string = [[NSMutableString alloc] initWithCapacity:[data length]];
    
    [data enumerateByteRangesUsingBlock:^(const void *bytes, NSRange byteRange, BOOL *stop) {
        unsigned char *dataBytes = (unsigned char*)bytes;
        for (NSInteger i = 0; i < byteRange.length; i++) {
            NSString *hexStr = [NSString stringWithFormat:@"%x", (dataBytes[i]) & 0xff];
            if ([hexStr length] == 2) {
                [string appendString:hexStr];
            } else {
                [string appendFormat:@"0%@", hexStr];
            }
        }
    }];
    
    return string;
}

- (void) goBack {
    NSLog(@"返回");
    if([self.myWebView canGoBack]) {
        [self.myWebView goBack];
    } else {
        NSLog(@"关闭WebView页面");
        [self.navigationController popViewControllerAnimated:FALSE];
    }
}

@end
