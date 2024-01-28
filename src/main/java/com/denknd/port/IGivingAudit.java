package com.denknd.port;

public interface IGivingAudit {
   String getCommand();
    default String getMakesAction(){
        return "не известная команда";
    };
}
