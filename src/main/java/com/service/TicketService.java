package com.service;

import com.dto.response.TicketResponse;
import reactor.core.publisher.Mono;

public interface TicketService {
    Mono<TicketResponse> getTicketByPnr(String pnr);
    Mono<byte[]> downloadTicketPdf(String pnr);
    Mono<String> resendTicketEmail(String pnr);
}