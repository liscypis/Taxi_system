package com.lisowski.server.services.map;

import com.google.maps.DirectionsApi;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.*;
import com.lisowski.server.DTO.response.RideDetailsResponse;
import com.lisowski.server.Utils.MapsKey;
import com.lisowski.server.models.Ride;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.lisowski.server.Utils.Price.calculatePrice;

@Service
public class GoogleMapService {
    public final GeoApiContext context;

    public GoogleMapService() {
        this.context = new GeoApiContext.Builder().apiKey(MapsKey.API_KEY).build();
    }

    public Long findClosestDriver(String origin, String[] arrayOfPositions) {
        System.out.println("destination " + origin);
        System.out.println("origins " + Arrays.toString(arrayOfPositions));
        String[] origins = new String[]{origin};
//        String[] origins  = new String[] {"50.874872,20.626861","50.887521,20.659298"};

        try {
            DistanceMatrix matrix =
                    DistanceMatrixApi.getDistanceMatrix(this.context, arrayOfPositions, origins).await();
//            System.out.println(matrix.toString());
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
        System.out.println("index " + 0 + " duration [s] " + rows[0].elements[0].duration.inSeconds);
        for (int i = 1; i < rows.length; i++) {
            System.out.println("index " + i + " duration [s] " + rows[i].elements[0].duration.inSeconds);
            if (rows[i].elements[0].duration.inSeconds < min) {
                min = rows[i].elements[0].duration.inSeconds;
                minIndex = i;
            }
        }
        return minIndex;
    }

    public RideDetailsResponse getRideInfo(String origin, String destination, String waypoint) {
        DirectionsResult directionResult = getDirection(origin, destination, waypoint);
        GeocodingResult[] geocodingUserLoc = getGeocodedUserAndDestination(waypoint);
        GeocodingResult[] geocodingUserDest = getGeocodedUserAndDestination(destination);
        if (directionResult == null || geocodingUserLoc == null || geocodingUserDest == null)
            return null;
        else {

            String userLoc = "";
            String userDest = "";
            for (int i = 0; i < geocodingUserLoc.length; i++) {
                System.out.println("Geocoded u loc " + geocodingUserLoc[i].geometry.location.toString());
                userLoc = geocodingUserLoc[i].geometry.location.toString();
            }
            for (int i = 0; i < geocodingUserDest.length; i++) {
                System.out.println("Geocoded u dest " + geocodingUserDest[i].geometry.location.toString());
                userDest = geocodingUserDest[i].geometry.location.toString();
            }

            DirectionsRoute[] route = directionResult.routes;
            String[] polylines = getPolylines(route[0]);
            RideDetailsResponse ride = new RideDetailsResponse();
            Long allDuration = route[0].legs[0].duration.inSeconds + route[0].legs[1].duration.inSeconds;
            ride.setDriverDistance(route[0].legs[0].distance.inMeters);
            ride.setUserDistance(route[0].legs[1].distance.inMeters);
            ride.setDriverDuration(route[0].legs[0].duration.inSeconds);
            ride.setUserDuration(route[0].legs[1].duration.inSeconds);
            ride.setDriverPolyline(polylines[0]);
            ride.setUserPolyline(polylines[1]);
            ride.setUserLocation(userLoc);
            ride.setUserDestination(userDest);
            ride.setApproxPrice(calculatePrice(allDuration, route[0].legs[1].distance.inMeters));

            return ride;
        }
    }

    private GeocodingResult[] getGeocodedUserAndDestination(String location)  {
        try{
            return GeocodingApi.newRequest(this.context).address(location).await();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    private DirectionsResult getDirection(String origin, String destination, String waypoint) {
        System.out.println("Driver loc " + origin + " user loc " + waypoint + "user des " + destination);
        try {
            return DirectionsApi.newRequest(this.context)
                    .units(Unit.METRIC)
                    .region("pl")
                    .origin(origin)
                    .waypoints(waypoint)
                    .destination(destination)
                    .await();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private String[] getPolylines(DirectionsRoute directionsRoute) {
        List<LatLng> toUser = new ArrayList<LatLng>();
        List<LatLng> toDestination = new ArrayList<LatLng>();
        for (int i = 0; i < directionsRoute.legs[0].steps.length; i++)
            toUser.addAll(directionsRoute.legs[0].steps[i].polyline.decodePath());

        for (int i = 0; i < directionsRoute.legs[1].steps.length; i++)
            toDestination.addAll(directionsRoute.legs[1].steps[i].polyline.decodePath());

        EncodedPolyline toUserPolyline = new EncodedPolyline(toUser);
        EncodedPolyline toDestinationPolyline = new EncodedPolyline(toDestination);
        System.out.println("to user polyline " + toUserPolyline.getEncodedPath());
        System.out.println("to destination polyline " + toDestinationPolyline.getEncodedPath());

        return new String[]{toUserPolyline.getEncodedPath(), toDestinationPolyline.getEncodedPath()};
    }

}
