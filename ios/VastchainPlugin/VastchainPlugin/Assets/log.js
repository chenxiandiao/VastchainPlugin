

console.log = (function(oriLogFunc){
                            return function(str){
                                        oriLogFunc.call(console,str);
                                            window.webkit.messageHandlers.BlueJSBridge.postMessage("log:" + str);
                                    }
                            })(console.log);


console.log("111");
