package id.net.gmedia.absensipsp;

/**
 * Created by Bayu on 29/12/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SessionManager {
	// Shared Preferences
	private SharedPreferences pref;

	// Editor for Shared preferences
	private Editor editor;

	// Context
	private Context _context;

	// Shared pref mode

	private int PRIVATE_MODE = 0;

	// Sharedpref file name
	private static final String PREF_NAME = "AndroidHivePref";
	private static final String CHECK_NAME = "check";

	// All Shared Preferences Keys
	private static final String IS_LOGIN = "IsLoggedIn";

	// User name (make variable public to access from outside)
	public static final String KEY_NAME = "name";
	public static final String KEY_TOKEN = "token";
	public static final String KEY_NIP = "Nip";
	public static final String KEY_APPROVAL = "approval";

	// Email address (make variable public to access from outside)
	public static final String KEY_EMAIL = "email";

	private static final String FCM_ID = "fcm";

	// Constructor
	public SessionManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	/**
	 * Create login session
	 */
	public void createLoginSession(String name, String email, String token, String Nip, String approval) {
		// Storing login value as TRUE
		editor.putBoolean(IS_LOGIN, true);
		// Storing name in pref
		editor.putString(KEY_NAME, name);
		// Storing email in pref
		editor.putString(KEY_EMAIL, email);
		// Storing token in pref
		editor.putString(KEY_TOKEN, token);
		editor.putString(KEY_NIP, Nip);
		editor.putString(KEY_APPROVAL, approval);
		// commit changes
		editor.commit();
	}

	public String getToken() {
		return pref.getString(KEY_TOKEN, "");
	}

	public String getNip() {
		return pref.getString(KEY_NIP, "");
	}

	public String getApproval() {
		return pref.getString(KEY_APPROVAL, "");
	}

	public void logoutUser() {
		// Clearing all data from Shared Preferences
//		editor.clear();
		editor.putBoolean(IS_LOGIN, false);
		// Storing name in pref
		editor.putString(KEY_NAME, "");
		// Storing email in pref
		editor.putString(KEY_EMAIL, "");
		// Storing token in pref
		editor.putString(KEY_TOKEN, "");
		editor.putString(KEY_NIP, "");
		editor.putString(KEY_APPROVAL, "");
//		editor.putString(KEY_PIN, "");
		editor.commit();

		// After logout redirect user to Loing Activity
		Intent i = new Intent(_context, Login.class);
		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		// Staring Login Activity
		_context.startActivity(i);
	}

	public boolean isLoggedIn() {
		return pref.getBoolean(IS_LOGIN, false);
	}

	public static void setFcm(Context context, String fcm){
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putString(FCM_ID, fcm);
		editor.apply();
	}

	public static String getFcm(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context).getString(FCM_ID, "");
	}
}