package com.smartschool.api.service;

import com.smartschool.api.entity.Driver;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface DriverService {
    Driver createDriver(Long schoolId, Driver driver, Long busId, MultipartFile aadharCardPhoto, MultipartFile drivingLicensePhoto, MultipartFile bankPassbookPhoto) throws IOException;
    Driver getDriverById(Long schoolId, Long driverId);
    List<Driver> getDriversBySchool(Long schoolId);
    Driver updateDriver(Long schoolId, Long driverId, Driver driverDetails, MultipartFile aadharCardPhoto, MultipartFile drivingLicensePhoto, MultipartFile bankPassbookPhoto) throws IOException;
    void deleteDriver(Long schoolId, Long driverId);
    Driver assignBus(Long schoolId, Long driverId, Long busId);
    Driver removeFromBus(Long driverId);
}