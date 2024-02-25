package com.denknd.in.controllers;

import com.denknd.aspectj.audit.AuditRecording;
import com.denknd.dto.CounterInfoDto;
import com.denknd.dto.MeterDto;
import com.denknd.mappers.MeterCountMapper;
import com.denknd.services.MeterCountService;
import com.denknd.swagger.RespBadRequest;
import com.denknd.swagger.RespForbidden;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;

/**
 * Контроллер по управлению информацией о счетчиках
 */
@RestController
@RequestMapping("/counter-info")
@RequiredArgsConstructor
@Tag(
        name = "CounterInfoController",
        description = "Для добавления информации о счетчике"
)
public class CounterInfoController {
  /**
   * Сервис для работы с информацией по счетчикам
   */
  private final MeterCountService meterCountService;
  /**
   * Маппер для преобразования объекта {@link com.denknd.entity.Meter}
   */
  private final MeterCountMapper meterCountMapper;

  /**
   * Метод для добавления информации о счетчике
   *
   * @param counterInfoDto информация для обновления информации о счетчиках
   * @return возвращает информацию о счетчике, в случае успешного обновления
   * @throws SQLException          ошибка при возникновении
   * @throws AccessDeniedException ошибка доступа к данным
   */
  @Operation(summary = "Добавляет информацию о счетчике(роли: ADMIN)")
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "200",
                  description = "Адрес успешно добавлен",
                  content = @Content(
                          mediaType = MediaType.APPLICATION_JSON_VALUE,
                          schema = @Schema(implementation = MeterDto.class))
          )
  })
  @RespBadRequest
  @RespForbidden
  @PutMapping
  @AuditRecording("Добавляет дополнительную информацию о счетчиках")
  public ResponseEntity<MeterDto> addInfoForMeter(@Valid CounterInfoDto counterInfoDto)
          throws SQLException, AccessDeniedException {
    var meter = this.meterCountMapper.mapCounterInfoDtoToMeter(counterInfoDto);
    var result = this.meterCountService.addInfoForMeterCount(meter);
    var meterDto = this.meterCountMapper.mapMeterToMeterDto(result);
    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(meterDto);
  }
}
