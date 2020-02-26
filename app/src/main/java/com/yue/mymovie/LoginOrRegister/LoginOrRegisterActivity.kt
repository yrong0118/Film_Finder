package com.yue.mymovie.LoginOrRegister

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentTransaction
import com.yue.mymovie.R

class LoginOrRegisterActivity : AppCompatActivity(), RegisterFragment.GoToLoginListener,
    LoginFragment.GoToResListener {


    lateinit var registerFragment:RegisterFragment
    lateinit var loginFragment:LoginFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_or_register)

        registerFragment = RegisterFragment.newInstance()
        loginFragment = LoginFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.login_and_register_container, loginFragment).commit()

    }

    override fun goToLoginListener(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.login_and_register_container,loginFragment)
            .addToBackStack(loginFragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun goToResListener() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.login_and_register_container,registerFragment)
            .addToBackStack(registerFragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }
}
