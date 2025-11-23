package com.service;

import com.dto.request.InventoryRequest;
import com.dto.response.ApiResponse;
import reactor.core.publisher.Mono;

public interface InventoryService {
    Mono<ApiResponse<String>> addFlightInventory(InventoryRequest request);
    Mono<ApiResponse<String>> updateFlightInventory(String inventoryId, InventoryRequest request);
}