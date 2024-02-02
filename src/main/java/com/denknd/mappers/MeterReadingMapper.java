package com.denknd.mappers;

import com.denknd.dto.MeterReadingRequestDto;
import com.denknd.dto.MeterReadingResponseDto;
import com.denknd.entity.Address;
import com.denknd.entity.MeterReading;
import com.denknd.entity.TypeMeter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Маппер для показаний
 */
@Mapper
public interface MeterReadingMapper {
  MeterReadingMapper INSTANCE = Mappers.getMapper(MeterReadingMapper.class);

  /**
   * Преобразование MeterReadingRequestDto в MeterReading с указанием адреса и типа счетчика.
   * @param meterReadingRequestDto показания полученные от пользователя
   * @param address адрес, на который для показаний
   * @param typeMeter тип показаний
   * @return показания для пользователя
   */
  MeterReading mapMeterReadingRequestDtoToMeterReading(MeterReadingRequestDto meterReadingRequestDto, Address address, TypeMeter typeMeter);

  /**
   * Преобразование MeterReading в MeterReadingResponseDto
   * @param meterReading показания от сервиса
   * @return показания для пользователя
   */
  @Mapping(target = "addressId", source = "meterReading.address.addressId")
  @Mapping(target = "typeDescription", source = "meterReading.typeMeter.typeDescription")
  @Mapping(target = "metric", source = "meterReading.typeMeter.metric")
  @Mapping(target = "code", source = "meterReading.typeMeter.typeCode")
  MeterReadingResponseDto mapMeterReadingToMeterReadingResponseDto(MeterReading meterReading);

  /**
   * Преобразование списка MeterReading в список MeterReadingResponseDto
   * @param meterReadingList показания от сервиса
   * @return показания для пользователя
   */
  List<MeterReadingResponseDto> mapMeterReadingsToMeterReadingResponsesDto(List<MeterReading> meterReadingList);
}
