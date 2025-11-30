package com.fliqo.service.dto.request;

import lombok.Builder;

@Builder
public record FindStoreDetailCommand(String storeName, String rgstPk) {}
