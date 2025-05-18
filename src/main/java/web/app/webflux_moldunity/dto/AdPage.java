package web.app.webflux_moldunity.dto;

import web.app.webflux_moldunity.entity.ad.Ad;

import java.util.List;

public record AdPage(
        List<Ad> ads,
        Long total,
        Long page
){}
