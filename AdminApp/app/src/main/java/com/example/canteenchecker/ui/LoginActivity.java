package com.example.canteenchecker.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.canteenchecker.adminapp.R;
import com.example.canteenchecker.common.StringUtils;
import com.example.canteenchecker.infrastructure.CanteenAdminApplication;
import com.example.canteenchecker.proxy.ServiceProxyFactory;
import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

  private static final String TAG = LoginActivity.class.getSimpleName();

  private EditText editUsername;
  private EditText editPassword;
  private ProgressBar progressBarLoading;
  private Button buttonLogin;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    editUsername = findViewById(R.id.editUsername);
    editPassword = findViewById(R.id.editPassword);
    buttonLogin = findViewById(R.id.buttonLogin);
    progressBarLoading = findViewById(R.id.progressBarLoading);
    resetFields();
    buttonLogin.setOnClickListener(this::onLogin);
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    setUIEnabled(true);
    resetFields();
    ((CanteenAdminApplication) getApplication()).clearAuthenticationToken(); //to be sure that the token is invalidated
  }

  private void resetFields() {
    editUsername.setText(null);
    editUsername.requestFocus();
    editPassword.setText(null);
    progressBarLoading.setVisibility(View.GONE);
  }

  @SuppressLint("StaticFieldLeak")
  private void onLogin(View view) {
    setUIEnabled(false);
    new AsyncTask<String, Void, String>() {

      @Override
      protected String doInBackground(String... parameters) {
        return login(parameters);
      }

      @Override
      protected void onPostExecute(String authenticationToken) {
        if(StringUtils.isNullOrEmpty(authenticationToken)) {
          setUIEnabled(true);
          editPassword.setText(null);
          Toast.makeText(LoginActivity.this, R.string.message_login_failed, Toast.LENGTH_SHORT).show();
        } else {
          ((CanteenAdminApplication) getApplication()).setAuthenticationToken(authenticationToken);
          Intent intent = new Intent();
          intent.setClass(getApplicationContext(), TabBarActivity.class);
          startActivity(intent);
        }
        progressBarLoading.setVisibility(View.GONE);
      }
    }.execute(editUsername.getText().toString(), editPassword.getText().toString());
  }

  private String login(String[] parameters) {
    try {
      return ServiceProxyFactory.create().authenticate(parameters[0], parameters[1]);
    } catch(IOException exception) {
      Log.e(TAG, String.format("%s %s", getResources().getString(R.string.message_authenticate_io_exception), parameters[0]), exception);
    }
    return null;
  }

  private void setUIEnabled(boolean enabled) {
    progressBarLoading.setVisibility(View.VISIBLE);
    buttonLogin.setEnabled(enabled);
    editUsername.setEnabled(enabled);
    editPassword.setEnabled(enabled);
  }


}