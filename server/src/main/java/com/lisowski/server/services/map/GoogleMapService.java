package com.lisowski.server.services.map;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixRow;
import com.lisowski.server.Utils.MapsKey;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class GoogleMapService {
    public final GeoApiContext context;

    public GoogleMapService() {
        this.context = new GeoApiContext.Builder().apiKey(MapsKey.API_KEY).build();
    }

    public Long findClosestDriver(String origin, String[] arrayOfPositions) {
        System.out.println("destination " + origin);
        System.out.println("origins " + Arrays.toString(arrayOfPositions));
        String[] origins  = new String[] {"50.874872,20.626861","50.887521,20.659298"};
        String[] destinations = new String[] { "50.888859,20.645138" };

//        try {
//            DistanceMatrix matrix =
//                    DistanceMatrixApi.getDistanceMatrix(this.context, origins, destinations).await();
//            System.out.println(matrix.toString());
//            DistanceMatrixRow[] rows = matrix.rows;
//
//            return findShortestTimeIndex(rows);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
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
}
