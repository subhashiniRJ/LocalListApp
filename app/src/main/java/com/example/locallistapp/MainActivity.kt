package com.example.locallistapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {

    companion object{
        var TAG = "MainActivity"
        var KEY = "MyEmailList"
    }


    private var etNumber: EditText? = null
    private var etMail: EditText? = null
    private lateinit var noData: TextView
    private lateinit var textTitle: TextView
    private lateinit var rvEmail: RecyclerView
    private lateinit var gson: Gson
    private var appSharedPrefs: SharedPreferences? = null
    private lateinit var emailList : ArrayList<EmailModelClass>
    private lateinit var adapter : EmailListAdapter
    private var noError: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
        setUpDefaults()
        setUpEvents()
    }

    private fun init(){
        rvEmail = findViewById<RecyclerView>(R.id.rv_mailList)
        textTitle = findViewById<TextView>(R.id.textView)
        noData = findViewById<TextView>(R.id.tvNoList)
        etNumber = findViewById<EditText>(R.id.editTextPhone)
        etMail = findViewById<EditText>(R.id.editTextTextEmailAddress)
        gson = Gson()
        appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
        emailList = ArrayList()
        adapter = EmailListAdapter(this@MainActivity,emailList)
        rvEmail.layoutManager = LinearLayoutManager(this@MainActivity,RecyclerView.VERTICAL,false)
        rvEmail.adapter = adapter
        rvEmail!!.isNestedScrollingEnabled = false
        rvEmail!!.setHasFixedSize(false)
        getList()
    }

    private fun getList() {
        val response: String? = appSharedPrefs!!.getString(KEY, "")
        if(response!!.isNotBlank() || response!!.isNotEmpty()){
            rvEmail.visibility = View.VISIBLE
            textTitle.visibility = View.VISIBLE
            noData.visibility = View.GONE
            val type: Type = object : TypeToken<List<EmailModelClass?>?>() {}.type
            emailList = gson.fromJson(response, type)
            adapter.mList = emailList
            adapter.notifyDataSetChanged()
            Log.e(TAG, "email -----$emailList")
        }else{
            noData.visibility = View.VISIBLE
            rvEmail.visibility = View.GONE
            textTitle.visibility = View.GONE

        }
    }

    private fun setUpDefaults() {
        if(emailList.isEmpty()){

        }else{

        }
    }

    private fun setUpEvents() {
        var btn = findViewById<Button>(R.id.btnSubmit)
        var scrolllView = findViewById<NestedScrollView>(R.id.scrollable)
        btn.setOnClickListener {
            var noError = true
            if (etMail!!.text.isEmpty()) {
                noError = false
                etMail?.setHintTextColor(ContextCompat.getColor(this@MainActivity,R.color.color_red))
            } else if (!isEmailValid(etMail?.text.toString())) {
                noError = false
                etMail!!.text.clear()
                etMail!!.hint = "Enter valid email"
                etMail!!.setHintTextColor(ContextCompat.getColor(this@MainActivity,R.color.color_red))
            }

            if (etNumber!!.text.isEmpty()) {
                noError = false
                etNumber!!.setHintTextColor(ContextCompat.getColor(this@MainActivity,R.color.color_red))
            }else if(etNumber!!.text.toString().length < 10){
                noError = false
                etMail!!.hint = "Enter valid number"
                etNumber!!.setHintTextColor(ContextCompat.getColor(this@MainActivity,R.color.color_red))
            }

            if(noError){
                rvEmail.visibility = View.VISIBLE
                textTitle.visibility = View.VISIBLE
                noData.visibility = View.GONE
                when {
                    checkExistMail() -> {
                        Snackbar.make(etMail!!,"Someone already owned your this mail id",Snackbar.LENGTH_SHORT).show()
                    }
                    checkExistNumber() -> {
                        Snackbar.make(etMail!!,"Someone already owned your this Number",Snackbar.LENGTH_SHORT).show()
                    }
                    else -> {
                        emailList.add(
                            EmailModelClass(
                                etMail!!.text.toString(),
                                etNumber!!.text.toString()
                            )
                        )
                        adapter.mList = emailList
                        adapter.notifyDataSetChanged()
                        etNumber!!.setText("")
                        etMail!!.setText("")
                        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(btn.windowToken, 0)
                        /*rvEmail.scrollToPosition(adapter.itemCount)*/
                        scrolllView.post(Runnable { scrolllView.fullScroll(View.FOCUS_DOWN) })
                        saveInPreference(emailList)
                    }
                }
            }
        }
    }

    private fun checkExistMail() : Boolean{
        var isExist = false
        for (email in emailList){
            isExist = email.email == etMail!!.text.toString()
        }
        return isExist
    }


    private fun checkExistNumber() : Boolean{
        var isExist = false
        for (email in emailList){
            isExist = email.number == etNumber!!.text.toString()
        }
        return isExist
    }

    private fun saveInPreference(emailList: ArrayList<EmailModelClass>) {
        val prefsEditor: SharedPreferences.Editor = appSharedPrefs!!.edit()
        val json: String = gson.toJson(emailList)
        prefsEditor.putString(KEY, json)
        prefsEditor.commit()
    }

    fun isEmailValid(email: String): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

}