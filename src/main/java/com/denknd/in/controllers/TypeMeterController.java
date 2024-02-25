package com.denknd.in.controllers;

import com.denknd.audit.api.AuditRecording;
import com.denknd.dto.AddressDto;
import com.denknd.dto.TypeMeterDto;
import com.denknd.exception.TypeMeterAdditionException;
import com.denknd.mappers.TypeMeterMapper;
import com.denknd.services.TypeMeterService;
import com.denknd.swagger.RespBadRequest;
import com.denknd.swagger.RespForbidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.util.Set;

/**
 * Контроллер для управления типами показаний.
 */
@RestController
@RequestMapping("/meter-types")
@RequiredArgsConstructor
@Tag(
        name = "TypeMeterController",
        description = "Получение информации о всех типах показаний счетчика"
)
public class TypeMeterController {
  private final TypeMeterService typeMeterService;
  private final TypeMeterMapper typeMeterMapper;

  /**
   * Метод добавляет новый тип показаний.
   *
   * @param typeMeterDto DTO объект с данными о типе показаний.
   * @return Добавленный тип показаний в виде DTO объекта.
   * @throws TypeMeterAdditionException Ошибка при добавлении нового типа показаний.
   * @throws AccessDeniedException      Ошибка доступа.
   */
  @AuditRecording("Добавляет новый тип показаний")
  @PostMapping
  @Operation(summary = "Добавление нового типа показаний счетчика (роли: ADMIN)")
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "201",
                  description = "Новый тип показаний добавлен успешно",
                  content = @Content(
                          mediaType = MediaType.APPLICATION_JSON_VALUE,
                          schema = @Schema(implementation = AddressDto.class))
          )
  })
  @RespBadRequest
  @RespForbidden
  public ResponseEntity<TypeMeterDto> addNewType(@RequestBody @Valid TypeMeterDto typeMeterDto){
    var typeMeter = this.typeMeterMapper.mapTypeMeterDtoToTypeMeter(typeMeterDto);
    var result = this.typeMeterService.addNewTypeMeter(typeMeter);
    var typeMeterDtoResult = this.typeMeterMapper.typeMeterToTypeMeterDto(result);
    return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(typeMeterDtoResult);
  }

  /**
   * Метод возвращает доступные на данный момент типы показаний.
   *
   * @return Доступные типы показаний в виде множества DTO объектов.
   */
  @GetMapping
  @AuditRecording("Отправляет информацию о доступных типах показаний")
  @Operation(summary = "Получение информации о всех типах показаний счетчика (роли: USER, ADMIN)")
  @ApiResponses(
          value = {
                  @ApiResponse(
                          responseCode = "200",
                          description = "Для Получения доступных типов показаний пользователю",
                          content = @Content(
                                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                                  array = @ArraySchema(schema = @Schema(implementation = AddressDto.class))))
          }
  )
  public ResponseEntity<Set<TypeMeterDto>> getTypeMeterCodes() {
    var typeMeter = this.typeMeterService.getTypeMeter();
    return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Set.copyOf(this.typeMeterMapper.typeMetersToTypeMetersDto(typeMeter)));
  }

}
