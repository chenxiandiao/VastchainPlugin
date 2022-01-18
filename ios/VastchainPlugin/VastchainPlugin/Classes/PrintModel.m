//
//  PrintModel.m
//  AFNetworking
//
//  Created by cxd on 2021/12/30.
//

#import <Foundation/Foundation.h>
#import "PrintModel.h"

@implementation PrintModel


-(void) initWithUrl:(NSString *)url qrCodeId:(NSString *)qrCodeId name: (NSString *)name
        packageCount: (NSString*) packageCount totalCount: (NSString *) totalCount orgName: (NSString*) orgName{
    self.url = url;
    self.qrCodeId = qrCodeId;
    self.name = name;
    self.packageCount = packageCount;
    self.totalCount = totalCount;
    self.orgName = orgName;
    
}

@end
