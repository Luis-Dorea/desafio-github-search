package br.com.igorbag.githubsearch.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.domain.Repository
import androidx.core.content.edit

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
            //setupRetrofit()
            //getAllReposByUserName()
        }

    companion object {
        const val PREF_NAME = "AppPreferences"
        const val KEY_USERNAME = "username"
    }

    // Metodo responsavel por realizar o setup da view e recuperar os Ids do layout
    fun setupView() {
        //OK 1 - Recuperar os Id's da tela para a Activity com o findViewById
        nomeUsuario = findViewById(R.id.et_nome_usuario)
        btnConfirmar = findViewById(R.id.btn_confirmar)
        listaRepositories = findViewById(R.id.rv_lista_repositories)

        //Inicialização do SharedPreferences
        sharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    //metodo responsavel por configurar os listeners click da tela
    private fun setupListeners() {
        //OK 2 - colocar a acao de click do botao confirmar
        btnConfirmar.setOnClickListener {
            Log.e("saida", nomeUsuario.text.toString())
            saveUserLocal()
        }
    }

    // salvar o usuario preenchido no EditText utilizando uma SharedPreferences
    private fun saveUserLocal() {
        //OK 3 - Persistir o usuario preenchido na editText com a SharedPref no listener do botao salvar

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
        // 4- depois de persistir o usuario exibir sempre as informacoes no EditText  se a sharedpref possuir algum valor, exibir no proprio editText o valor salvo
        val username = sharedPref.getString(KEY_USERNAME, "")
        nomeUsuario.setText(username)
    }

    //Metodo responsavel por fazer a configuracao base do Retrofit
    fun setupRetrofit() {
        /*
           @TODO 5 -  realizar a Configuracao base do retrofit
           Documentacao oficial do retrofit - https://square.github.io/retrofit/
           URL_BASE da API do  GitHub= https://api.github.com/
           lembre-se de utilizar o GsonConverterFactory mostrado no curso
        */
    }

    //Metodo responsavel por buscar todos os repositorios do usuario fornecido
    fun getAllReposByUserName() {
        // TODO 6 - realizar a implementacao do callback do retrofit e chamar o metodo setupAdapter se retornar os dados com sucesso
    }

    // Metodo responsavel por realizar a configuracao do adapter
    fun setupAdapter(list: List<Repository>) {
        /*
            @TODO 7 - Implementar a configuracao do Adapter , construir o adapter e instancia-lo
            passando a listagem dos repositorios
         */
    }


    // Metodo responsavel por compartilhar o link do repositorio selecionado
    // @Todo 11 - Colocar esse metodo no click do share item do adapter
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

    // @Todo 12 - Colocar esse metodo no click item do adapter
    fun openBrowser(urlRepository: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            )
        )

    }

}