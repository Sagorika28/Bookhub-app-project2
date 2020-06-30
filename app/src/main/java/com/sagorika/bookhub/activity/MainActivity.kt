package com.sagorika.bookhub.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.sagorika.bookhub.R
import com.sagorika.bookhub.fragment.AboutAppFragment
import com.sagorika.bookhub.fragment.DashboardFragment
import com.sagorika.bookhub.fragment.FavouritesFragment
import com.sagorika.bookhub.fragment.ProfileFragment

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView

    var previousMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)
        setUpToolbar()

        //open dashboard
        openDashboard()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // to make the items of nav drawer listen when clicked
        navigationView.setNavigationItemSelectedListener {

            //highlighting the selected menu item
            if (previousMenuItem != null)
                previousMenuItem?.isChecked = false //unchecked the previous item

            //check the current item
            it.isCheckable = true
            it.isChecked = true
            //make current item previous
            previousMenuItem = it

            when (it.itemId) {
                R.id.dashboard -> {
                    openDashboard()

                    //to close drawer
                    drawerLayout.closeDrawers()
                }
                R.id.favourites -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FavouritesFragment()
                        )
                        .commit()

                    //giving title to individual fragments
                    supportActionBar?.title = "Favourites"

                    drawerLayout.closeDrawers()
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            ProfileFragment()
                        )
                        .commit()

                    supportActionBar?.title = "Profile"

                    drawerLayout.closeDrawers()
                }
                R.id.aboutApp -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            AboutAppFragment()
                        )
                        .commit()

                    supportActionBar?.title = "About App"

                    drawerLayout.closeDrawers()
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    //to open the nav drawer
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home)
            drawerLayout.openDrawer(GravityCompat.START)

        return super.onOptionsItemSelected(item)
    }

    //to open dashboard fragment
    fun openDashboard() {
        val fragment = DashboardFragment()
        val transaction = supportFragmentManager.beginTransaction()

        transaction.replace(R.id.frame, fragment)
        transaction.commit()

        supportActionBar?.title = "Dashboard"

        //check dashboard item on opening
        navigationView.setCheckedItem(R.id.dashboard)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)

        when (frag) {
            //returns to dashboard on pressing back if it's not the dashboard fragment itslef
            !is DashboardFragment -> openDashboard()

            //back buttons functions normally and exits app when pressed
            else -> super.onBackPressed()
        }
    }
}
