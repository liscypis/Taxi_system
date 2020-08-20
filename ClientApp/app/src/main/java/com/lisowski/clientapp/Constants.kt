package com.lisowski.clientapp

object Constants {

    // Endpoints
    const val BASE_URL = "http://192.168.8.112:8081/api/"
    const val LOGIN_URL = "auth/signin"
    const val REGISTER_URL = "auth/signup"
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



    const val RIDE_DETAIL = "ride_detail"

    const val COMPLETE = "complete"
    const val USER_LOC = "user_loc"
    const val USER_DEST = "user_dest"
    const val USER_POLYLINE = "user_polyline"

}