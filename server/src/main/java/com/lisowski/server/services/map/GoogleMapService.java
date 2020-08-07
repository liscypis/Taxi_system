package com.lisowski.server.services.map;

import com.google.maps.DirectionsApi;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.*;
import com.lisowski.server.Utils.MapsKey;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GoogleMapService {
    public final GeoApiContext context;

    public GoogleMapService() {
        this.context = new GeoApiContext.Builder().apiKey(MapsKey.API_KEY).build();
    }

    public Long findClosestDriver(String origin, String[] arrayOfPositions) {
        System.out.println("destination " + origin);
        System.out.println("origins " + Arrays.toString(arrayOfPositions));
        String[] origins = new String[] {origin};
//        String[] origins  = new String[] {"50.874872,20.626861","50.887521,20.659298"};
//        String[] destinations = new String[] { "50.888859,20.645138" };

        try {
            DistanceMatrix matrix =
                    DistanceMatrixApi.getDistanceMatrix(this.context, arrayOfPositions, origins).await();
            System.out.println(matrix.toString());
            DistanceMatrixRow[] rows = matrix.rows;

            return findShortestTimeIndex(rows);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;

    }

    private long findShortestTimeIndex(DistanceMatrixRow[] rows) {
        long min = rows[0].elements[0].duration.inSeconds;
        long minIndex = 0;
        for(int i = 1; i < rows.length; i++) {
            if(rows[i].elements[0].duration.inSeconds < min){
                min = rows[i].elements[0].duration.inSeconds;
                minIndex = i;
            }
        }
        return minIndex;
    }

    public void getDirection(String origin, String destination, String waypoint) {
        System.out.println("Driver loc " + origin + " user loc " + waypoint + "user des " + destination);
        try {
            DirectionsResult result =
                    DirectionsApi.newRequest(this.context)
                            .units(Unit.METRIC)
                            .region("pl")
                            .origin(origin)
                            .waypoints(waypoint)
                            .destination(destination)
                            .await();
            GeocodedWaypoint[] geocodedWaypoint = result.geocodedWaypoints;
            DirectionsRoute[] directionsRoutes = result.routes;
            System.out.println(geocodedWaypoint.length);

            for (GeocodedWaypoint waypointStatus : geocodedWaypoint) {
                System.out.println(waypointStatus.toString());
            }
            System.out.println(Arrays.toString(directionsRoutes[0].waypointOrder));
            System.out.println(directionsRoutes[0].legs[0].steps.length);
            getPolylines(directionsRoutes[0]);
            System.out.println(directionsRoutes[0].overviewPolyline.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String[] getPolylines(DirectionsRoute directionsRoute) {
        List<LatLng> toUser = new ArrayList<LatLng>();
        List<LatLng> toDestination = new ArrayList<LatLng>();
        for (int i = 0; i < directionsRoute.legs[0].steps.length; i++) {
            toUser.addAll(directionsRoute.legs[0].steps[i].polyline.decodePath());
            toDestination.addAll(directionsRoute.legs[1].steps[i].polyline.decodePath());
        }
        EncodedPolyline toUserPolyline = new EncodedPolyline(toUser);
        EncodedPolyline toDestinationPolyline = new EncodedPolyline(toDestination);
        System.out.println("to user polyline " + toUserPolyline.getEncodedPath());
        System.out.println("to destination polyline " + toDestinationPolyline.getEncodedPath());

        return new String[] {toUserPolyline.getEncodedPath(), toDestinationPolyline.getEncodedPath()};
    }
}
