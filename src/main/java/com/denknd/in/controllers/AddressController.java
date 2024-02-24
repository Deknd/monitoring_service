package com.denknd.in.controllers;

import com.denknd.audit.api.AuditRecording;
import com.denknd.dto.AddressDto;
import com.denknd.exception.AddressDatabaseException;
import com.denknd.mappers.AddressMapper;
import com.denknd.services.AddressService;
import com.denknd.swagger.RespBadRequest;
import com.denknd.swagger.RespForbidden;
import com.denknd.time.api.MeasureExecutionTime;
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
import java.util.List;
import java.util.Optional;

/**
 * Контроллер для работы с адресами.
 */
@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "AddressController",
        description = "Дает доступ к управлению адресами"
)
public class AddressController {
  /**
   * Сервис управления адресами.
   */
  private final AddressService addressService;
  /**
   * Маппер адресов.
   */
  private final AddressMapper addressMapper;

  /**
   * Добавляет адрес пользователю.
   *
   * @param addressDto адрес полученный от пользователя
   * @return возвращает добавленный адрес
   * @throws AddressDatabaseException ошибка при сохранении адреса в бд.
   * @throws AccessDeniedException    ошибка доступа к данным.
   */
  @Operation(summary = "Добавляет новый адрес(roles: USER)")
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "201",
                  description = "Адрес успешно добавлен",
                  content = @Content(
                          mediaType = MediaType.APPLICATION_JSON_VALUE,
                          schema = @Schema(implementation = AddressDto.class))
          )
  })
  @RespBadRequest
  @RespForbidden
  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @AuditRecording("Добавляет новый адрес")
  public ResponseEntity<AddressDto> addAddress(@RequestBody @Valid AddressDto addressDto)
          throws AddressDatabaseException, AccessDeniedException {
    var address = this.addressMapper.mapAddressDtoToAddress(addressDto);
    var result = this.addressService.addAddressByUser(address);
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(this.addressMapper.mapAddressToAddressDto(result));
  }

  /**
   * Возвращает адреса пользователю, по его идентификатору.
   *
   * @param userId идентификатор пользователя
   * @return адреса доступные пользователю
   */
  @AuditRecording("Получения списка адресов")
  @MeasureExecutionTime
  @GetMapping
  @Operation(summary = "Получение списка адресов(roles: USER, ADMIN)")
  @ApiResponses(
          value = {
                  @ApiResponse(
                          responseCode = "200",
                          description = "Для Получения доступных адресов пользователю",
                          content = @Content(
                                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                                  array = @ArraySchema(schema = @Schema(implementation = AddressDto.class))))
          }
  )
  public ResponseEntity<List<AddressDto>> getAddress(
          @Parameter(description = "Идентификатор пользователя(роли: ADMIN)")
          @RequestParam("userId")
          Optional<Long> userId) {
    var addressList = this.addressService.getAddresses(userId);
    var addressDtoList = this.addressMapper.mapAddressesToAddressesDto(addressList);
    return ResponseEntity.status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(addressDtoList);
  }
}
