package com.denknd.in.controllers;

import com.denknd.audit.api.AuditRecording;
import com.denknd.dto.MeterReadingRequestDto;
import com.denknd.dto.MeterReadingResponseDto;
import com.denknd.entity.Address;
import com.denknd.entity.MeterReading;
import com.denknd.entity.Parameters;
import com.denknd.entity.TypeMeter;
import com.denknd.exception.MeterReadingConflictError;
import com.denknd.mappers.MeterReadingMapper;
import com.denknd.services.MeterReadingService;
import com.denknd.swagger.RespConflict;
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
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Контроллер для работы с показаниями счетчиков.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/meter-readings")
@Slf4j
@Tag(
        name = "MeterReadingController",
        description = "Ендпоинты для работы с показаниями счетчиков"
)
public class MeterReadingController {

  private final MeterReadingService meterReadingService;
  private final MeterReadingMapper meterReadingMapper;

  /**
   * Возвращает историю показаний по указанным данным.
   * Если addressId == null, то выведется история по всем адресам записанных на пользователя.
   * Выводится история по указанным типам {@param typeMeterIds}, если ничего не передавать,
   * то вернется история по всем доступным типам. Если указан startDate,
   * то выводится список от этой даты, иначе ограничений нет.
   * Если указан endDate, то выводится список до этой даты, иначе по текущею дату.
   *
   * @param addressId    Идентификатор адреса, по которому запрашивается история показаний (может быть null).
   * @param userId       Идентификатор пользователя, для которого запрашивается история показаний (не может быть null).
   * @param typeMeterIds Идентификаторы типов показаний, для которых запрашивается история (может быть null).
   * @param startDate    Начальная дата истории показаний.
   * @param endDate      Конечная дата истории показаний.
   * @return Список DTO объектов с показаниями.
   */
  @GetMapping("/history")
  @AuditRecording("Получения истории о показаниях")
  @Operation(summary = "Получения истории о показаниях(roles: USER, ADMIN)")
  @ApiResponses(
          value = {
                  @ApiResponse(
                          responseCode = "200",
                          description = "Для Получения истории о показаниях по переданным параметрам",
                          content = @Content(
                                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                                  array = @ArraySchema(schema = @Schema(implementation = MeterReadingResponseDto.class))))
          }
  )
  @MeasureExecutionTime
  public ResponseEntity<List<MeterReadingResponseDto>> getHistoryMeterReading(
          @Parameter(description = " идентификатор адреса. Если не передан, выведется история по всем доступным пользователю адресам(roles: USER, ADMIN).")
          @RequestParam("addrId") Optional<Long> addressId,
          @Parameter(description = "покажет историю показаний для указанного пользователя.(roles: ADMIN).")
          @RequestParam("userId") Optional<Long> userId,
          @Parameter(description = "типы показаний, по которым нужна история (пример: typeId=1,2,3). Если не передан, выведется история по всем типам(roles: USER, ADMIN).",
                  content = @Content(schema = @Schema(implementation = String.class, format = "1,2,3")))
          @RequestParam("typeIds") Optional<Set<Long>> typeMeterIds,
          @Parameter(description = "дата, от которой нужна история (паттерн: yyyy-MM). Если не передан, выведется история с начала подачи показаний(roles: USER, ADMIN).",
                  content = @Content(schema = @Schema(implementation = String.class, format = "yyyy-MM")))
          @RequestParam("start_date") Optional<YearMonth> startDate,
          @Parameter(description = "дата, по которую нужна история (паттерн: yyyy-MM). Если не передан, выведется история по последним показаниям(roles: USER, ADMIN).",
                  content = @Content(schema = @Schema(implementation = String.class, format = "yyyy-MM")))
          @RequestParam("end_date") Optional<YearMonth> endDate
  ) {
    var buildParametersForHistory = Parameters.builder()
            .addressId(addressId.orElse(null))
            .userId(userId.orElse(null))
            .typeMeterIds(typeMeterIds.orElse(null))
            .startDate(startDate.orElse(null))
            .endDate(endDate.orElse(null))
            .build();
    var historyMeterByAddress
            = this.meterReadingService.getHistoryMeterByAddress(
            buildParametersForHistory);
    var meterReadingResponseDtos
            = this.meterReadingMapper.mapMeterReadingsToMeterReadingResponsesDto(historyMeterByAddress);
    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(meterReadingResponseDtos);
  }

