package com.platzi.android.firestore.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.platzi.android.firestore.R
import com.platzi.android.firestore.model.User
import com.platzi.android.firestore.network.Callback
import com.platzi.android.firestore.network.FirestoreService
import com.platzi.android.firestore.network.USERS_COLLECTION_NAME
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_trader.*
import java.lang.Exception

/**
 * @author Santiago Carrillo
 * github sancarbar
 * 1/29/19.
 */

/**Pasa como parametro al crear un nuevo documento en una collecion*/
const val USERNAME_KEY = "username_key"

class LoginActivity : AppCompatActivity() {


    private val TAG = "LoginActivity"
   /**Instancia para utilizar autenticacion FirebaseAuth*/
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**Crea una instancia para consumir FirestoreServices*/
    lateinit var firestoreService: FirestoreService

    /**Codigo que se ejecuta al iniciar la aplicacion*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        firestoreService = FirestoreService(FirebaseFirestore.getInstance())
    }

/**  Este metodo da la funcion al click
    fun onStartClicked(view: View) {
        view.isEnabled = false
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val username = username.text.toString()
                    firestoreService.findUserById(username, object : Callback<User> {
                      /**Hay que habilitar en la consolo la opcion de autneticacion anonima*/
                        override fun onSuccess(result: User?) {
                            if (result == null) {
                                val user = User()
                                user.username = textView.text.toString()
                                saveUserAndStartMainActivity(user, view)
                            } else
                                startMainActivity(username)
                        }

                        override fun onFailed(exception: Exception) {
                            showErrorMessage(view)
                        }

                    })


                } else {
                    showErrorMessage(view)
                    view.isEnabled = true
                }
            }

    }
*/


    /**Creando el metodo alternativo para crear el usuario en FirebaseAuth*/
    fun onStartClicked(view: View){
        /**Invocando al metodo de autenticacion*/
        // si logra loguearse de forma anonima en firebase
        auth.signInAnonymously().addOnCompleteListener{
            //ejecuta la siguiente tarea
            task -> if(task.isSuccessful){  //Si la tarea es exitosa
            /**Mediante un metodo de FirestoreService determina si el usuario ingresado ya existe o no */
            /**Si no existe devuelve un objeto User para que otro metod lo cree*/
            /**Si existe devuelve solo el nombre del usuario*/
         //Capturo el texto escrito en una variable y lo paso al metodo
             val usuarioCapturado = usernameEt.text.toString()

            firestoreService.findUserById(usuarioCapturado, object : Callback<User> {
                  override fun onSuccess(result: User?) { //Si se pudo conectar con FiBa retorna un objeto User() aunque pudiera venir vacio
                      if(result==null){  //si viene vacio significa que no existe y entonces lo va crear
                        //Parte1
                          /**1. Instancia un nuevo User()*/
                          /**2. Asigna la unica propiedad obligatoria que es el nombre*/
                          /**3. Invoca a la fun que agrega usuarios a la collecion en FaBi*/
                          val newUser = User()
                          newUser.username= usuarioCapturado
                          saveDocumentInColeccionUsers(newUser, view)  //El mensaje viene de la fun
                        //Parte2 si el usuario Fue creado con exito avanzo a la 2da actividad
                          /**1. Valido mediante un callback si se creo con exito el documento en la coleccion*/
                          /**2. Si fue verdadero inicio la segunda actividad pasando como parametro el username */
                          /**3. Invoca a la fun que agrega usuarios a la collecion en FaBi*/
                          val userCreated= true // validad con call back
                          if(userCreated){  startMainActivity(newUser.username) }
                                      } // fin del if
                         else{ //si result no es nulo entonces el usuario si existe
                         // showSimpleMessage(view, "El usuario SI existe")
                          //Parte1
                          /**1. El result es un objeto User()*/
                          /**2. De este objeto extraigo el username que requiero para pasar a la siguinete actividad*/
                          /**3. Invoco a la fun y le paso como parametro el username**/
                          val oldUser = result!!
                          val userName = oldUser.username
                           startMainActivity(userName)
                            }
                                                       }  //fin de onSuccess

                  override fun onFailed(exception: Exception) {
                          showErrorMessage(view)
                                                              } //fin del onFailed
              })
            }   /**  fin de    if(task.isSuccessful)*/
            else{ //si la tarea no es exitosa
            showErrorMessage(view)
                }
                                                     }
                                    }// Fin de onStartClick

    /**Con el texto ingresado en la caja de texto creara un nuevo documento en la colleccion*/
    fun saveDocumentInColeccionUsers(user: User, view: View){
        /**Paso 1. Instancio el metodo que me permite crear un nuevo documento en una coleccion */
                                   /**data: Any  //Envia las propiedades del nuevo documento a crear
                                          *,collectionName: String,  //le indica al metodo a que coleccion agregar el documento
                                                                 *  id: String  //usa el texto escrito en la pantalla para generar un id
                                                                                * Entre las llaves del Callback<> indicas que tipo de objeto va a devolver*/
        firestoreService.setDocument(user, USERS_COLLECTION_NAME, user.username, object : Callback<Void> {
           /**Toodo esto va dentro de un callback*/
              override fun onSuccess(result: Void?) {
                Snackbar.make(view, getString(R.string.nuevo_documento_creado), Snackbar.LENGTH_LONG)
                    .setAction("Info", null).show()
            }

             override fun onFailed(exception: Exception) {
                showErrorMessage(view)
                Log.e(TAG, "error", exception)
            }
                            })//fin del callback y del metodo


        }


/**Guarda el usuario en Firestore e inicia la nueva actividad o bien muestra el error*/
    private fun saveUserAndStartMainActivity(user: User, view: View) {
        firestoreService.setDocument(user, USERS_COLLECTION_NAME, user.username, object : Callback<Void> {
            override fun onSuccess(result: Void?) {
                startMainActivity(user.username)
            }

            override fun onFailed(exception: Exception) {
                showErrorMessage(view)
                Log.e(TAG, "error", exception)
                view.isEnabled = true
            }

        })
    }
   /**Es llamado cuando el usuario no se pudo agregar a la coleccion de usuarios*/
    private fun showErrorMessage(view: View) {
       /**El snackbar necesita una vista por que requiere saber donde se va a mostrar*/
        Snackbar.make(view, getString(R.string.error_while_connecting_to_the_server), Snackbar.LENGTH_LONG)
            .setAction("Info", null).show()
    }


    /**Muestra un simple mennsaje donde se le invoca*/
    private fun showSimpleMessage(view: View, mensaje: String) {
        /**El snackbar necesita una vista por que requiere saber donde se va a mostrar*/
        Snackbar.make(view, mensaje, Snackbar.LENGTH_LONG)
            .setAction("Info", null).show()
    }



    /**Inicia la actividad y transporta  el nombre que se introdujo en el EditText */
    private fun startMainActivity(username: String) {
        val intent = Intent(this@LoginActivity, TraderActivity::class.java)
        intent.putExtra(USERNAME_KEY, username)
        startActivity(intent)
        finish()
    }

}
