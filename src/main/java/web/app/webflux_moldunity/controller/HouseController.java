package web.app.webflux_moldunity.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import web.app.webflux_moldunity.entity.Ad;
import web.app.webflux_moldunity.service.AdService;
import web.app.webflux_moldunity.entity.Category;
import web.app.webflux_moldunity.entity.real_estate.House;
import web.app.webflux_moldunity.entity.real_estate.RealEstate;

@RestController
public class HouseController<T extends Category> {
    private final AdService adService;

    @Autowired
    public HouseController(AdService adService) {
        this.adService = adService;
    }

    @GetMapping(value = "/ads/house/{id}")
    public Mono<Ad> getAd(@PathVariable Long id){
        return null;
    }

    @PostMapping(value = "/house")
    public Mono<Ad> add(@RequestBody Ad ad) {
        return adService.save(ad, RealEstate.class, House.class);
    }

}
