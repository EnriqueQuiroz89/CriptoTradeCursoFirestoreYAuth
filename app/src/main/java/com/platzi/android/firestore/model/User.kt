package com.platzi.android.firestore.model

/**
 * @author Santiago Carrillo
 * 3/7/19.
 */

/**Se usara para mapear el documento de la coleccion usuario*/
class User {
    var username: String = ""
    var cryptosList: List<Crypto>? = null  /**En esta lista se almacenaran las monedas del usuario
                                            * Pudiendo estar vacia esta lista*/
}
