package web.app.webflux_moldunity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import web.app.webflux_moldunity.entity.Ad;
import web.app.webflux_moldunity.entity.transport.Car;
import web.app.webflux_moldunity.entity.transport.Transport;
import web.app.webflux_moldunity.service.AdService;

@RestController
public class CarController {
    private final AdService adService;

    @Autowired
    public CarController(AdService adService) {
        this.adService = adService;
    }

    @GetMapping(value = "/ads/car/{id}")
    public Mono<Ad> getAd(@PathVariable Long id){
        return null;
    }

    @PostMapping(value = "/car")
    public Mono<Ad> add(@RequestBody Ad ad) {
        return adService.save(ad, Transport.class, Car.class);
    }
}
