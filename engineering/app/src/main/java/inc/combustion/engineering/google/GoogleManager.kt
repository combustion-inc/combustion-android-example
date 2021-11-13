package inc.combustion.engineering.google

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import inc.combustion.LOG_TAG
import java.util.concurrent.atomic.AtomicBoolean

class GoogleManager(
    private val _app: Application,
    private val _requestGoogleSignIn: (Intent) -> Unit
) {
    private var _googleSignInClient : GoogleSignInClient
    private val _firebaseAuth = FirebaseAuth.getInstance()
    private val _onSignInSuccessList = mutableListOf<() -> Unit>()
    private var _onSignInSuccess: (() -> Unit)? = null

    companion object {
        private lateinit var INSTANCE: GoogleManager
        private val initialized = AtomicBoolean(false)

        val instance: GoogleManager get() = INSTANCE

        /*
         * Initializes the Google Manager instance with a reference to the Application context
         */
        fun initialize(application: Application, requestGoogleSignIn: (Intent) -> Unit) {
            if(!initialized.getAndSet(true)) {
                INSTANCE = GoogleManager(application, requestGoogleSignIn)
            }
        }
    }

    init {
        // Configure Google Sign-in Options
        val gso : GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("746307581755-1nlpkmc3sr8tnkfghnpdardjo1nk9rm7.apps.googleusercontent.com")
            .requestEmail()
            .requestScopes(
                Scope(Scopes.DRIVE_FILE),
                Scope(Scopes.DRIVE_APPS),
                Scope(Scopes.EMAIL),
                Scope(Scopes.PROFILE),
                Scope("https://www.googleapis.com/auth/spreadsheets")
            )
            .build()

        // Initialize the Google sign-in client
        _googleSignInClient = GoogleSignIn.getClient(_app.applicationContext, gso)

        // Check for previous sign in
        GoogleSignIn.getLastSignedInAccount(_app.applicationContext)?.let {
            signedIn = true
        }
    }

    var signedIn: Boolean = false
        private set

    val email = _firebaseAuth.currentUser?.email

    /**
     * Initiates Google sign-in
     *
     * @param completeCallback optional argument.  callback to the caller when
     *  sigin is successful
     */
    fun startSignIn(onSuccessCallback: (() -> Unit)? = null) {
        if(!signedIn) {
            _onSignInSuccess = onSuccessCallback
            _requestGoogleSignIn(_googleSignInClient.signInIntent)
        }
    }

    /**
     * Handles the result from the google sign in activity
     *
     * @param result activity result
     */
    fun signInActivityResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(result.data)

            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                _firebaseAuth.signInWithCredential(credential)
                    .addOnSuccessListener {
                        signedIn = true
                        val user = _firebaseAuth.currentUser

                        // if provided callback to the initiator
                        _onSignInSuccess?.let { it() }
                        _onSignInSuccessList.forEach { callback ->
                            callback()
                        }
                        Log.w(LOG_TAG, "Google sign in success! ${user?.email}")

                    }.addOnFailureListener { e ->
                        Log.w(LOG_TAG, "Google sign in failed", e)
                    }

            } catch (e: ApiException) {
                Log.w(LOG_TAG, "Google sign in failed", e)
            }
        } else {
            Log.w(LOG_TAG, "Google sign in failed")
        }
    }

    /**
     * Signs out of Google
     */
    fun signOut() {
        if(signedIn) {
            _googleSignInClient.signOut()
            signedIn = false
        }
    }

    /**
     * Registers function to be called back to on successful login
     *
     * @param onSuccessCallback the function
     */
    fun registerSignInSuccessHandler(onSuccessCallback: (() -> Unit)) {
        _onSignInSuccessList.add(onSuccessCallback)
    }
}