package com.platzi.android.firestore.model

/**
 * @author Santiago Carrillo
 * 3/7/19.
 */
/**Mapea el objeto que nos devuelve Firebase*/
              /**Nombre de la moneda*/
                                   /**Url de la imagen de la moneda*/
                                                               /**Disponibilidad de unidades*/
class Crypto(var name: String = "",
             var imageUrl: String = "",
            // var available: Int = 0) {
             var available: String = "") {
    //convierte el nombre de la moneda a minusculas
    // el id de un docuemnto es el nombre de la moneda
    fun getDocumentId(): String {
        return name.toLowerCase()
    }
}