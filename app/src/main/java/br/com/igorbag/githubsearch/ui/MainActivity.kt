package br.com.igorbag.githubsearch.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.domain.Repository
import br.com.igorbag.githubsearch.ui.adapter.RepositoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    lateinit var nomeUsuario: EditText
    lateinit var btnConfirmar: Button
    lateinit var listaRepositories: RecyclerView
    lateinit var githubApi: GitHubService
    lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        setupListeners()
        showUserName()
        setupRetrofit()
        getAllReposByUserName()
    }

    companion object {
        const val PREF_NAME = "AppPreferences"
        const val KEY_USERNAME = "username"
        private const val URL_BASE = "https://api.github.com/"
    }

    // Metodo responsavel por realizar o setup da view e recuperar os Ids do layout
    fun setupView() {
        nomeUsuario = findViewById(R.id.et_nome_usuario)
        btnConfirmar = findViewById(R.id.btn_confirmar)
        listaRepositories = findViewById(R.id.rv_lista_repositories)

        listaRepositories.layoutManager = LinearLayoutManager(this)

        //Inicialização do SharedPreferences
        sharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    //metodo responsavel por configurar os listeners click da tela
    private fun setupListeners() {
        btnConfirmar.setOnClickListener {
            saveUserLocal()
            getAllReposByUserName()
        }
    }

    // salvar o usuario preenchido no EditText utilizando uma SharedPreferences
    private fun saveUserLocal() {
        //Inicialmente pensei assim:
        /*val editor = sharedPref.edit()
        editor.putString("username", nomeUsuario.text.toString())
        editor.apply()*/

        //Mas esse é a forma mais enxuta de fazer com extension functions:
        sharedPref.edit {
            putString(KEY_USERNAME, nomeUsuario.text.toString())
        }
    }

    private fun showUserName() {
        val username = sharedPref.getString(KEY_USERNAME, "")
        nomeUsuario.setText(username)
    }

    //Metodo responsavel por fazer a configuracao base do Retrofit
    fun setupRetrofit() {
        /*
           @ok -  realizar a Configuracao base do retrofit
           Documentacao oficial do retrofit - https://square.github.io/retrofit/
           URL_BASE da API do  GitHub= https://api.github.com/
           lembre-se de utilizar o GsonConverterFactory mostrado no curso
        */

        val retrofit = Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        githubApi = retrofit.create(GitHubService::class.java)
    }

    //Metodo responsavel por buscar todos os repositorios do usuario fornecido
    fun getAllReposByUserName() {
        val nomeDoUsuario = nomeUsuario.text.toString()

        if (nomeDoUsuario != "") {
            githubApi.getAllRepositoriesByUser(nomeUsuario.text.toString()).enqueue(object : Callback<List<Repository>> {
                override fun onResponse(
                    call: Call<List<Repository>?>,
                    response: Response<List<Repository>>
                ) {
                    if (response.isSuccessful){
                        response.body()?.let {
                            setupAdapter(it)
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            R.string.response_error,
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("onFailure error", response.errorBody().toString())
                    }
                }

                override fun onFailure(
                    call: Call<List<Repository>>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        applicationContext,
                        R.string.response_error,
                        Toast.LENGTH_LONG
                    ).show()
                }

            })

        } else {
            Toast.makeText(
                applicationContext,
                R.string.response_error,
                Toast.LENGTH_LONG).show()
        }
    }

    // Metodo responsavel por realizar a configuracao do adapter
    fun setupAdapter(list: List<Repository>) {
        val adapter = RepositoryAdapter(list)

        adapter.repositoryItemLister = {
            Toast.makeText(applicationContext, it.name, Toast.LENGTH_LONG).show()
            openBrowser(it.htmlUrl)
        }

        adapter.btnShareLister = {
            shareRepositoryLink(it.htmlUrl)
        }

        listaRepositories.adapter = adapter
    }


    // Metodo responsavel por compartilhar o link do repositorio selecionado
    // @ok - Colocar esse metodo no click do share item do adapter
    fun shareRepositoryLink(urlRepository: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    // Metodo responsavel por abrir o browser com o link informado do repositorio
    // @ok - Colocar esse metodo no click item do adapter
    fun openBrowser(urlRepository: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            )
        )

    }

}