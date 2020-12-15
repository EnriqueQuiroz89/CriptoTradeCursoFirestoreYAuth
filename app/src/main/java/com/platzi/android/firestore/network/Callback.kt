package com.platzi.android.firestore.network

import java.lang.Exception

/**
 * @author Santiago Carrillo
 * 3/7/19.
 */

/**Nos notifica si la operacion es exitosa o no*/
interface Callback<T> {  // Tipo generico de Java que nos permite mapear cualquier tipo de resultado
    /**Si hay nos va a dar un
     * resultado de tipo generico T*/
    fun onSuccess(result: T?)
    /**Nos va a notificar el
     * erro por medio de una excepcion*/
    fun onFailed(exception: Exception)

}