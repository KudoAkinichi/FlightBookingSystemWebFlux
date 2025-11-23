package com.service;

import reactor.core.publisher.Mono;

public interface PNRGeneratorService {
    Mono<String> generateUniquePNR();
    boolean validatePNR(String pnr);
}