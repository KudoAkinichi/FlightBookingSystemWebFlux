package com.service.impl;

import com.repository.BookingRepository;
import com.service.PNRGeneratorService;
import com.util.PNRGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class PNRGeneratorServiceImpl implements PNRGeneratorService {

    private final BookingRepository bookingRepository;

    @Override
    public Mono<String> generateUniquePNR() {
        return Mono.defer(() -> {
            String pnr = PNRGenerator.generatePNR();

            return bookingRepository.existsByPnr(pnr)
                    .flatMap(exists -> {
                        if (Boolean.TRUE.equals(exists)) {
                            // If PNR exists, generate a new one recursively
                            log.debug("PNR {} already exists, generating new one", pnr);
                            return generateUniquePNR();
                        }
                        log.debug("Generated unique PNR: {}", pnr);
                        return Mono.just(pnr);
                    });
        });
    }

    @Override
    public boolean validatePNR(String pnr) {
        return PNRGenerator.isValidPNR(pnr);
    }
}