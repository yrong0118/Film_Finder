package com.yue.mymovie.LoginOrRegister


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.Nullable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.yue.mymovie.MainActivity

import com.yue.mymovie.R
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_register.*

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {
    lateinit var email : EditText
    lateinit var passWord: EditText
    lateinit var submitBtn: Button
    lateinit var goToRegister: TextView
    private lateinit var progressBar: ProgressBar
    lateinit var mCallback: GoToResListener
    private lateinit var firebaseAuth: FirebaseAuth

    // Container Activity must implement this interface
    interface GoToResListener {

        fun goToResListener()
    }

    companion object {

        val TAG = "Login Frafment"
        fun newInstance(): LoginFragment {
            var args = Bundle()
            var fragment = LoginFragment()
            fragment.setArguments(args)
            return fragment
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "destroy")
        if (email.text.toString() != null ) {
            email.clearFocus()
            passWord.clearFocus()
            if (textWatcher != null) {
                email.removeTextChangedListener(textWatcher)
                passWord.removeTextChangedListener(textWatcher)
                textWatcher = null
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_login, container, false)
        email = view.findViewById(R.id.email_edittext_login)
        passWord = view.findViewById(R.id.password_edittext_login)
        submitBtn = view.findViewById(R.id.login_button_login)
        progressBar = view.findViewById(R.id.progressBar)
        goToRegister = view.findViewById(R.id.back_to_register_textview)

        val preferences: SharedPreferences = this.activity!!.getSharedPreferences("mymovie", Context.MODE_PRIVATE)

        email.setText(preferences.getString("SAVED_USERNAME", ""))
        passWord.setText(preferences.getString("SAVED_PASSWORD",""))
        email.addTextChangedListener(textWatcher!!)
        passWord.addTextChangedListener(textWatcher!!)

        progressBar.visibility = View.INVISIBLE

        firebaseAuth = FirebaseAuth.getInstance()

        submitBtn.setOnClickListener {
            //            sleep(1000)

            performLogin(preferences)

        }
        goToRegister.setOnClickListener {
            Log.d("Register Fragment", "Go to Register Fragment")
            mCallback.goToResListener()

        }
        return view
    }

    private fun performLogin(preferences: SharedPreferences) {
        // firebaseAnalytics.logEvent("login_clicked", null)
        Log.d(TAG, "Email is: " + email.text.toString())
        Log.d(TAG, "Password is: " + password_edittext_login.text.toString())
        progressBar.visibility = View.VISIBLE

        val inputtedUserEmail: String = email_edittext_login.text.toString().trim()
        val inputtedPassword: String = passWord.text.toString()

        firebaseAuth
            .signInWithEmailAndPassword(inputtedUserEmail, inputtedPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser: FirebaseUser? = firebaseAuth.currentUser
                    val currUserEmail = currentUser?.email
                    Toast.makeText(this.activity, "Logged in as: $currUserEmail", Toast.LENGTH_SHORT).show()

                    // Save the inputted username to file
                    preferences
                        .edit()
                        .putString("SAVED_USEREMAIL", inputtedUserEmail)
                        .apply()
                    preferences
                        .edit()
                        .putString("SAVED_PASSWORD", inputtedPassword)
                        .apply()

                    val intent = Intent(this.activity, MainActivity::class.java)
                    startActivity(intent)

                } else {
                    val exception = task.exception

                    // Example of logging some extra metadata (the error reason) with our analytic
                    val reason =
                        if (exception is FirebaseAuthInvalidCredentialsException) "invalid_credentials" else "connection_failure"
                    val bundle = Bundle()
                    bundle.putString("error_type", reason)

                    Toast.makeText(this.activity, "Login failed: $exception", Toast.LENGTH_SHORT)
                        .show()

                    progressBar.visibility = View.INVISIBLE

                }
            }
    }

    private var textWatcher: TextWatcher? = object :TextWatcher{

        override fun afterTextChanged(p0: Editable?) {
            if (isResumed){
                val inputtedUsername: String = email.text.toString().trim()
                val inputtedPassword: String = passWord.text.toString()
                val enabled: Boolean = inputtedUsername.isNotEmpty() && inputtedPassword.isNotEmpty()

                // Kotlin shorthand for login.setEnabled(enabled)
                submitBtn.isEnabled = enabled
                goToRegister.isEnabled = enabled
            }
            //https://www.jianshu.com/p/dee22b05bf66
        }

        override fun beforeTextChanged(p0: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(newString: CharSequence, start: Int, before: Int, count: Int) {

        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mCallback = context as GoToResListener
        } catch (e: ClassCastException) {
            //
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG,"OnResume")
    }


}
