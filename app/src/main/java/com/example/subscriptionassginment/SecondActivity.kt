package com.example.subscriptionassginment

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class SecondActivity : AppCompatActivity() {

    private lateinit var fire: FirebaseDatabase
    private lateinit var FireDB: DatabaseReference

    private lateinit var addBtn: Button
    private lateinit var cancelBtn: Button
    private lateinit var editName: EditText
    private lateinit var editPrice: EditText
    private lateinit var dateEdt: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        addBtn = findViewById(R.id.btnOK)
        cancelBtn = findViewById(R.id.btnCancel)


        dateEdt = findViewById(R.id.etxtvExpDate)
        editPrice = findViewById(R.id.etxtvPrice)
        editName = findViewById(R.id.etxtvName)


        dateEdt.setOnClickListener {

            val c = Calendar.getInstance()

            // current day, month and year.
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            // variable for date picker dialog.
            val datePickerDialog = DatePickerDialog(

                this,
                { view, year, monthOfYear, dayOfMonth ->
                    // setting date to our edit text.
                    val dat = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                    dateEdt.setText(dat)
                },

                year,
                month,
                day
            )
            //to display our date picker dialog.
            datePickerDialog.show()
        }

        //Firebase
        addBtn.setOnClickListener {
            if (editName.text.isNotEmpty() && editPrice.text.isNotEmpty() && dateEdt.text.isNotEmpty()) {
                addToFirebase(editName.text.toString(), editPrice.text.toString(), dateEdt.text.toString())
            }
            else{
                Toast.makeText(this, "Fill in all the fields peasant", Toast.LENGTH_SHORT).show()
            }

        }
        cancelBtn.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }


    }

    private fun addToFirebase(name: String, price: String, date: String) {
        val sub = Subscription(name, date, price.toDouble())

        fire = FirebaseDatabase.getInstance()
        FireDB = fire.getReference("Subscriptions")
        FireDB.child(name).setValue(sub).addOnCompleteListener {
            Toast.makeText(this, "Subscription added", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        }

    }

    fun close(view: View) {
        var returnIntent = Intent();

        if (view.id == R.id.btnCancel)
            this.setResult(RESULT_CANCELED)
        else {
            this.setResult(RESULT_OK, returnIntent)
        }
        finish()
    }

}