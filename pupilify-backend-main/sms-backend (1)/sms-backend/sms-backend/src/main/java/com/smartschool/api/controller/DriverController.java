package com.smartschool.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartschool.api.entity.Driver;
import com.smartschool.api.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/drivers")
@CrossOrigin("*")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @PostMapping("/{schoolId}/create")
    public ResponseEntity<Driver> createDriver(@PathVariable Long schoolId,
                                               @RequestParam("driver") String driverJson,
                                               @RequestParam Long busId,
                                               @RequestParam(value = "aadharCardPhoto", required = false) MultipartFile aadharCardPhoto,
                                               @RequestParam(value = "drivingLicensePhoto", required = false) MultipartFile drivingLicensePhoto,
                                               @RequestParam(value = "bankPassbookPhoto", required = false) MultipartFile bankPassbookPhoto) throws IOException {
        Driver driver = new ObjectMapper().readValue(driverJson, Driver.class);
        return ResponseEntity.ok(driverService.createDriver(schoolId, driver, busId, aadharCardPhoto, drivingLicensePhoto, bankPassbookPhoto));
    }

    @GetMapping("/{schoolId}/{driverId}")
    public ResponseEntity<Driver> getDriverById(@PathVariable Long schoolId, @PathVariable Long driverId) {
        return ResponseEntity.ok(driverService.getDriverById(schoolId, driverId));
    }

    @GetMapping("/school/{schoolId}")
    public ResponseEntity<List<Driver>> getDriversBySchool(@PathVariable Long schoolId) {
        return ResponseEntity.ok(driverService.getDriversBySchool(schoolId));
    }

    @PutMapping("/{schoolId}/update/{driverId}")
    public ResponseEntity<Driver> updateDriver(@PathVariable Long schoolId,
                                               @PathVariable Long driverId,
                                               @RequestParam("driver") String driverJson,
                                               @RequestParam(value = "aadharCardPhoto", required = false) MultipartFile aadharCardPhoto,
                                               @RequestParam(value = "drivingLicensePhoto", required = false) MultipartFile drivingLicensePhoto,
                                               @RequestParam(value = "bankPassbookPhoto", required = false) MultipartFile bankPassbookPhoto) throws IOException {
        Driver driverDetails = new ObjectMapper().readValue(driverJson, Driver.class);
        return ResponseEntity.ok(driverService.updateDriver(schoolId, driverId, driverDetails, aadharCardPhoto, drivingLicensePhoto, bankPassbookPhoto));
    }

    @DeleteMapping("/{schoolId}/delete/{driverId}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long schoolId, @PathVariable Long driverId) {
        driverService.deleteDriver(schoolId, driverId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{schoolId}/{driverId}/assign-bus/{busId}")
    public ResponseEntity<Driver> assignBus(@PathVariable Long schoolId, @PathVariable Long driverId, @PathVariable Long busId) {
        return ResponseEntity.ok(driverService.assignBus(schoolId, driverId, busId));
    }

    @PutMapping("/remove-from-bus/{driverId}")
    public ResponseEntity<Driver> removeFromBus(@PathVariable Long driverId) {
        return ResponseEntity.ok(driverService.removeFromBus(driverId));
    }
}