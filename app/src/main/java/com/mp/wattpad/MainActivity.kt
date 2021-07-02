package com.mp.wattpad

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mp.wattpad.adapters.StoriesAdapter
import com.mp.wattpad.api.WattpadApi
import com.mp.wattpad.data.database.DatabaseHelper
import com.mp.wattpad.data.database.TableStories
import com.mp.wattpad.data.model.SQLModel
import com.mp.wattpad.utils.constants.Companion.BASE_URL
import com.mp.wattpad.utils.constants.Companion.TAG_MAIN_ACTIVITY
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var storiesList: List<SQLModel>
    private lateinit var linearLayoutManager: LinearLayoutManager

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DatabaseHelper.initDatabase(this)

        if (hasNetworkConnection(this)) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    getStoriesFromAPI()
                } catch (e: Exception) {
                    Log.e(
                        TAG_MAIN_ACTIVITY,
                        "Exception Occurred in Coroutine when calling the Wattpad API"
                    )
                    Log.e(TAG_MAIN_ACTIVITY, e.toString())
                }
            }
        } else {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    getStoriesFromSQLite()
                } catch (e: Exception) {
                    Log.e(
                        TAG_MAIN_ACTIVITY,
                        "Exception Occurred in Coroutine when calling SQLite DB"
                    )
                    Log.e(TAG_MAIN_ACTIVITY, e.toString())
                }
            }
        }
    }

    /**
     *  hasNetworkConnection() - checks whether the user's phone has active internet connection or not
     *  Unfortunately, I was not able to figure out how to check for internet connection on OS less than M
     */

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun hasNetworkConnection(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                } else {
                    TODO("ANDROID OS Version < M")
                }
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i(TAG_MAIN_ACTIVITY, "CELLULAR NETWORK ON")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i(TAG_MAIN_ACTIVITY, "WIFI ON")
                    return true
                }
            }
        }
        return false
    }

    /**
     *  showStories() - sends the list of stories to StoriesAdapter along with layoutmanger settings
     */
    private fun showStories(stories: MutableList<SQLModel>) {
        storiesList = stories
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        storiesRV.layoutManager = linearLayoutManager
        storiesRV.adapter = StoriesAdapter(this, stories)
    }

    /**
     *  sendStoriesToMainThread() - creates a coroutine on the Main thread and calls showStories() method
     */
    private fun sendStoriesToMainThread(stories: MutableList<SQLModel>) {
        lifecycleScope.launch(Main) {
            try {
                showStories(stories)
                Toast.makeText(
                    this@MainActivity,
                    "Stories are stored locally for offline use",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Log.e(
                    TAG_MAIN_ACTIVITY,
                    "Exception Occurred when trying to send Wattpad Stories to Main Thread"
                )
                Log.e(TAG_MAIN_ACTIVITY, e.toString())
            }
        }
    }

    /**
     *  getStoriesFromSQLite() - fetches stories from SQLiteDB when no internet connection is available on device
     */

    private fun getStoriesFromSQLite() {
        var TableStoriesInstance = TableStories()
        var stories = TableStoriesInstance.getStories()
        sendStoriesToMainThread(stories as MutableList<SQLModel>)
    }

    /**
     *  getStoriesFromAPI() - called from the coroutine in onCreate() method in this activity
     *    creates a RetrofitBuilder object and the getStories() method is called in the WattpadAPI interface
     *    Before sending the stories to Main Thread, the fetched stories are stored in SQLiteDB
     */
    private suspend fun getStoriesFromAPI() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(WattpadApi::class.java)

        val retrofitData = retrofitBuilder.getStories()
        var listOfStories: MutableList<SQLModel> = mutableListOf()
        var TableStoriesInstance = TableStories()

        TableStoriesInstance.clearStories()

        for (i in retrofitData.stories) {
            val SQLModel = SQLModel(i.title, i.user.name, i.cover)
            listOfStories.add(SQLModel)
            TableStoriesInstance.insertStories(SQLModel)
        }
        sendStoriesToMainThread(listOfStories)
    }
}