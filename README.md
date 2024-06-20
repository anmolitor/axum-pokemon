## TODO
- Das gleiche in Java (Spring Boot/Quarkus?)
- Stats von Pokemon holen ✅
- Stats auf Level 50 berechnen (ohne dvs/evs)
- Tackle mit festem Angriffs/Levelwert auf das Pokemon aus dem Request
- Regelmäßiges Benchmarken mit `wrk -t12 -c400 -d30s http://localhost:4000/bulbasaur` bzw. `wrk -t12 -c400 -d30s http://localhost:8080/bulbasaur`

## Erwartung

- Rust Performance > Graal VM (Garbage Collector) oder weniger Spikes
- Dev Experience ist bei Rust besser
- Type Safety
- Error Handling
- async/await vs Threadpool -> wenn Requests länger warten, limitiert das den Throughput?
- Compile Times?
- 
