package com.platzi.android.firestore.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.platzi.android.firestore.R
import com.platzi.android.firestore.adapter.CryptosAdapter
import com.platzi.android.firestore.adapter.CryptosAdapterListener
import com.platzi.android.firestore.model.Crypto
import com.platzi.android.firestore.model.User
import com.platzi.android.firestore.network.Callback
import com.platzi.android.firestore.network.FirestoreService
import com.platzi.android.firestore.network.RealtimeDataListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_trader.*
import java.lang.Exception


/**
 * @author Santiago Carrillo
 * 2/14/19.
 */
class TraderActivity : AppCompatActivity(), CryptosAdapterListener {

    lateinit var firestoreService: FirestoreService  /**Para consumir los servicios de firebase*/

    private val cryptosAdapter: CryptosAdapter = CryptosAdapter(this)

    private var username: String? = null /**? <- le indicamos que puede tomar valor nulo*/

    private var user: User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trader)
        firestoreService = FirestoreService(FirebaseFirestore.getInstance())

        /**Inicializacomos y recuperamos el valor que viene de la actividad login*/
        username = intent.extras!![USERNAME_KEY]!!.toString()
        /**El nombre recuperado de la actividad anterior lo mostramos en esta actividad en un TextView*/
        usernameTextView.text = username

        configureRecyclerView()
        loadCryptos()

        fab.setOnClickListener { view ->
            Snackbar.make(view, getString(R.string.generating_new_cryptos), Snackbar.LENGTH_SHORT)
                .setAction("Info", null).show()
            generateCryptoCurrenciesRandom()
        }

    }

    private fun generateCryptoCurrenciesRandom() {
        for (crypto in cryptosAdapter.cryptoList) {
            val amount = (1..10).random()
            crypto.available += amount
            firestoreService.updateCrypto(crypto)
        }
    }

    /**Este metodo carga las cripto monedas invocando al metodo que las descerializa y devuleve una lista de criptos*/
      private fun loadCryptos() {
          /**Invoca al metodo que obtiene las monedas de la coleccion mediante un callback*/
          firestoreService.getCryptos(object : Callback<List<Crypto>> {    /**Puede devolver una List<Crypto> o una excepcion*/

            override fun onSuccess(resultCryptoList: List<Crypto>?) {  /**Si devuelve una List<Crypto> la ocupa como parametro*/
                                                                  /**Y ahi la guarda en standby*/

              /**Usa el metodo para obtener al documento usuario en formato User() mediante el username que es el id*/
              firestoreService.findUserById(username!!, object : Callback<User> {
                    override fun onSuccess(result: User?) {  /**Si exito ocupa el User? devuelto en forma de result */
                        user = result  /**Asigna el result a una variable GLOBAL user */

                        /**Tiene lista de Criptomonedas??*/
                        if (user!!.cryptosList == null) {    /**Si la lista de monedas del usuario esta vacia*/
                            /**Crea una cartera vacia de cryptomonedas*/
                             val userCryptoList = mutableListOf<Crypto>()
                            /**Mediante una for se recorre la coleccion de Criptos que retorno el callback de getCryptos
                             * y se agrega en cada iteracion una moneda a la lista  */
                            for (crypto in resultCryptoList!!) { /***/
                               /***Crea un objeto Crypto para asignarle las propiedades
                                * de una criptomoneda ya existente*/
                                val cryptoUser = Crypto()
                                /**Setear cada uno de los valores*/
                                cryptoUser.name = crypto.name
                                cryptoUser.available = crypto.available.toInt()
                                cryptoUser.imageUrl = crypto.imageUrl
                               /**Una vez armado el objeto lo agrega a la cartera vacia
                                * que aun no ha sido asigna al usuario */
                                userCryptoList.add(cryptoUser)
                            }   /**Al terminar el for significa que yaa no hay monedas por agregar*/
                            /**Asigna la cartera recien llenada a la cartera vacia del Usuario */
                            user!!.cryptosList = userCryptoList

                            /**Finalmente actualizamos la data al servidor de la siguiente forma*/
                            firestoreService.updateUser(user!!, null)   //No voy a hacer nada cuando esta oeracion termine
                            /**Enviamos un usuario y nos devuleve el usuario actualizado*/
                        }
                        /**Lo anterior solo sirvio para crear carteras nuevas a usuarios nuevos*/

                        loadUserCryptos()  /*** Este metddo carga la lista al panel de informacion */


                        addRealtimeDatabaseListeners(user!!, resultCryptoList!!)

                    }   /**Fin del onSucesss de findUserBY*/

                    override fun onFailed(exception: Exception) { /**Esto hara si falla el findUserBY */
                        showGeneralServerErrorMessage()
                    }

                }) /**fin del findUserById*/


              /**solo ejecuta una acción específica desde
               * un thread que estés ejecutando sobre
               * una view (un componente, ya sea TextView u otro)
               * del hilo principal, es decir,
               * un componente de tu app.*/
                this@TraderActivity.runOnUiThread {
                    cryptosAdapter.cryptoList = resultCryptoList!!
                    cryptosAdapter.notifyDataSetChanged()
                                                  }

                                             } /**Fin del onSucess del getCryptos*/

            override fun onFailed(exception: Exception) {   /**Esto se hara si falla getCryptos */
                Log.e("TraderActivity", "error loading criptos", exception)
                showGeneralServerErrorMessage()
            }

        })
    }

    private fun addRealtimeDatabaseListeners(user: User, cryptosList: List<Crypto>) {

        firestoreService.listenForUpdates(user, object : RealtimeDataListener<User> {
            override fun onDataChange(updatedData: User) {
                this@TraderActivity.user = updatedData
                loadUserCryptos()
            }

            override fun onError(exception: Exception) {
                showGeneralServerErrorMessage()
            }

        })

        firestoreService.listenForUpdates(cryptosList, object : RealtimeDataListener<Crypto> {
            override fun onDataChange(updatedData: Crypto) {
                var pos = 0
                for (crypto in cryptosAdapter.cryptoList) {
                    if (crypto.name.equals(updatedData.name)) {
                        crypto.available = updatedData.available
                        cryptosAdapter.notifyItemChanged(pos)
                    }
                    pos++
                }
            }

            override fun onError(exception: Exception) {
                showGeneralServerErrorMessage()
            }

        })

    }
