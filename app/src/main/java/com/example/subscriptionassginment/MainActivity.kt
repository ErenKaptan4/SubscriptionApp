package com.example.subscriptionassginment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast

import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {
    private lateinit var fire: FirebaseDatabase
    private lateinit var FireDB: DatabaseReference

    lateinit var subscriptions: MutableList<Subscription>
    lateinit var subscriptionAdapter: SubscriptionAdapter

    lateinit var addSubIntent: Intent
    lateinit var biometricPrompt: BiometricPrompt
    lateinit var executor: Executor
    lateinit var promtInfo: PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //List
        subscriptions = mutableListOf(

        )

        val rvSubs: RecyclerView = findViewById(R.id.rvSubs)
        rvSubs.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        subscriptionAdapter = SubscriptionAdapter(subscriptions)

        rvSubs.adapter = subscriptionAdapter

        //requesting permissions
        requestMultiplePermissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.INTERNET
            )
        )


        //initializing Intent
        addSubIntent = Intent(this, SecondActivity::class.java)


        //firebase
        readData()
    }

    private fun biometric() {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)

                    //Error message
                    Toast.makeText(
                        this@MainActivity,
                        "Authentication Failed: " + errString,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    //Successes message
                    Toast.makeText(
                        this@MainActivity,
                        "Authentication Successful",
                        Toast.LENGTH_SHORT
                    ).show()

                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()

                    //Failed message
                    Toast.makeText(this@MainActivity, "Authentication Failed", Toast.LENGTH_SHORT)
                        .show()
                }

            })

        promtInfo = PromptInfo.Builder()
            .setTitle("Finger Authentication")
            .setDescription("Use FingerPrint To Login")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        biometricPrompt.authenticate(promtInfo)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        floatingAction(item.actionView)
        return super.onOptionsItemSelected(item)
    }

    public fun floatingAction(view: View?) {
        newSubLauncher.launch(addSubIntent)
    }

    private fun readData(){
        fire = FirebaseDatabase.getInstance()
        FireDB = fire.getReference("Subscriptions")
        FireDB.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                subscriptions.clear()
                for (ds:DataSnapshot in snapshot.children){
                    val data = ds.getValue(Subscription::class.java)
                    if (data != null) {
                        subscriptions.add(data)
                    }
                    subscriptionAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to read data", Toast.LENGTH_SHORT).show()
            }

        })
    }

    var newSubLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == RESULT_OK) {
                subscriptionAdapter.notifyDataSetChanged()

            }
        }

    private val requestMultiplePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { res ->
            val deniedList = mutableListOf<String>()

            if (res[android.Manifest.permission.INTERNET] == false) {
                deniedList.add(android.Manifest.permission.INTERNET)
            }

            if (res[android.Manifest.permission.ACCESS_COARSE_LOCATION] == false) {
                deniedList.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)
            }

            if (res[android.Manifest.permission.ACCESS_FINE_LOCATION] == false) {
                deniedList.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }

            if (res[android.Manifest.permission.CAMERA] == false) {
                deniedList.add(android.Manifest.permission.CAMERA)
            }

            //If all permissions granted, authenticate fingerprint
            if (deniedList.isEmpty()) {
                biometric()
            }

        }


}