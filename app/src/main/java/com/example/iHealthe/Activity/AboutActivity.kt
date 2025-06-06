package com.example.iHealthe.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.example.iHealthe.R
import com.example.iHealthe.utils.setupNavigation
import com.google.android.material.bottomnavigation.BottomNavigationView

class AboutActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.statusBarColor = ContextCompat.getColor(this, R.color.gray)
        setContentView(R.layout.activity_about)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        setupVersionInfo()
        setupPrivacyPolicyButton()
        setBottomNavigationView()
    }

    private fun setupVersionInfo() {
        val version = findViewById<TextView>(R.id.app_version)
        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        version.text = "${getString(R.string.version)} : $versionName"
    }

    private fun setupPrivacyPolicyButton() {
        val privacyPolicyButton = findViewById<Button>(R.id.privacy_policy_button)
        privacyPolicyButton.setOnClickListener {
            val url = "https://jmmdevelopment.github.io/app/privacy-policy.html"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }

    private fun setBottomNavigationView() {
        bottomNavigationView.setupNavigation(R.id.nav_about, this)
    }

    override fun onResume() {
        super.onResume()
        bottomNavigationView.selectedItemId = R.id.nav_about
    }
}
