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
            .addOnSuccessListener { result ->  /**se implementa para deserializar el objeto result*/
                /**al iterar documentos en result se entiende que result es una lista de documentos*/
                for (document in result) { /**Con este for se asume que es una lista de objetos*/
                 /**No entiendo por que en el mismo ciclo se puede llenar la lista*/

               /**Aqui se covierte el objeto result a un  List<Crypto!>*/
                    val cryptoList = result.toObjects(Crypto::class.java)
                    callback!!.onSuccess(cryptoList) /**Aqui el callback devuleve la lista compuesta*/

                    break /**Se interrupme la ejecucion*/
                }
            }  /**Fin del addOnSuccessListener*/
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

         /**Escucharan los cambios que haya en las colleciones de CRyptos y Users*/
                        /**Recibe una lista de cryptos*/
                                                /**Instancia una interfaz */
    fun listenForUpdates(cryptos: List<Crypto>, listener: RealtimeDataListener<Crypto>) {
             /**Obtener referencia a la coleccion de criptomonedas*/
              val cryptoReference = firebaseFirestore.collection(CRYPTO_COLLECTION_NAME)
           /**Iterar la lista que se recibe por parametros*/
        for (crypto in cryptos) {
                    /**Dentro de la coleccion crearemos para cada elemento un listener*/
            cryptoReference.document(crypto.getDocumentId()) /**Le indica que a la cripto actual*/
                          /**Requiere dos parametros*/
                                                 /**instancia de la dataa*/
                                                           /**Error que ocurra en el servidor*/
                           .addSnapshotListener { snapshot, e ->  /**Le agregue un suscriptor*/
                                                                 /**a cambios en este nodo*/
                     if (e != null) { /**Si hay error devolbvemos al listener el error*/
                         listener.onError(e)
                                    }
                               /**Si no hay error*/
                if (snapshot != null && snapshot.exists()) {  /**Verificamos que el snapshot no sea nulo
                                                                  y en segunda que contenga datos*/
                             /**Propagar estos datos a traves del listener*/
                                          /**conevertimos el data al modelo que queremos*/
                    listener.onDataChange(snapshot.toObject(Crypto::class.java)!!)
                }
            }
        }
    }

         /**Monitorea cambios en los usuarios*/
    fun listenForUpdates(user: User, listener: RealtimeDataListener<User>) {
             /**Referencia a la coleccion de usuarios*/
        val usersReference = firebaseFirestore.collection(USERS_COLLECTION_NAME)
        /**A la constante de la coleeccion*/
                                /**Le vamos a enviar el id del documento*/
                                              /**Agregamos el Snapshot listener a tal documento*/
                                                                    /**el snapshot y la excepcion*/
        usersReference.document(user.username).addSnapshotListener { snapshot, e ->
            if (e != null) { /**SI hay error lo pasa al listener*/
                listener.onError(e)
            }
            /**Si no hay error */
            if (snapshot != null && snapshot.exists()) {
                /**Ahora envio al lister el data CATSEADO en forma de User */
                listener.onDataChange(snapshot.toObject(User::class.java)!!)
            }
        }
    }


}