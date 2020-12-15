package com.platzi.android.firestore.network

import java.lang.Exception

/**
 *La interfaz nos va a notificarcada vez que haya un cambio
 * 3/9/19.
 */
                              /**Usamos los tipos genericos para poder
                               * monitorear los cambios en cualquier tipo
                               * de objeto*/
interface RealtimeDataListener<T> {
                     /**Nos va a enviar la data que ha sido cambiada*/
    fun onDataChange(updatedData: T)
                    /**Cuando ocurre algun erro en el Server*/
    fun onError(exception: Exception)

}
