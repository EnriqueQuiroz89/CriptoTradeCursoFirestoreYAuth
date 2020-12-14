package com.platzi.android.firestore.network

import com.google.firebase.firestore.FirebaseFirestore
import com.platzi.android.firestore.model.Crypto
import com.platzi.android.firestore.model.User


/**
 * @author Santiago Carrillo
 * 3/7/19.
 */
<<<<<<< HEAD
/**Facilitan el acceso a las colecciones*/
=======
/**Facilitan el llamado de colecciones en el codigo*/
>>>>>>> pasarTradeConsole
const val CRYPTO_COLLECTION_NAME = "cryptos"
const val USERS_COLLECTION_NAME = "users"


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

    fun updateUser(user: User, callback: Callback<User>?) {
        firebaseFirestore.collection(USERS_COLLECTION_NAME).document(user.username)
            .update("cryptosList", user.cryptosList)
            .addOnSuccessListener { result ->
                if (callback != null)
                    callback.onSuccess(user)

            }
            .addOnFailureListener { exception -> callback!!.onFailed(exception) }
    }

    fun updateCrypto(crypto: Crypto) {
        firebaseFirestore.collection(CRYPTO_COLLECTION_NAME).document(crypto.getDocumentId())
            .update("available", crypto.available)
    }

    fun getCryptos(callback: Callback<List<Crypto>>?) {
        firebaseFirestore.collection(CRYPTO_COLLECTION_NAME)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val cryptoList = result.toObjects(Crypto::class.java)
                    callback!!.onSuccess(cryptoList)
                    break
                }
            }
            .addOnFailureListener { exception -> callback!!.onFailed(exception) }
    }

    fun findUserById(id: String, callback: Callback<User>) {
        firebaseFirestore.collection(USERS_COLLECTION_NAME).document(id)
            .get()
            .addOnSuccessListener { result ->
                if (result.data != null) {
                    callback.onSuccess(result.toObject(User::class.java))
                } else {
                    callback.onSuccess(null)
                }
            }
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