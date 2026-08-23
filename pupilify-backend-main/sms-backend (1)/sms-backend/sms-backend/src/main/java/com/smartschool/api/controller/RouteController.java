package com.smartschool.api.controller;

import com.smartschool.api.entity.Route;
import com.smartschool.api.entity.Stoppage;
import com.smartschool.api.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/routes")
@CrossOrigin("*")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @PostMapping("/{schoolId}/create")
    public ResponseEntity<Route> createRoute(@PathVariable Long schoolId, @RequestParam Long busId, @RequestParam String routeName) {
        return ResponseEntity.ok(routeService.createRoute(schoolId, busId, routeName));
    }

    @PostMapping("/{schoolId}/{routeId}/stoppages/add")
    public ResponseEntity<Stoppage> addStoppage(@PathVariable Long schoolId, @PathVariable Long routeId, @RequestParam String stopName) {
        return ResponseEntity.ok(routeService.addStoppage(schoolId, routeId, stopName));
    }

    @GetMapping("/{schoolId}/bus/{busId}")
    public ResponseEntity<List<Route>> getRoutesByBus(@PathVariable Long schoolId, @PathVariable Long busId) {
        return ResponseEntity.ok(routeService.getRoutesByBus(schoolId, busId));
    }

    @GetMapping("/{schoolId}/{routeId}/stoppages")
    public ResponseEntity<List<Stoppage>> getStoppagesByRoute(@PathVariable Long schoolId, @PathVariable Long routeId) {
        return ResponseEntity.ok(routeService.getStoppagesByRoute(schoolId, routeId));
    }

    @PutMapping("/{schoolId}/stoppages/{stoppageId}")
    public ResponseEntity<Stoppage> updateStoppage(@PathVariable Long schoolId, @PathVariable Long stoppageId, @RequestParam String stopName) {
        return ResponseEntity.ok(routeService.updateStoppage(schoolId, stoppageId, stopName));
    }

    @DeleteMapping("/{schoolId}/stoppages/{stoppageId}")
    public ResponseEntity<Void> deleteStoppage(@PathVariable Long schoolId, @PathVariable Long stoppageId) {
        routeService.deleteStoppage(schoolId, stoppageId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{schoolId}/{routeId}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long schoolId, @PathVariable Long routeId) {
        routeService.deleteRoute(schoolId, routeId);
        return ResponseEntity.ok().build();
    }
}