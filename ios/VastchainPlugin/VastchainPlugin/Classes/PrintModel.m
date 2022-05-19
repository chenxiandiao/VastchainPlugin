//
//  PrintModel.m
//  AFNetworking
//
//  Created by cxd on 2021/12/30.
//

#import <Foundation/Foundation.h>
#import "PrintModel.h"

@implementation PrintModel


//-(void) initWithUrl:(NSString *)url qrCodeId:(NSString *)qrCodeId name: (NSString *)name
//        packageCount: (NSString*) packageCount totalCount: (NSString *) totalCount orgName: (NSString*) orgName{
//    self.url = url;
//    self.qrCodeId = qrCodeId;
//    self.name = name;
//    self.packageCount = packageCount;
//    self.totalCount = totalCount;
//    self.orgName = orgName;
//
//}


-(void)initNoConfigCommodity:(NSString *)url qrCodeId:(NSString *)qrCodeId  orgName: (NSString*) orgName {
    self.url = url;
    self.qrCodeId = qrCodeId;
    self.orgName = orgName;
}

-(void)initConfigCommodity:(NSString *)url qrCodeId:(NSString *)qrCodeId name: (NSString *)name
                totalCount: (NSString *) totalCount orgName: (NSString*) orgName {
    self.url = url;
    self.qrCodeId = qrCodeId;
    self.name = name;
    self.totalCount = totalCount;
    self.orgName = orgName;
}

-(void)initStoreHouse:(NSString *)url qrCodeId:(NSString *)qrCodeId  orgName: (NSString*) orgName storehouseName: (NSString*) storehouseName storehouseOrgName: (NSString*) storehouseOrgName {
    self.url = url;
    self.qrCodeId = qrCodeId;
    self.orgName = orgName;
    self.storehouseName = storehouseName;
    self.storehouseOrgName = storehouseOrgName;
}

- (BOOL)isWareHouse {
    return self.storehouseName!=nil && [self.storehouseName isEqualToString:@""] == NO;
}

- (BOOL)isConfigCommodity {
    return self.name!=nil && [self.name isEqualToString:@""] == NO;
}
@end
