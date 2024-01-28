package com.denknd.in.commands.functions;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LongIdParserFromRawParameters extends AbstractMyFunctionParser<String[], Long> {


    @Override
    public Long apply(String[] commandAndParam, String idParameter) {
        var addressIdString = this.parserParameters(commandAndParam, idParameter);
        if (addressIdString == null) {
            return null;
        }
        try {
            return Long.parseLong(addressIdString);
        } catch (NumberFormatException e) {
            System.out.println("Id введен не верно: "+addressIdString);
            return null;
        }
    }
}
