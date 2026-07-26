package com.smartschool.api.service;

import com.smartschool.api.dto.BusLocationResponse;
import com.smartschool.api.dto.LocationUpdateRequest;
import com.smartschool.api.entity.BusLiveLocation;
import java.util.List;

public interface LocationService {

    // For the Driver App
    BusLiveLocation toggleLocationTracking(Long busId, boolean isActive);
    BusLiveLocation updateBusLocation(Long busId, LocationUpdateRequest request);

    // For the Principal Portal
    List<BusLiveLocation> getActiveBusesForSchool(Long schoolId);

    // For the Student App
    BusLocationResponse getStudentBusLocation(Long studentId);
}