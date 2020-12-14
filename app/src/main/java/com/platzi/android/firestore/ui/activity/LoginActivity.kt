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

/**Por que se creo fuera de la clase*/
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
        Snackbar.make(view, getString(R.string.error_while_connecting_to_the_server), Snackbar.LENGTH_LONG)
            .setAction("Info", null).show()

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

      /**Inicia la actividad y transporta  el nombre que se introdujo en el EditText */
    private fun startMainActivity(username: String) {
        val intent = Intent(this@LoginActivity, TraderActivity::class.java)
        intent.putExtra(USERNAME_KEY, username)
        startActivity(intent)
        finish()
    }

}