/**Se encarga de cargar los elemento de la lista en el panel*/
    private fun loadUserCryptos() {
    /**runOnUiThread solo ejecuta una acción específica desde
     * un thread que estés ejecutando sobre
     * una view (un componente, ya sea TextView u otro)
     * del hilo principal, es decir,
     * un componente de tu app.*/

        runOnUiThread {
                 /**Que el usaurio no sea null*/
                                /**Que el usuario tenga un Lista de Criptos no nulla*/
            if (user != null && user!!.cryptosList != null) {
                          /**Borra todas las vistas que tiene*/
                infoPanel.removeAllViews()  /**PAnel donde se mostraran las monedas que
                                               posee el usuario */

                 /**mediante un for vamos a recorrer las monedas que tiene el
                  * usuario y las vamos a ir pasANDO al metodo
                  * encargado de pintar tales elementos*/
                for (crypto in user!!.cryptosList!!) {
                    addUserCryptoInfoRow(crypto) /**Recibe un objeto Crypto()*/
                }
            }

        }
    }


/**Se encarga de agregar al panel del usuario
 *  la Crypto() que recibe como parametro*/
    private fun addUserCryptoInfoRow(crypto: Crypto) {
                                              /**Significa esta actividad*/
                                                                    /**El recurso seria*/
                                                                                       /**Indico donde lo vamos a agregar*/
        val view = LayoutInflater.from(this).inflate(R.layout.coin_info, infoPanel, false)
       /**Actualizar los valores*/
                        /**Pinta el nombre de la Crypto*/
       view.findViewById<TextView>(R.id.coinLabel).text =
                        getString(R.string.coin_info,  /**Continee los valores a reemplazar*/
                                  crypto.name,                 /**Valor 1*/
                                  crypto.available.toString()) /**Valor 2*/
                       /**Pinta la imagen de acuerdo a la url
                        * que se le paso*/

        /**Para la imagen*/
                      /**Fuente de la imagen*/
                                            /**Target de la imagen*/
        Picasso.get().load(crypto.imageUrl).into(view.findViewById<ImageView>(R.id.coinIcon))

        /**Agregar la vista al contenedor*/
       infoPanel.addView(view)
    }

    private fun configureRecyclerView() {
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = cryptosAdapter

    }

    fun showGeneralServerErrorMessage() {
        Snackbar.make(fab, getString(R.string.error_while_connecting_to_the_server), Snackbar.LENGTH_LONG)
            .setAction("Info", null).show()
    }

    /***Esta funcion es matematica y no se puede hacer si available es string
    override fun onBuyCryptoClicked(crypto: Crypto) {
        if (crypto.available > 0) {
            for (userCrypto in user!!.cryptosList!!) {
                if (userCrypto.name == crypto.name) {
                    userCrypto.available += 1
                    break
                }
            }
            crypto.available--

            firestoreService.updateUser(user!!, null)
            firestoreService.updateCrypto(crypto)
        }
    } */


}