package com.smartschool.api.controller;

import com.smartschool.api.dto.BusLocationResponse;
import com.smartschool.api.dto.LocationUpdateRequest;
import com.smartschool.api.entity.BusLiveLocation;
import com.smartschool.api.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transport/location")
@CrossOrigin("*")
public class LocationController {

    @Autowired
    private LocationService locationService;

    // Endpoint for Driver's App
    @PostMapping("/toggle/{busId}")
    public ResponseEntity<BusLiveLocation> toggleTracking(@PathVariable Long busId, @RequestParam boolean active) {
        return ResponseEntity.ok(locationService.toggleLocationTracking(busId, active));
    }

    // Endpoint for Driver's App
    @PostMapping("/update/{busId}")
    public ResponseEntity<BusLiveLocation> updateLocation(@PathVariable Long busId, @RequestBody LocationUpdateRequest request) {
        return ResponseEntity.ok(locationService.updateBusLocation(busId, request));
    }

    // Endpoint for Principal's Portal
    @GetMapping("/all/{schoolId}")
    public ResponseEntity<List<BusLiveLocation>> getAllActiveBusLocations(@PathVariable Long schoolId) {
        return ResponseEntity.ok(locationService.getActiveBusesForSchool(schoolId));
    }

    // Endpoint for Student's App
    @GetMapping("/student/{studentId}")
    public ResponseEntity<BusLocationResponse> getStudentBusLocation(@PathVariable Long studentId) {
        return ResponseEntity.ok(locationService.getStudentBusLocation(studentId));
    }
}