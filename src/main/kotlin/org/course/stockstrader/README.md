### Stocks Trader 

#### API Examples
- Get all Stocks
```
curl http://localhost:8081/stocks
```
- Get Stock
```
curl http://localhost:8081/stocks/0
curl http://localhost:8081/stocks?symbol=GOOG
```

- Upsert Stock
```
curl -X POST -H "Content-Type: application/json" -d '{"id":null,"symbol":"GOOG","price":125.6}'  http://localhost:8081/stocks/
```

- Request best quote from various stock exchanges
```
curl http://localhost:8081/stocks/quote?symbol=GOOG
```

- Open a stream for stocks changes
```
curl http://localhost:8081/stocks/stream1?offset=1
```