  /**
   * Метод добавляет новые показания счетчика.
   *
   * @param meterReadingRequestDto DTO объект с данными показаний для сохранения.
   * @return DTO объект с сохраненными показаниями.
   * @throws MeterReadingConflictError Ошибка при конфликте при сохранении показаний.
   * @throws AccessDeniedException     Ошибка доступа.
   */
  @PostMapping
  @AuditRecording("Добавляет новые показания")
  @Operation(summary = "Отправка показаний счетчика (роли: USER)")
  @ApiResponses(
          value = {
                  @ApiResponse(
                          responseCode = "201",
                          description = "Для отправления показаний счетчика",
                          content = @Content(
                                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                                  schema = @Schema(implementation = MeterReadingResponseDto.class)))
          }
  )
  @RespForbidden
  @RespConflict
  public ResponseEntity<MeterReadingResponseDto> addMeterReadingValue(
          @RequestBody @Valid MeterReadingRequestDto meterReadingRequestDto
  ) throws MeterReadingConflictError, AccessDeniedException {
    var meterReading = this.meterReadingMapper
            .mapMeterReadingRequestDtoToMeterReading(
                    meterReadingRequestDto,
                    Address.builder().addressId(meterReadingRequestDto.addressId()).build(),
                    TypeMeter.builder().typeMeterId(meterReadingRequestDto.typeMeterId()).build());
    var resultMeterReading = this.meterReadingService.addMeterValue(meterReading);
    var meterReadingResponseDto = this.meterReadingMapper.mapMeterReadingToMeterReadingResponseDto(resultMeterReading);
    return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(meterReadingResponseDto);
  }

  /**
   * Показывает показания по полученным данным. Если не указана date,
   * то вернутся актуальные показания на текущую дату.
   * Если адрес не указан, то вернутся показания по всем адресам доступным пользователю.
   * Если не указаны типы показаний, по которым нужны данные, то вернутся данные по всем доступным показаниям.
   *
   * @param addressId    Идентификатор адреса, для которого запрашиваются показания (может быть null).
   * @param userId       Идентификатор пользователя, для которого запрашиваются показания (не может быть null).
   * @param typeMeterIds Идентификаторы типов показаний, для которых запрашиваются данные (может быть null).
   * @param date         Дата, на которую запрашиваются актуальные показания (может быть null).
   * @return Список DTO объектов с актуальными показаниями.
   */
  @GetMapping
  @AuditRecording("Получаем актуальные показания")
  @Operation(summary = "Получение актуальных показаний счетчика (роли: USER, ADMIN)")
  @ApiResponses(
          value = {
                  @ApiResponse(
                          responseCode = "200",
                          description = "Для Получения актуальных показаний по переданным параметрам",
                          content = @Content(
                                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                                  array = @ArraySchema(schema = @Schema(implementation = MeterReadingResponseDto.class))))
          }
  )
  @MeasureExecutionTime
  public ResponseEntity<List<MeterReadingResponseDto>> getMeterReadings(
          @Parameter(description = " идентификатор адреса. Если не передан, выведется история по всем доступным пользователю адресам(roles: USER, ADMIN).")
          @RequestParam("addrId") Optional<Long> addressId,
          @Parameter(description = "покажет историю показаний для указанного пользователя.(roles: ADMIN).")
          @RequestParam("userId") Optional<Long> userId,
          @Parameter(description = " типы показаний, по которым нужны показания (пример: typeId=1,2,3). Если не передан, будут выведены показания по всем типам показаний(roles: USER, ADMIN)",
                  content = @Content(schema = @Schema(implementation = String.class, format = "1,2,3")))
          @RequestParam("typeIds") Optional<Set<Long>> typeMeterIds,
          @Parameter(description = "если параметр передан, будут выведены показания именно по этой дате, а не актуальные (паттерн: yyyy-MM)(roles: USER, ADMIN).",
                  content = @Content(schema = @Schema(implementation = String.class, format = "yyyy-MM")))
          @RequestParam("date") Optional<YearMonth> date) {
    var parameters = Parameters.builder()
            .addressId(addressId.orElse(null))
            .userId(userId.orElse(null))
            .typeMeterIds(typeMeterIds.orElse(null))
            .date(date.orElse(null))
            .build();
    List<MeterReading> actualMeterByAddress = this.meterReadingService.getActualMeterByAddress(parameters);
    var meterReadingResponseDtos = this.meterReadingMapper.mapMeterReadingsToMeterReadingResponsesDto(actualMeterByAddress);
    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(meterReadingResponseDtos);
  }

}
