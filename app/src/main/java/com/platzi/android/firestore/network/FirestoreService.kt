package com.platzi.android.firestore.network

import com.google.firebase.firestore.FirebaseFirestore
import com.platzi.android.firestore.model.Crypto
import com.platzi.android.firestore.model.User


/**
 * @author Santiago Carrillo
 * 3/7/19.
 */

/**Facilitan el llamado de colecciones en el codigo*/

const val CRYPTO_COLLECTION_NAME = "cryptos"
const val USERS_COLLECTION_NAME = "users"

     /**Recibira como parametro el modulo requerido para utilizar Firestore */
class FirestoreService(val firebaseFirestore: FirebaseFirestore) {

       /**Puede agregar cualquier tipo de documento en Firestore*/
                   /**de tipo Any para enviar cualquier tipo de datos*/
                              /**El nombre de la coleccion*/
                                                      /**identificador que le asignemos al documento*/
                                                                  /**El callback es de tipo Void ya que no regresara objeto*/
    fun setDocument(data: Any, collectionName: String, id: String, callback: Callback<Void>) {
        firebaseFirestore.collection(collectionName).document(id).set(data)
            /**Los listener son opcionales pero recomndables para evitar el crash*/
            .addOnSuccessListener { callback.onSuccess(null) } //devuelve null ya el callback retorna un void
                /**A la 'exception->' les llama arrow functiona*/
            .addOnFailureListener { exception -> callback.onFailed(exception) }
    }
   /**Actualizar la cantidad de Cripto monedas que posee el usuario */
                  /**el Usuario que se va a modificar*/
                               /**Reutilizamos el Callback para indicarnos si fue exitosa o no esa ejecucios*/
    fun updateUser(user: User, callback: Callback<User>?) {
                                     /**Le indicamos que coleccion */
        firebaseFirestore.collection(USERS_COLLECTION_NAME).document(user.username)
            .update("cryptosList", user.cryptosList)
            .addOnSuccessListener { result ->   /**Si se actualiza correctamente*/
                if (callback != null)           /***/
                    callback.onSuccess(user)    /**Si hay exito devolvemos
                                                 por callback el objeto User()*/
            }            /**Propago la excepcion en el callback*/
            .addOnFailureListener { exception -> callback!!.onFailed(exception) }
    }

    /**Permite modificar la cantidad disponible de monedas cuando son compradas*/
    fun updateCrypto(crypto: Crypto) {
          /**Uso la funcion update del objeto firebaseFirestore*/
        firebaseFirestore.collection(CRYPTO_COLLECTION_NAME).document(crypto.getDocumentId())
            .update("available", crypto.available)
    }

         /**Entiendo que poniendo un callback en los paramtros
         *  hace la funcion de una variable de retorno*/
         /**Palabras del profe: este callback lo que nos
                              *  va aretornar es una lista de criptomonedas  */
    fun getCryptos(callback: Callback<List<Crypto>>?) {
        firebaseFirestore.collection(CRYPTO_COLLECTION_NAME)
            .get() /**Este metodo es para lectura directo y no en tiempo real*/
            .addOnSuccessListener { result ->  /**El metodo arrojaria un resultado que es la lista*/
                for (document in result) { /**Con este for se asume que es una lista de objetos*/
                  /**En cada iteracion va mapeando el resultado a una lista
                   * de objetos de tipo Crypto*/
                    val cryptoList = result.toObjects(Crypto::class.java)
                    callback!!.onSuccess(cryptoList) /**SI el callback detecta operecion exitosa
                                                        este metodo devolvera la lista mediante
                                                        el callback*/
                    break /**Se interrupme la ejecucion*/
                }
            }
            .addOnFailureListener { exception -> callback!!.onFailed(exception) }
    }
     /**Encuentra a los usuarios por ID*/
                     /**como id pasamos el nameuser que ingresa el usuario*/
                                  /**Usamos un callback para retornar el objeto
                                   * en caso de que lo encuentre  */
    fun findUserById(id: String, callback: Callback<User>) {
        firebaseFirestore.collection(USERS_COLLECTION_NAME).document(id)
            .get()
                /**Le pasamos la accion de lo que hara si es exitoso el proceso*/
            .addOnSuccessListener { result ->
                if (result.data != null) {
                    /**Devolvera mediante callback el usuario buscado*/
                                       /**Aqui le indicamos a que clase queremos convertirlo*/
                    callback.onSuccess(result.toObject(User::class.java))
                } else {
                    /**Si no lo encutra devolvera un objeto nulll*/
                    callback.onSuccess(null)
                }
            } /**En caso de falla*/
            .addOnFailureListener { exception -> callback.onFailed(exception) }
    }

    fun listenForUpdates(cryptos: List<Crypto>, listener: RealtimeDataListener<Crypto>) {
        val cryptoReference = firebaseFirestore.collection(CRYPTO_COLLECTION_NAME)
        for (crypto in cryptos) {
            cryptoReference.document(crypto.getDocumentId()).addSnapshotListener { snapshot, e ->
                if (e != null) {
                    listener.onError(e)
                }
                if (snapshot != null && snapshot.exists()) {
                    listener.onDataChange(snapshot.toObject(Crypto::class.java)!!)
                }
            }
        }
    }

    fun listenForUpdates(user: User, listener: RealtimeDataListener<User>) {
        val usersReference = firebaseFirestore.collection(USERS_COLLECTION_NAME)

        usersReference.document(user.username).addSnapshotListener { snapshot, e ->
            if (e != null) {
                listener.onError(e)
            }
            if (snapshot != null && snapshot.exists()) {
                listener.onDataChange(snapshot.toObject(User::class.java)!!)
            }
        }
    }


}