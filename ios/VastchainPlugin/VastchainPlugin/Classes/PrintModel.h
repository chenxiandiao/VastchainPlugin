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
@property NSString *packageCount;
@property NSString *totalCount;
@property NSString *orgName;

-(void)initWithUrl:(NSString *)url qrCodeId:(NSString *)qrCodeId name: (NSString *)name
      packageCount: (NSString*) packageCount totalCount: (NSString *) totalCount orgName: (NSString*) orgName;

@end

#endif /* PrintModel_h */
