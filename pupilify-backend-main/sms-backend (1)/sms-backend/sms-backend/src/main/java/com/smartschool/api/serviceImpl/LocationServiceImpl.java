package com.smartschool.api.serviceImpl;

import com.smartschool.api.dto.BusLocationResponse;
import com.smartschool.api.dto.LocationUpdateRequest;
import com.smartschool.api.entity.Bus;
import com.smartschool.api.entity.BusLiveLocation;
import com.smartschool.api.entity.RouteStoppage;
import com.smartschool.api.entity.StudentTransportMapping;
import com.smartschool.api.repository.BusLiveLocationRepository;
import com.smartschool.api.repository.BusRepository;
import com.smartschool.api.repository.StudentTransportMappingRepository;
import com.smartschool.api.service.LocationService;
import com.smartschool.api.util.LocationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class LocationServiceImpl implements LocationService {

    @Autowired private BusRepository busRepository;
    @Autowired private BusLiveLocationRepository liveLocationRepository;
    @Autowired private StudentTransportMappingRepository mappingRepository;

    @Override
    @Transactional
    public BusLiveLocation toggleLocationTracking(Long busId, boolean isActive) {
        BusLiveLocation location = liveLocationRepository.findById(busId)
            .orElseGet(() -> {
                Bus bus = busRepository.findById(busId).orElseThrow(() -> new RuntimeException("Bus not found with ID: " + busId));
                BusLiveLocation newLocation = new BusLiveLocation();
                newLocation.setBus(bus);
                return newLocation;
            });

        location.setTrackingActive(isActive);
        log.info("Bus ID {} tracking toggled to: {}", busId, isActive);
        return liveLocationRepository.save(location);
    }

    @Override
    @Transactional
    public BusLiveLocation updateBusLocation(Long busId, LocationUpdateRequest request) {
        BusLiveLocation location = liveLocationRepository.findById(busId)
            .orElseThrow(() -> new RuntimeException("Bus location tracking not initialized. Please toggle on first."));

        if (!location.isTrackingActive()) {
            log.warn("Received location update for busId {} but tracking is inactive. Ignoring.", busId);
            return location;
        }

        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setSpeed(request.getSpeed());

        BusLiveLocation updatedLocation = liveLocationRepository.save(location);

        // Asynchronously check for proximity and notify students
        checkProximityAndNotify(updatedLocation);

        return updatedLocation;
    }

    @Override
    public List<BusLiveLocation> getActiveBusesForSchool(Long schoolId) {
        return liveLocationRepository.findAllByBus_School_IdAndIsTrackingActiveTrue(schoolId);
    }

    @Override
    public BusLocationResponse getStudentBusLocation(Long studentId) {
        Optional<StudentTransportMapping> mappingOpt = mappingRepository.findByStudentId(studentId);
        
        if (mappingOpt.isEmpty()) {
            BusLocationResponse response = new BusLocationResponse();
            response.setTrackingActive(false);
            response.setMessage("Student not assigned to any bus route.");
            return response;
        }
        
        Long busId = mappingOpt.get().getRoute().getBus().getId();

        return liveLocationRepository.findById(busId)
            .map(location -> {
                if (!location.isTrackingActive()) {
                    return BusLocationResponse.offline();
                }
                BusLocationResponse response = new BusLocationResponse();
                response.setTrackingActive(true);
                response.setLatitude(location.getLatitude());
                response.setLongitude(location.getLongitude());
                response.setLastUpdated(location.getLastUpdated());
                response.setMessage("Location updated successfully.");
                return response;
            })
            .orElse(BusLocationResponse.offline());
    }

    private void checkProximityAndNotify(BusLiveLocation liveLocation) {
        Long busId = liveLocation.getBusId();
        List<StudentTransportMapping> mappings = mappingRepository.findAllByStoppage_Route_Bus_Id(busId);

        for (StudentTransportMapping mapping : mappings) {
            RouteStoppage stoppage = mapping.getStoppage();
            double distance = LocationUtil.calculateDistance(
                liveLocation.getLatitude(), liveLocation.getLongitude(),
                stoppage.getLatitude(), stoppage.getLongitude()
            );

            // Check if the bus is within 1.0 KM of the student's stop
            if (distance < 1.0) {
                log.info("SIMULATING PUSH NOTIFICATION: Bus for student {} is nearby stoppage '{}'. Distance: {} KM",
                    mapping.getStudent().getId(), stoppage.getStopName(), String.format("%.2f", distance));
                // In a real application, you would integrate with a push notification service like Firebase here.
            }
        }
    }
}