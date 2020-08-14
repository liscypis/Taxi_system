package com.lisowski.clientapp

object Constants {

    // Endpoints
    const val BASE_URL = "http://192.168.8.108:8081/api/"
    const val LOGIN_URL = "auth/signin"
    const val REGISTER_URL = "auth/signup"
    const val INITIAL_ORDER = "test/initialOrderRide"
    const val CONFIRM_ORDER = "test/confirmRide"
    const val GET_POSITION = "test/getDriverLocation/{driverID}"
    const val GET_RIDE_STATUS = "test/getRideStatus/{id}"
    const val CONFIRM_DRIVER_ARRIVE = "test/confirmDriverArrival/{id}"
    const val GET_DRIVER_CAR = "test/getCarByDriverId/{id}"



    const val RIDE_DETAIL = "ride_detail"

}