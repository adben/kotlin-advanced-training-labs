package org.course.stockstrader.reactor.service;

import org.course.stockstrader.coroutines.domain.StockQuoteDto;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

abstract public class JExchangeService {

    private final WebClient webClient;
    private final String exchangeId;

    public JExchangeService(String baseUrl, String exchangeId) {
        this.webClient = WebClient.create(baseUrl);
        this.exchangeId = exchangeId;
    }

    public Mono<StockQuoteDto> getStockQuote(String stockSymbol) {
        return
                webClient.get()
                        .uri("/quotes?symbol=" + stockSymbol + "&exchange=" + exchangeId)
                        .retrieve()
                        .bodyToMono(StockQuoteDto.class);
    }
}
