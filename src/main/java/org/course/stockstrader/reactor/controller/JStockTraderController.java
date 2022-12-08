package org.course.stockstrader.reactor.controller;

import org.course.stockstrader.coroutines.domain.Stock;
import org.course.stockstrader.coroutines.domain.StockQuoteDto;
import org.course.stockstrader.reactor.domain.StockBuilder;
import org.course.stockstrader.reactor.repository.JStocksRepository;
import org.course.stockstrader.reactor.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@RestController
public class JStockTraderController {
    private final List<JExchangeService> exchanges;
    private final JStocksRepository stockRepository;


    @Autowired
    public JStockTraderController(
            JStocksRepository stockRepository,
            JExchangeServiceNasdaq exchangeServiceNasdaq,
            JExchangeServiceEuronext exchangeServiceEuronext,
            JExchangeServiceSix exchangeServiceSix) {
        this.exchanges = Arrays.asList(exchangeServiceEuronext, exchangeServiceNasdaq, exchangeServiceSix);
        this.stockRepository = stockRepository;
    }


    /**
     * Challenge 7 - Exercise A
     * For instructions go to @see StockTraderController#getStock
     */
    @GetMapping("/jstocks/{stock-id}")
    @ResponseBody
    public Mono<Stock> getStock(@PathVariable("stock-id") Long id) {
        return stockRepository.findById(id);
    }


    /**
     * Challenge 7 - Exercise B
     * For instructions go to @see StockTraderController#stockBySymbol
     */
    @GetMapping("/jstock")
    @ResponseBody
    public Mono<Stock> stockBySymbol(@RequestParam("symbol") String symbol) {
        return stockRepository.findBySymbol(symbol);
    }



    /**
     * Challenge 7 - Exercise C
     * For instructions go to @see StockTraderController#upsertStock
     */
    @PostMapping("/jstocks")
    @ResponseBody
    public Mono<Stock> upsertStock(@RequestBody Stock stock) {
        if(stock.getId() == null)
            return stockRepository.save(stock);
        return stockRepository.findById(stock.getId())
                .map(found -> StockBuilder.from(found).withPrice(stock.getPrice()).build())
                .switchIfEmpty(Mono.just(stock))
                .flatMap(stockRepository::save);
    }

    /**
     * Challenge 7 - Exercise D
     * For instructions go to @see StockTraderController#bestQuote
     */
    @GetMapping("/jstocks/quote")
    @ResponseBody
    public Mono<StockQuoteDto> bestQuote(@RequestParam("symbol") String symbol) {
        return stockRepository.findBySymbol(symbol).flatMap(stock ->
                Flux.fromIterable(this.exchanges).flatMap(service ->
                        service.getStockQuote(stock.getSymbol())).collectList().flatMap(quotes ->
                        Mono.justOrEmpty(quotes.stream().min(Comparator.comparing(StockQuoteDto::getCurrentPrice)))))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find stock with symbol=" + symbol)));
    }


    /**
     * Challenge 8 - Exercise A
     * For instructions go to @see StockTraderController#getStocks
     */
    @GetMapping("/jstocks")
    @ResponseBody
    public Flux<Stock> getStocks() {
        return stockRepository.findAll();
    }


}


