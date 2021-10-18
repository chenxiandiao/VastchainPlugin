//
//  Interceptor.h
//  VastchainPlugin
//
//  Created by cxd on 2021/10/13.
//  Copyright © 2021 chenxiandiao. All rights reserved.
//




@class InterceptChain;

@interface Interceptor:NSObject

-(void) procceed:(NSString*)file chain: (InterceptChain*)chain;
-(BOOL) checked;
-(void) procceedBegin ;



@end



//@class Interceptor;
//@protocol Interceptor <NSObject>
//
//@required
//-(void) procceed:(NSString*)file chain: (InterceptChain*)chain;
//-(BOOL) checked;
//-(void) procceedBegin ;
//@end
