package com.platzi.android.firestore.model

/**
 * @author Santiago Carrillo
 * 3/7/19.
 */

/**Es el modelo de datos para un documento en FiBa*/
class User {
    var username: String = ""

    var cryptosList: List<Crypto>? = null
}
