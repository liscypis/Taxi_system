package com.lisowski.driverapp

object Constants {

    // Endpoints
    const val BASE_URL = "http://192.168.8.108:8081/api/"
    const val LOGIN_URL = "auth/signin"
    const val INITIAL_ORDER = "test/initialOrderRide"
    const val CONFIRM_ORDER = "test/confirmRide"
    const val GET_POSITION = "test/getDriverLocation/{driverID}"
    const val GET_RIDE_STATUS = "test/getRideStatus/{id}"
    const val CONFIRM_DRIVER_ARRIVE = "test/confirmDriverArrival/{id}"
    const val GET_DRIVER_CAR = "test/getCarByDriverId/{id}"
    const val GET_PRICE = "test/getPriceForRide/{id_ride}"
    const val COMPLETE_RIDE = "test/setRideStatus"
    const val RIDE_RATE = "test/setRideRate"
    const val GET_HISTORY = "test/getUserRidesByUserId/{id_user}"
    const val CHECK_FOR_NEW_RIDE = "test/checkForNewRide/{id}"
    const val ADD_LOCATION = "test/addLocation"
    const val DRIVER_STATUS = "test/setStatus"


    const val RIDE_DETAIL = "ride_detail"

    const val WAITING_FOR_USER = "wait_to_user"
    const val ON_THE_WAY_TO_DEST = "way_to_destination"
    const val ENDING = "ending"
    const val COMPLETE = "complete"

    const val OFFLINE = "offline"
    const val AVAILABLE = "available"
    const val BUSY = "busy"

    const val USER_LOC = "user_loc"
    const val USER_DEST = "user_dest"
    const val USER_POLYLINE = "user_polyline"

}