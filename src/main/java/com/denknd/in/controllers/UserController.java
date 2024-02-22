package com.denknd.in.controllers;

import com.denknd.audit.api.AuditRecording;
import com.denknd.dto.UserCreateDto;
import com.denknd.dto.UserDto;
import com.denknd.entity.Parameters;
import com.denknd.exception.InvalidUserDataException;
import com.denknd.exception.UserAlreadyExistsException;
import com.denknd.mappers.UserMapper;
import com.denknd.services.UserService;
import com.denknd.swagger.RespBadRequest;
import com.denknd.swagger.RespConflict;
import com.denknd.swagger.RespForbidden;
import com.denknd.swagger.RespServerError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

/**
 * Контроллер для работы с пользователями.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "UserController",
        description = "Дает доступ для управления пользователями"
)
public class UserController {
  /**
   * Сервис для управления пользователями
   */
  private final UserService userService;

  /**
   * Маппер пользователей
   */
  private final UserMapper userMapper;

  /**
   * Метод создает нового пользователя в системе.
   *
   * @param userCreateDto DTO объект, содержащий данные для создания пользователя.
   * @return DTO объект с созданным пользователем.
   * @throws UserAlreadyExistsException если данный пользователь уже существует.
   * @throws NoSuchAlgorithmException   если не удалось использовать алгоритм шифрования.
   * @throws InvalidUserDataException   если произошла ошибка при сохранении данных в базе данных.
   */
  @PostMapping
  @AuditRecording("Регистрирует нового пользователя")
  @Operation(summary = "Добавляет новый пользователя в систему")
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "201",
                  description = "Пользователь создан",
                  content = @Content(
                          mediaType = MediaType.APPLICATION_JSON_VALUE,
                          schema = @Schema(implementation = UserDto.class)
                  )
          )
  })
  @RespConflict
  @RespServerError
  @RespBadRequest
  @RespForbidden
  public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserCreateDto userCreateDto)
          throws UserAlreadyExistsException, NoSuchAlgorithmException, InvalidUserDataException, AccessDeniedException {
    var user = this.userMapper.mapUserCreateDtoToUser(userCreateDto);
    var result = this.userService.registrationUser(user);
    var userDto = this.userMapper.mapUserToUserDto(result);
    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(userDto);

  }

  /**
   * Метод получает данные пользователя по идентификатору или электронному адресу.
   *
   * @param userId идентификатор пользователя.
   * @param email  электронный адрес пользователя.
   * @return DTO объект с данными пользователя.
   */
  @GetMapping
  @AuditRecording("Получает информацию о пользователе")
  @Operation(summary = "Получение списка адресов(roles: USER, ADMIN)")
  @ApiResponses(
          value = {
                  @ApiResponse(
                          responseCode = "200",
                          description = "Для Получает информацию о пользователе",
                          content = @Content(
                                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                                  array = @ArraySchema(schema = @Schema(implementation = UserDto.class))))
          }
  )
  public ResponseEntity<UserDto> getUser(
          @Parameter(description = "Идентификатор пользователя(роли: ADMIN)")
          @RequestParam("id") Optional<Long> userId,
          @Parameter(description = "Электронный адрес пользователя(роли: ADMIN)")
          @RequestParam("email") Optional<String> email) {
    var parameters = Parameters.builder().userId(userId.orElse(null)).email(email.orElse(null)).build();
    var user = this.userService.getUser(parameters);
    var userDto = this.userMapper.mapUserToUserDto(user);
    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(userDto);
  }

}
