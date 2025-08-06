package com.hulkhire.payments.utils.converters;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.hulkhire.payments.DTO.TransactionDTO;
import com.hulkhire.payments.constants.PaymentMethodEnum;
import com.hulkhire.payments.constants.PaymentTypeEnum;
import com.hulkhire.payments.constants.ProviderEnum;
import com.hulkhire.payments.constants.TxnStatusEnum;
import com.hulkhire.payments.entity.TransactionEntity;

@Mapper(componentModel = "spring", uses = { PaymentMethodEnum.class, PaymentTypeEnum.class, ProviderEnum.class, TxnStatusEnum.class })
public interface TransactionMapper {

    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Mapping(source = "providerId", target = "provider", qualifiedByName = "mapProvider")
    @Mapping(source = "txnStatusId", target = "txnStatus", qualifiedByName = "mapTxnStatus")
    @Mapping(source = "paymentTypeId", target = "paymentType", qualifiedByName = "mapPaymentType")
    @Mapping(source = "paymentMethodId", target = "paymentMethod", qualifiedByName = "mapPaymentMethod")
    TransactionDTO toDTO(TransactionEntity entity);

    List<TransactionDTO> toDTOList(List<TransactionEntity> entities);
    
    @Mapping(source = "txnStatus", target = "txnStatusId", qualifiedByName = "mapTxnStatusToId")
    
    TransactionEntity toEntity(TransactionDTO dto);


    @Named("mapPaymentMethod")
    default String mapPaymentMethod(Integer code) {
        return PaymentMethodEnum.fromId(code);
    }

    @Named("mapProvider")
    default String mapProvider(Integer code) {
        return ProviderEnum.fromId(code);
    }

    @Named("mapPaymentType")
    default String mapPaymentType(Integer code) {
    	
        return PaymentTypeEnum.fromId(code);
    }

    @Named("mapTxnStatus")
    default String mapTxnStatus(Integer code) {
        return TxnStatusEnum.fromId(code);
    }
    @Named("mapTxnStatusToId")
    default Integer mapTxnStatusToId(String name) {
        return TxnStatusEnum.fromName(name);
    }
}
