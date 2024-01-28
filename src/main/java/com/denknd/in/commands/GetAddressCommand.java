package com.denknd.in.commands;

import com.denknd.entity.Address;
import com.denknd.entity.Role;
import com.denknd.entity.User;
import com.denknd.in.commands.functions.LongIdParserFromRawParameters;
import com.denknd.in.commands.functions.MyFunction;
import com.denknd.services.AddressService;
import com.denknd.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс представляющий команду консоли, при помощи которой получают доступные адреса пользователя
 */
@Setter
@RequiredArgsConstructor
public class GetAddressCommand implements ConsoleCommand<String> {
    /**
     * Команда, которая отвечает за работу этого класса
     */
    private final String COMMAND = "get_addr";
    /**
     * Дополнительный параметр для получения айди Юзера из консоли
     */
    private final String USER_ID_PARAMETER = "user=";
    /**
     * Сервис для работы с адресами
     */
    private final AddressService addressService;
    /**
     * Сервис для работы с пользователями
     */
    private final UserService userService;
    /**
     * Парсер параметров в Long
     */
    private MyFunction<String[], Long> longIdParserFromRawParameters = new LongIdParserFromRawParameters();;

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
        return "Получает доступные адреса";
    }
    /**
     * Основной метод класса
     * @param command команда полученная из консоли
     * @param userActive активный юзер
     * @return возвращает сообщение об результате работы
     */
    @Override
    public String run(String command, User userActive) {
        if (userActive == null) {
            return null;
        }
        var userRole = Role.builder().roleName("USER").build();
        var adminRole = Role.builder().roleName("ADMIN").build();
        if (userActive.getRoles().contains(userRole)) {
            var addresses = this.addressService.getAddresses(userActive.getUserId());
            if (addresses.isEmpty()) {
                return "У вас не зарегистрировано ни одного адреса";
            }
            var collect = this.addressToStringConverter(addresses);

            return "Ваши адреса: \n" + collect;
        } else if (userActive.getRoles().contains(adminRole)) {
            var commandAndParam = command.split(" ");

            var id = this.longIdParserFromRawParameters.apply(commandAndParam, this.USER_ID_PARAMETER);
            if (id != null && this.userService.existUser(id)) {
                var addresses = this.addressService.getAddresses(id);
                if (addresses.isEmpty()) {
                    return "У пользователя с id-" + id + " не зарегистрировано ни одного адреса";
                }
                var collect = this.addressToStringConverter(addresses);
                return "Адреса пользователя с id-" + id + " : \n" + collect;
            } else {
                return "Нет адресов у пользователя с id = " + id;
            }
        }
        return null;
    }

    /**
     * Конвертирует список адресов в строку
     * @param addresses список адресов
     * @return строку из адресов
     */
    private String addressToStringConverter(List<Address> addresses) {
        return addresses.stream().map(address ->
                        "   id: " + address.getAddressId() + ", " + address.toString())
                .collect(Collectors.joining(",\n    "));
    }
    /**
     * Подсказка для команды help
     * @param roles роли доступные пользователю
     * @return возвращает сообщение с подсказкой по работе с данной командой
     */
    @Override
    public String getHelpCommand(List<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }
        var userRole = Role.builder().roleName("USER").build();
        var adminRole = Role.builder().roleName("ADMIN").build();
        var paramMessage = roles.contains(adminRole) ? "\nДополнительные параметры:\n    " + this.USER_ID_PARAMETER + " - ищет пользователя по заданному id(пример: " + this.USER_ID_PARAMETER + "234) Обязателен" : "";
        if (roles.contains(userRole) || roles.contains(adminRole)) {
            return this.COMMAND + " - показывает адреса пользователя" + paramMessage;
        }
        return null;
    }
}
