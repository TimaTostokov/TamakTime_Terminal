package com.pos_terminal.tamaktime_temirnal.common

import com.pos_terminal.tamaktime_temirnal.presentation.activity.MainActivity

object Constants {
    const val BASE_URL = "https://kantindeyim.com/"
    const val NETWORK_TIMEOUT = 30L
    const val CATEGORY_ID_END_POINT = "/api/v1/canteens/{canteenId}/product/categories/"
    const val ORDER_END_POINT = "/api/v1/canteens/{canteenId}/order/"
    const val ORDER_ID_END_POINT = "/api/v1/canteens/{canteenId}/order/{orderId}/process/"
    const val PUT_DOCUMENT_END_POINT = "/api/v1/canteens/{canteen_id}/document/{document_id}/"
    const val PRODUCTS_END_POINT = "/api/v1/canteens/{canteenId}/products"
    const val STUDENTS_END_POINT = "/api/v1/schools/{school_id}/students/card/{card_uuid}/"
    const val USERS_END_POINT = "/api/v1/users/me/"
    const val QR_END_POINT = "/api/v1/canteens/student-cart/cards/{card_uuid}"
    const val STUDENTS_LIMITS_END_POINT = "/api/v1/schools/students/{student_id}/limits/"
    lateinit var APP_ACTIVITY: MainActivity
}