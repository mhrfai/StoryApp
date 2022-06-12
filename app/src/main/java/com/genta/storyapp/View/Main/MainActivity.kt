package com.genta.storyapp.View.Main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.genta.storyapp.Data.Response.ResponseGetStory
import com.genta.storyapp.Model.UserPreference
import com.genta.storyapp.R
import com.genta.storyapp.Story.ListStoryAdapter
import com.genta.storyapp.Story.addActivity
import com.genta.storyapp.View.Login.LoginActivity
import com.genta.storyapp.ViewModelFactory
import com.genta.storyapp.databinding.ActivityMainBinding
import java.util.ArrayList


class MainActivity : AppCompatActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private lateinit var mainActivity: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    companion object {
        const val Token = "token"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivity.root)
        setupView()
        setListStory()

    }



    private fun setupView() {
        mainViewModel = ViewModelProvider(this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(this, { user ->
            if (user.isLogin){
               supportActionBar?.title = "User : "+ user.name
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        })
        mainActivity.apply {
            mainActivity.floatingBtn.setOnClickListener{
                mainViewModel.getUser().observe(this@MainActivity,{user ->
                    Intent(this@MainActivity,addActivity::class.java).let {
                        it.putExtra(addActivity.EXTRA_TOKEN,user.token)
                        startActivity(it)

                    }
                })

            }


        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.nav_bar,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout->{
                mainViewModel.exit()
                Toast.makeText(this,"Logout Berhasil", Toast.LENGTH_SHORT).show()
            }

        }
        return super.onOptionsItemSelected(item)
    }
    fun setListStory(){
        mainViewModel.Story.observe(this,{
            adapter(it)

        })
        mainViewModel.getUser().observe(this,{id->
            if(id.isLogin){
                id.token.let {
                    mainViewModel.getStoryuser(id.token)
                }
            }
        })
        mainViewModel.APIresult.observe(this,{
            Toast.makeText(this,it.toString(),Toast.LENGTH_SHORT).show()
        })

    }
    fun adapter(story : ArrayList<ResponseGetStory>){
        val _adapter = ListStoryAdapter(story)
        mainActivity.apply {
            rvStoryList.layoutManager = LinearLayoutManager(this@MainActivity)
            rvStoryList.setHasFixedSize(true)
            rvStoryList.adapter = _adapter
        }


    }




}