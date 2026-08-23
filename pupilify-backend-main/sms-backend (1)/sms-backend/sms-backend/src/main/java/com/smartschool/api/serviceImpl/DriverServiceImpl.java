package com.smartschool.api.serviceImpl;

import com.smartschool.api.entity.Bus;
import com.smartschool.api.entity.Driver;
import com.smartschool.api.entity.Role;
import com.smartschool.api.entity.User;
import com.smartschool.api.repository.BusRepository;
import com.smartschool.api.repository.DriverRepository;
import com.smartschool.api.repository.UserRepository;
import com.smartschool.api.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class DriverServiceImpl implements DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private String getDriverUploadDir() {
        return uploadDir + "/drivers/";
    }

    @Override
    public Driver createDriver(Long schoolId, Driver driver, Long busId, MultipartFile aadharCardPhoto, MultipartFile drivingLicensePhoto, MultipartFile bankPassbookPhoto) throws IOException {
        if (driver.getPhoneNo() == null || driver.getPhoneNo().isEmpty()) {
            throw new RuntimeException("Phone number is required");
        }
        Bus bus = busRepository.findById(busId).orElseThrow(() -> new RuntimeException("Bus not found"));
        if (!bus.getSchool().getId().equals(schoolId)) {
            throw new RuntimeException("Bus does not belong to this school");
        }
        driver.setBus(bus);

        User user = new User();
        user.setUsername(driver.getEmail());
        user.setPassword(passwordEncoder.encode(driver.getPassword()));
        user.setRole(Role.ROLE_DRIVER);
        user.setSchool(bus.getSchool());
        user.setActive(true);
        driver.setUser(user);

        if (aadharCardPhoto != null && !aadharCardPhoto.isEmpty()) {
            driver.setAadharCardPhoto(saveFile(aadharCardPhoto, "AADHAR_"));
        }
        if (drivingLicensePhoto != null && !drivingLicensePhoto.isEmpty()) {
            driver.setDrivingLicensePhoto(saveFile(drivingLicensePhoto, "LICENSE_"));
        }
        if (bankPassbookPhoto != null && !bankPassbookPhoto.isEmpty()) {
            driver.setBankPassbookPhoto(saveFile(bankPassbookPhoto, "PASSBOOK_"));
        }

        return driverRepository.save(driver);
    }

    @Override
    public Driver getDriverById(Long schoolId, Long driverId) {
        Driver driver = driverRepository.findById(driverId).orElseThrow(() -> new RuntimeException("Driver not found"));
        if (driver.getBus() != null && !driver.getBus().getSchool().getId().equals(schoolId)) {
            throw new RuntimeException("Driver does not belong to this school");
        }
        return driver;
    }

    @Override
    public List<Driver> getDriversBySchool(Long schoolId) {
        return driverRepository.findByBusSchoolId(schoolId);
    }

    @Override
    public Driver updateDriver(Long schoolId, Long driverId, Driver driverDetails, MultipartFile aadharCardPhoto, MultipartFile drivingLicensePhoto, MultipartFile bankPassbookPhoto) throws IOException {
        Driver driver = getDriverById(schoolId, driverId);
        driver.setPhoneNo(driverDetails.getPhoneNo());
        driver.setAlternateNo(driverDetails.getAlternateNo());
        driver.setDob(driverDetails.getDob());
        driver.setAddress(driverDetails.getAddress());

        if (aadharCardPhoto != null && !aadharCardPhoto.isEmpty()) {
            driver.setAadharCardPhoto(saveFile(aadharCardPhoto, "AADHAR_"));
        }
        if (drivingLicensePhoto != null && !drivingLicensePhoto.isEmpty()) {
            driver.setDrivingLicensePhoto(saveFile(drivingLicensePhoto, "LICENSE_"));
        }
        if (bankPassbookPhoto != null && !bankPassbookPhoto.isEmpty()) {
            driver.setBankPassbookPhoto(saveFile(bankPassbookPhoto, "PASSBOOK_"));
        }

        return driverRepository.save(driver);
    }

    @Override
    public void deleteDriver(Long schoolId, Long driverId) {
        Driver driver = getDriverById(schoolId, driverId);
        User user = driver.getUser();
        if (user != null) {
            userRepository.delete(user);
        }
        driverRepository.delete(driver);
    }

    @Override
    public Driver assignBus(Long schoolId, Long driverId, Long busId) {
        Driver driver = getDriverById(schoolId, driverId);
        Bus bus = busRepository.findById(busId).orElseThrow(() -> new RuntimeException("Bus not found"));
        if (!bus.getSchool().getId().equals(schoolId)) {
            throw new RuntimeException("Bus does not belong to this school");
        }
        driver.setBus(bus);
        return driverRepository.save(driver);
    }

    @Override
    public Driver removeFromBus(Long driverId) {
        Driver driver = driverRepository.findById(driverId).orElseThrow(() -> new RuntimeException("Driver not found"));
        driver.setBus(null);
        return driverRepository.save(driver);
    }

    private String saveFile(MultipartFile file, String prefix) throws IOException {
        if (file == null || file.isEmpty()) return null;
        String fileName = prefix + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path dirPath = Paths.get(getDriverUploadDir());
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
        Path path = Paths.get(getDriverUploadDir() + fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }
}