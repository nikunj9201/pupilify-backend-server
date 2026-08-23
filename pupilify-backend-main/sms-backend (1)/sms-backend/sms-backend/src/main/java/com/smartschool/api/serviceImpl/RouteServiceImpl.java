package com.smartschool.api.serviceImpl;

import com.smartschool.api.entity.Bus;
import com.smartschool.api.entity.Route;
import com.smartschool.api.entity.School;
import com.smartschool.api.entity.Stoppage;
import com.smartschool.api.repository.BusRepository;
import com.smartschool.api.repository.RouteRepository;
import com.smartschool.api.repository.SchoolRepository;
import com.smartschool.api.repository.StoppageRepository;
import com.smartschool.api.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteServiceImpl implements RouteService {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private StoppageRepository stoppageRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Override
    public Route createRoute(Long schoolId, Long busId, String routeName) {
        School school = schoolRepository.findById(schoolId).orElseThrow(() -> new RuntimeException("School not found"));
        Bus bus = busRepository.findById(busId).orElseThrow(() -> new RuntimeException("Bus not found"));
        if (!bus.getSchool().getId().equals(schoolId)) {
            throw new RuntimeException("Bus does not belong to this school");
        }
        Route route = new Route();
        route.setSchool(school);
        route.setBus(bus);
        route.setRouteName(routeName);
        return routeRepository.save(route);
    }

    @Override
    public Stoppage addStoppage(Long schoolId, Long routeId, String stopName) {
        Route route = routeRepository.findById(routeId).orElseThrow(() -> new RuntimeException("Route not found"));
        if (!route.getSchool().getId().equals(schoolId)) {
            throw new RuntimeException("Route does not belong to this school");
        }
        Stoppage stoppage = new Stoppage();
        stoppage.setRoute(route);
        stoppage.setStopName(stopName);
        return stoppageRepository.save(stoppage);
    }

    @Override
    public List<Route> getRoutesByBus(Long schoolId, Long busId) {
        // Only return active routes
        return routeRepository.findBySchoolIdAndBusIdAndActiveTrue(schoolId, busId);
    }

    @Override
    public List<Stoppage> getStoppagesByRoute(Long schoolId, Long routeId) {
        Route route = routeRepository.findById(routeId).orElseThrow(() -> new RuntimeException("Route not found"));
        if (!route.getSchool().getId().equals(schoolId)) {
            throw new RuntimeException("Route does not belong to this school");
        }
        // Only return active stoppages
        return stoppageRepository.findByRouteIdAndActiveTrue(routeId);
    }

    @Override
    public Stoppage updateStoppage(Long schoolId, Long stoppageId, String stopName) {
        Stoppage stoppage = stoppageRepository.findById(stoppageId).orElseThrow(() -> new RuntimeException("Stoppage not found"));
        if (!stoppage.getRoute().getSchool().getId().equals(schoolId)) {
            throw new RuntimeException("Stoppage does not belong to this school");
        }
        stoppage.setStopName(stopName);
        return stoppageRepository.save(stoppage);
    }

    @Override
    public void deleteStoppage(Long schoolId, Long stoppageId) {
        Stoppage stoppage = stoppageRepository.findById(stoppageId).orElseThrow(() -> new RuntimeException("Stoppage not found"));
        if (!stoppage.getRoute().getSchool().getId().equals(schoolId)) {
            throw new RuntimeException("Stoppage does not belong to this school");
        }
        // Soft-delete to avoid foreign key constraint violations
        stoppage.setActive(false);
        stoppageRepository.save(stoppage);
    }

    @Override
    public void deleteRoute(Long schoolId, Long routeId) {
        Route route = routeRepository.findById(routeId).orElseThrow(() -> new RuntimeException("Route not found"));
        if (!route.getSchool().getId().equals(schoolId)) {
            throw new RuntimeException("Route does not belong to this school");
        }
        // Soft-delete route and its stoppages to avoid FK constraint violations
        route.setActive(false);
        if (route.getStoppages() != null) {
            for (Stoppage s : route.getStoppages()) {
                s.setActive(false);
            }
        }
        routeRepository.save(route);
    }
}