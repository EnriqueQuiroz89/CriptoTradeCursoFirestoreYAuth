package com.platzi.android.firestore.network

import java.lang.Exception

/**
 * @author Santiago Carrillo
 * 3/7/19.
 */

/**Nos notifica si la operacion es exitosa o no*/
interface Callback<T> {  // Tipo generico de Java que nos permite mapear cualquier tipo de objeto

    fun onSuccess(result: T?)

    fun onFailed(exception: Exception)

}