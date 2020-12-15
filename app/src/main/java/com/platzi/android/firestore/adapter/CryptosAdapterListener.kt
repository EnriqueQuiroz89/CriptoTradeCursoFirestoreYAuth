package com.platzi.android.firestore.adapter

import com.platzi.android.firestore.model.Crypto

/**
 * @author Santiago Carrillo
 * 3/9/19.
 */
/**Esta interfaz se ejecuta cuanbdo el usuario hace click en el bton buy */
interface CryptosAdapterListener{
    /**La funcion recibira el objeto crypto sobre el cual se hizo click*/
    fun onBuyCryptoClicked(crypto: Crypto)
}