package com.springmad.bilabonnement.controller;

import com.springmad.bilabonnement.model.Bil;
import com.springmad.bilabonnement.model.Kunde;
import com.springmad.bilabonnement.service.BilService;
import com.springmad.bilabonnement.service.ForretningService;
import com.springmad.bilabonnement.service.KundeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

// ===== @RestController =====
// @RestController er en annotation der kombinerer @Controller og @ResponseBody.
// Forskellen paa @Controller og @RestController:
//
//   @Controller    -> returnerer et VIEW (HTML-side via Thymeleaf)
//   @RestController -> returnerer DATA (JSON) direkte til browseren
//
// Huskeregl: "Controller viser sider, RestController giver data."
//
// @RestController bruges typisk naar:
//   - En frontend (fx React, JavaScript) skal hente data fra serveren
//   - Man vil lave et REST API
//   - Man vil returnere JSON i stedet for HTML
//
// I dette projekt bruger vi @RestController til at give
// et simpelt API der returnerer data som JSON.
@RestController
@RequestMapping("/api")
public class ApiController {

    // Controlleren taler kun med services (Controller -> Service -> Repository).
    @Autowired
    private BilService bilService;

    @Autowired
    private KundeService kundeService;

    @Autowired
    private ForretningService forretningService;

    // Endpoint: GET /api/biler
    // Returnerer alle biler som JSON (ikke HTML).
    // Browseren faar fx: [{"id":1,"navn":"Toyota Yaris","aar":2022,...}, ...]
    @GetMapping("/biler")
    public List<Bil> alleBiler() {
        return bilService.findAll();
    }

    // Endpoint: GET /api/kunder
    // Returnerer alle kunder som JSON.
    @GetMapping("/kunder")
    public List<Kunde> alleKunder() {
        return kundeService.findAll();
    }

    // Endpoint: GET /api/dashboard
    // Returnerer KPI-data som JSON (HashMap med key-value par).
    // Browseren faar fx: {"antalAktive":5,"samletPris":14995.00,"statusFordeling":{"AKTIV":5,"AFSLUTTET":3}}
    @GetMapping("/dashboard")
    public HashMap<String, Object> dashboardData() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("antalAktive", forretningService.antalAktiveUdlejninger());
        data.put("samletPris", forretningService.samletMaanedligPrisAktive());
        data.put("statusFordeling", forretningService.antalAbonnementerPerStatus());
        return data;
    }
}
