package com.sandeveloper.notesapp.utils

import android.content.Context
import com.sandeveloper.notesapp.utils.Constants.PREFS_TOKEN_FILE
import com.sandeveloper.notesapp.utils.Constants.USER_TOKEN
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenManager @Inject constructor(@ApplicationContext context:Context) {

    //SHARED PREFERENCE HELPS TO STORE A TOKEN IN LOCAL STORAGE FOR ALL PAGES AND DIRECTS THE LOGGED IN STATE IF TOKEN IS PRESENT

    private var prefs = context.getSharedPreferences(PREFS_TOKEN_FILE,Context.MODE_PRIVATE)
    //saving token from Login and register Response se respective fragments
    fun saveToken(token: String?){
        val editor= prefs.edit()
        editor.putString(USER_TOKEN,token)
        editor.apply()
    }

    fun getToken(): String? {
        return prefs.getString(USER_TOKEN,null)

    }
}