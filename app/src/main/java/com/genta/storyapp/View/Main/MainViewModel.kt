package com.genta.storyapp.View.Main


import androidx.lifecycle.*
import com.genta.storyapp.Data.API.ApiConfig
import com.genta.storyapp.Data.Response.MsgGetStory
import com.genta.storyapp.Data.Response.ResponseGetStory
import com.genta.storyapp.Model.UserModel
import com.genta.storyapp.Model.UserPreference
import kotlinx.coroutines.launch
import retrofit2.Call

import retrofit2.Callback
import retrofit2.Response


class MainViewModel(private val pref: UserPreference)  : ViewModel(){
    private val _APIresult = MutableLiveData<String>()
    val APIresult : LiveData<String> =_APIresult
    val Story = MutableLiveData<ArrayList<ResponseGetStory>>()


    fun exit(){
        viewModelScope.launch {
            pref.logout()
        }
    }
    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun getStoryuser(token : String){
        val listStory = ArrayList<ResponseGetStory>()
        val client = ApiConfig.getApiService().getStory("Bearer "+token)

        client.enqueue(object: Callback<MsgGetStory>{
            override fun onResponse(call: Call<MsgGetStory>, response: Response<MsgGetStory>) {
                val _response = response.body()
                if (response.isSuccessful && _response != null){
                    Story.postValue(_response.listStory)
                    Story.value = listStory

                }else{
                    _APIresult.value = _response?.message
                }
            }

            override fun onFailure(call: Call<MsgGetStory>, t: Throwable) {
                _APIresult.value = t.message
            }
        })
    }
}