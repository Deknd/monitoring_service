package com.denknd.in.commands.functions;

import lombok.RequiredArgsConstructor;

/**
 * Парсит параметры и достает из нее цифры
 */
@RequiredArgsConstructor
public class LongIdParserFromRawParameters extends AbstractMyFunctionParser<String[], Long> {

    /**
     * Парсит из параметров по указанному параметру цифры
     * @param commandAndParam массив параметров
     * @param idParameter параметр
     * @return возвращает Long или null, если введена не цифра
     */
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
