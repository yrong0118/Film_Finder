package com.yue.mymovie.LoginOrRegister


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.yue.mymovie.MainActivity

import com.yue.mymovie.R
import kotlinx.android.synthetic.main.fragment_register.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class RegisterFragment : Fragment() {
    lateinit var userNameEt: EditText
    lateinit var emailEt : EditText
    lateinit var passWordEt: EditText
    lateinit var registerBtn: Button
    lateinit var hasAcound: TextView
    lateinit var selectedPhotoBtn: Button
    lateinit var mCallback: GoToLoginListener

    // Container Activity must implement this interface
    interface GoToLoginListener {

        fun goToLoginListener()
    }

    companion object {

        val TAG = "Register Frafment"
        fun newInstance(): RegisterFragment {
            var args = Bundle()
            var fragment = RegisterFragment()
            fragment.setArguments(args)
            return fragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_register, container, false)

        userNameEt = view.findViewById(R.id.username_edittext_register)
        emailEt = view.findViewById(R.id.email_edittext_register)
        passWordEt = view.findViewById(R.id.password_edittext_register)
        hasAcound = view.findViewById(R.id.already_have_account_text_view)
        registerBtn = view.findViewById(R.id.register_button_register)
        selectedPhotoBtn = view.findViewById(R.id.selectphoto_button_register)

        registerBtn.setOnClickListener {

            performRegister()

        }
        hasAcound.setOnClickListener {
            Log.d(TAG, "Go to Register Fragment")
            mCallback.goToLoginListener()

        }
//        hasAcound.setOnHoverListener { v, event ->
//            Log.d("Register Fragment", "Hover")
//        }

        selectedPhotoBtn.setOnClickListener {
            Log.d(TAG, "Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
        return view
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was....
            Log.d(TAG, "Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, selectedPhotoUri)

//            val bitmapDrawable = BitmapDrawable(bitmap)
//            selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)

            selectphoto_imageview_register.setImageBitmap(bitmap)
            selectphoto_button_register.alpha = 0f

        }
    }


    fun performRegister(){
        var username = userNameEt.text.toString().trim()
        var email = emailEt.text.toString().trim()
        var password = passWordEt.text.toString()
        Log.d(TAG, "Email is: " + email)
        Log.d(TAG, "Password is: " + password_edittext_register.text.toString())
        if (email.isEmpty() || password.isEmpty()|| username.isEmpty()) {
            Toast.makeText(this.activity, "Please enter text in username/email/password", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d(TAG, "Attempting to create user with email: $email")

        // Firebase Authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                // else if successful
                Log.d(TAG, "Successfully created user with uid: ${it.result!!.user!!.uid}")

                uploadImageToFirebaseStorage()

            }
            .addOnFailureListener{
                Log.d(TAG, "Failed to create user: ${it.message}")
                Toast.makeText(this.activity, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null){
            saveUserToFirebaseDatabase("")
            return
        }

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to upload image to storage: ${it.message}")
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {

        val uid = FirebaseAuth.getInstance().uid ?: ""

        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username_edittext_register.text.toString(), emailEt.text.toString(),profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Finally we saved the user to Firebase Database")

                val intent = Intent(this.activity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to set value to database: ${it.message}")
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            //mCallback initialize
            mCallback = context as GoToLoginListener
        } catch (e: ClassCastException) {
            //
        }
    }

}
