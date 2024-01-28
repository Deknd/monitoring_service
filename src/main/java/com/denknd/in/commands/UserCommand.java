package com.denknd.in.commands;

import com.denknd.entity.Role;
import com.denknd.entity.User;
import com.denknd.in.commands.functions.LongIdParserFromRawParameters;
import com.denknd.in.commands.functions.MyFunction;
import com.denknd.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
/**
 * Класс представляющий команду консоли, при помощи которой добавляется новый адрес
 */
@Setter
@RequiredArgsConstructor
public class UserCommand implements ConsoleCommand<String>{
    /**
     * Команда, которая отвечает за работу этого класса
     */
    private final String COMMAND = "user";
    /**
     * Дополнительный параметр, для получения информации о пользователи по емайл
     */
    private final String EMAIL_PARAM = "email=";
    /**
     * Дополнительный параметр, для получения информации о пользователи по айди
     */
    private final String ID_PARAM = "id=";
    /**
     * Сервис для работы с адресами
     */
    private final UserService userService;
    /**
     * Функция по извлечению из параметров числа
     */
    private MyFunction<String[], Long> longIdParserFromRawParameters = new LongIdParserFromRawParameters();

    /**
     * Возвращает команду, которая запускает работу метода run
     * @return команда для работы класса
     */
    @Override
    public String getCommand() {
        return this.COMMAND;
    }
    /**
     * Возвращает пояснение работы класса
     * @return пояснение, что делает класс, для аудита
     */
    @Override
    public String getMakesAction() {
        return "Выдает информацию о пользователе";
    }
    /**
     * Основной метод класса
     * @param command команда полученная из консоли
     * @param userActive активный юзер
     * @return возвращает сообщение об результате работы
     */
    @Override
    public String run(String command, User userActive) {
        var userRole = Role.builder().roleName("USER").build();
        var adminRole = Role.builder().roleName("ADMIN").build(); 
        if(userActive == null){
            return null;
        }
        if(userActive.getRoles().contains(userRole)){
            return userToStringConvert(userActive);
        } else if(userActive.getRoles().contains(adminRole)){
            var commandAndParam = command.split(" ");
            var id = this.longIdParserFromRawParameters.apply(commandAndParam, this.ID_PARAM);
            var email = Arrays.stream(commandAndParam)
                    .filter(param -> param.contains(this.EMAIL_PARAM))
                    .map(param -> param.replace(this.EMAIL_PARAM, ""))
                    .findFirst().orElse(null);
            if(id != null && this.userService.existUser(id)){
                var user = this.userService.getUserById(id);
                return this.userToStringConvert(user);
            } if(email != null && this.userService.existUserByEmail(email)){
                var user = this.userService.getUserByEmail(email);
                return this.userToStringConvert(user);
            }
        }
        return null;
    }

    /**
     * Конвертирует пользователя в строку
     * @param userActive
     * @return
     */
    private String userToStringConvert(User userActive) {
        return "id: "+userActive.getUserId() + ", " + userActive.getLastName() + " " + userActive.getFirstName() + ", " + userActive.getEmail();
    }
    /**
     * Подсказка для команды help
     * @param roles роли доступные пользователю
     * @return возвращает сообщение с подсказкой по работе с данной командой
     */
    @Override
    public String getHelpCommand(List<Role> roles) {
        var userRole = Role.builder().roleName("USER").build();
        var adminRole = Role.builder().roleName("ADMIN").build();
        if(roles == null || roles.isEmpty()){
            return null;
        }
        var param = roles.contains(adminRole) ?
                "\nДополнительные параметры:\n        "+
                this.EMAIL_PARAM + " - показывает пользователя с данным email(пример: "+this.EMAIL_PARAM+"test@mail.ru),\n        "+
                this.ID_PARAM + " - показывает пользователя с данным айди(пример: "+this.ID_PARAM+"2345)": "";
        if(roles.contains(userRole) || roles.contains(adminRole)){
            return this.COMMAND +" - Команда для просмотра данных о пользователе" + param;
        }
        return null;
    }
}
