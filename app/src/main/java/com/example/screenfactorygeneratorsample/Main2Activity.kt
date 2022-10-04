package com.example.screenfactorygeneratorsample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.screenfactorygeneratorsample.Main2ActivityScreen.initArguments
import com.example.screenfactorygeneratorsample.databinding.ActivityMain2Binding
import com.udfsoft.screenfactorygenerator.annotation.JParam
import com.udfsoft.screenfactorygenerator.annotation.JScreen

@JScreen()
class Main2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding

    @JParam
    var message: String = ""

    @JParam
    var count: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initArguments()
//        ScreenManager.initArguments(this)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.messageTextView.text = message
        binding.countTextView.text = count.toString()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}