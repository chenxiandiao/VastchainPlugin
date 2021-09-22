//
//  JSResponse.h
//  bluetooth
//
//  Created by cxd on 2021/9/16.
//

#ifndef JSResponse_h
#define JSResponse_h


#endif /* JSResponse_h */

@interface Message : NSObject {
    NSString *code;
    NSString *message;
    NSString *msg_;
}

@property (nonatomic, retain) NSString *from;
@property (nonatomic, retain) NSString *date;
@property (nonatomic, retain) NSString *msg;

-(NSDictionary *)dictionary;

@end

//Message.m
#import "Message.h"

@implementation Message

@synthesize from = from_;
@synthesize date = date_;
@synthesize msg = mesg_;

-(void) dealloc {
    self.from = nil;
    self.date = nil;
    self.msg = nil;
    [super dealloc];
}

-(NSDictionary *)dictionary {
    return [NSDictionary dictionaryWithObjectsAndKeys:self.from,@"from",self.date,    @"date",self.msg, @"msg", nil];
}
