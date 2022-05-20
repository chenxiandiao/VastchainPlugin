//
//  PrintModel.h
//  Pods
//
//  Created by cxd on 2021/12/30.
//

#ifndef PrintModel_h
#define PrintModel_h


@interface PrintModel : NSObject{
    
}

@property NSString *url;
@property NSString *qrCodeId;
@property NSString *name;
@property NSString *totalCount;
@property NSString *orgName;

@property NSString *storehouseName;
@property NSString *storehouseOrgName;

//-(void)initWithUrl:(NSString *)url qrCodeId:(NSString *)qrCodeId name: (NSString *)name
//      packageCount: (NSString*) packageCount totalCount: (NSString *) totalCount orgName: (NSString*) orgName;

-(void)initNoConfigCommodity:(NSString *)url qrCodeId:(NSString *)qrCodeId  orgName: (NSString*) orgName;

-(void)initConfigCommodity:(NSString *)url qrCodeId:(NSString *)qrCodeId name: (NSString *)name
              totalCount: (NSString *) totalCount orgName: (NSString*) orgName;

-(void)initStoreHouse:(NSString *)url qrCodeId:(NSString *)qrCodeId  orgName: (NSString*) orgName storehouseName: (NSString*) storehouseName storehouseOrgName: (NSString*) storehouseOrgName;

-(BOOL)isWareHouse;
-(BOOL)isConfigCommodity;
@end

#endif /* PrintModel_h */
