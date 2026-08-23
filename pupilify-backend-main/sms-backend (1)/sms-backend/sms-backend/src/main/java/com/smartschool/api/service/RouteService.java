package com.smartschool.api.service;

import com.smartschool.api.entity.Route;
import com.smartschool.api.entity.Stoppage;

import java.util.List;

public interface RouteService {
    Route createRoute(Long schoolId, Long busId, String routeName);
    Stoppage addStoppage(Long schoolId, Long routeId, String stopName, double fee);
    List<Route> getRoutesByBus(Long schoolId, Long busId);
    List<Stoppage> getStoppagesByRoute(Long schoolId, Long routeId);
    Stoppage updateStoppage(Long schoolId, Long stoppageId, String stopName, double fee);
    void deleteStoppage(Long schoolId, Long stoppageId);
    void deleteRoute(Long schoolId, Long routeId);
}