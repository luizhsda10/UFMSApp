package ufms.br.com.ufmsapp.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ufms.br.com.ufmsapp.MyApplication;
import ufms.br.com.ufmsapp.R;
import ufms.br.com.ufmsapp.gcm.UfmsListenerService;
import ufms.br.com.ufmsapp.network.VolleySingleton;
import ufms.br.com.ufmsapp.pojo.Aluno;
import ufms.br.com.ufmsapp.preferences.UserSessionPreference;
import ufms.br.com.ufmsapp.task.TaskLoadAlunos;
import ufms.br.com.ufmsapp.utils.PasswordEncryptionUtil;

public class LoginActivity extends AppCompatActivity {

    private static final String LOGIN_ACCESS_URL = "http://www.henriqueweb.com.br/webservice/access/userAccess.php?acao=login";
    private EditText emailLogin;
    private EditText passwordLogin;
    private ImageButton showPasswordImgButton;
    private ImageButton hidePasswordImgButton;
    private UserSessionPreference prefs;

    public static final int REQUEST_PLAY_SERVICES = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        startGooglePlayService();

        prefs = new UserSessionPreference(this);

        if (!prefs.isFirstTime()) {
            Intent intent = new Intent(getApplicationContext(),
                    MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        } else {
            new TaskLoadAlunos().execute();
        }


        emailLogin = (EditText) findViewById(R.id.login_email);
        passwordLogin = (EditText) findViewById(R.id.login_password);
        showPasswordImgButton = (ImageButton) findViewById(R.id.login_show_button);
        hidePasswordImgButton = (ImageButton) findViewById(R.id.login_hide_button);


        passwordLogin.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    showPasswordImgButton.setVisibility(View.VISIBLE);
                } else if (s.length() == 0) {
                    showPasswordImgButton.setVisibility(View.GONE);
                    hidePasswordImgButton.setVisibility(View.GONE);
                }
            }
        });

    }

    private void startGooglePlayService() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int resultCode = api.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (api.isUserResolvableError(resultCode)) {
                Dialog dialog = api.getErrorDialog(this, resultCode, REQUEST_PLAY_SERVICES);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        finish();
                    }
                });
                dialog.show();
            } else {
                Toast.makeText(this, R.string.gcm_nao_suportado,
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Intent it = new Intent(this, UfmsListenerService.class);
            it.putExtra(UfmsListenerService.EXTRA_REGISTRAR, true);
            startService(it);
        }
    }


    public void userCreateAccount(View view) {
        startActivity(new Intent(this, RegistrarActivity.class));
        finish();
    }

    public void userLogin(View view) {
        if (validateForm()) {

            final StringRequest postRequest = new StringRequest(Request.Method.POST, LOGIN_ACCESS_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            JSONObject jsonResponse;

                            try {
                                jsonResponse = new JSONObject(response);

                                if (jsonResponse.getInt("result") == 1) {

                                    Aluno aluno = MyApplication.getWritableDatabase().alunoByEmail(emailLogin.getText().toString());

                                    prefs.setName(aluno.getNome());
                                    prefs.setEmail(aluno.getEmail());
                                    prefs.setOld(true);

                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                } else {
                                    Toast.makeText(LoginActivity.this, "Login Inválido", Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error.Response", error.getMessage());
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {

                    String salt = "Random$SaltValue#WithSpecialCharacters12@$@4&#%^$*";
                    String password = PasswordEncryptionUtil.md5(passwordLogin.getText().toString().concat(salt));

                    Map<String, String> params = new HashMap<>();
                    params.put("email", emailLogin.getText().toString());
                    params.put("password", password);

                    return params;
                }
            };

            VolleySingleton.getInstance().getRequestQueue().add(postRequest);
        }
    }


    public void showHidePassword(View view) {
        switch (view.getId()) {
            case R.id.login_show_button:
                showPasswordImgButton.setVisibility(View.GONE);
                hidePasswordImgButton.setVisibility(View.VISIBLE);
                passwordLogin.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                break;
            case R.id.login_hide_button:
                hidePasswordImgButton.setVisibility(View.GONE);
                showPasswordImgButton.setVisibility(View.VISIBLE);
                passwordLogin.setTransformationMethod(PasswordTransformationMethod.getInstance());
                break;
        }
    }

    private boolean validateForm() {

        boolean isFieldSet;

        if (TextUtils.isEmpty(emailLogin.getText().toString())) {
            emailLogin.setError("Por favor, digite um email");
            isFieldSet = false;
        } else if (!emailLogin.getText().toString().contains("@")) {
            emailLogin.setError("Por favor, digite uma email válido. Geralmente inclui um caractere '@'");
            isFieldSet = false;
        } else if (TextUtils.isEmpty(passwordLogin.getText().toString())) {
            passwordLogin.setError("Por favor, digite uma senha");
            isFieldSet = false;

        } else if (TextUtils.getTrimmedLength(passwordLogin.getText()) < 6) {
            passwordLogin.setError("Sua senha deve conter no mínimo 6 caracteres");
            isFieldSet = false;
        } else {
            isFieldSet = true;
        }

        return isFieldSet;

    }
}