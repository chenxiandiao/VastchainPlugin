//
//  BluePrinterController.h
//  Pods
//
//  Created by cxd on 2021/12/30.
//

#ifndef BluePrinterController_h
#define BluePrinterController_h

#import "PrintModel.h"
#import "IBlueListener.h"

@interface BluePrinterController : NSObject

@property(nonatomic,strong)IBlueListener *blueListener;

-(id) init;

-(void) startScan;

-(void) printData:(NSString*) address printModel: (PrintModel*) data;



@end




#endif /* BluePrinterController_h */
