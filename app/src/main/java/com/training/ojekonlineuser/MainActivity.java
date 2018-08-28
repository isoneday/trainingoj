package com.training.ojekonlineuser;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.training.ojekonlineuser.activity.HalamanUtamaActivity;
import com.training.ojekonlineuser.helper.HeroHelper;
import com.training.ojekonlineuser.helper.SessionManager;
import com.training.ojekonlineuser.model.Data;
import com.training.ojekonlineuser.model.ResponseLogin;
import com.training.ojekonlineuser.model.ResponseRegisterrobo;
import com.training.ojekonlineuser.network.InitRetrofit;
import com.training.ojekonlineuser.network.RestApi;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.txt_rider_app)
    TextView txtRiderApp;
    @BindView(R.id.btnSignIn)
    Button btnSignIn;
    @BindView(R.id.btnRegister)
    Button btnRegister;
    @BindView(R.id.rootlayout)
    RelativeLayout rootlayout;
    private SessionManager manager;

    //todo 1 generate butterknife
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED
                   ) {
                requestPermissions(
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        110);


            }
            return;
        }
    }

    @OnClick({R.id.btnSignIn, R.id.btnRegister})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSignIn:
                lgoinuser();
                break;
            case R.id.btnRegister:
                registeruser();
                break;
        }
    }

    private void lgoinuser() {
        final AlertDialog.Builder dialogregis = new AlertDialog.Builder(this);
        dialogregis.setTitle("login");
        LayoutInflater inflater = LayoutInflater.from(this);
        View tampilanlogin = inflater.inflate(R.layout.layout_register, null);
        final ViewHolder holder = new ViewHolder(tampilanlogin);
        holder.edtPhone.setVisibility(View.GONE);
        holder.edtName.setVisibility(View.GONE);


        dialogregis.setView(tampilanlogin);
        dialogregis.setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                //  dialogInterface.dismiss();
                //check validasi
                if (TextUtils.isEmpty(holder.edtEmail.getText().toString())) {
                    Snackbar.make(rootlayout, R.string.requireemail, Snackbar.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(holder.edtPassword.getText().toString())) {
                    Snackbar.make(rootlayout, R.string.requirepassword, Snackbar.LENGTH_SHORT).show();
                } else if (holder.edtPassword.getText().toString().length() < 6) {
                    Snackbar.make(rootlayout, R.string.minimumpassword, Snackbar.LENGTH_SHORT).show();
                } else {
                    // memasukkan data ke webservice
                    //todo 2 get instance atau inisialisasi retrofit
                    RestApi api = InitRetrofit.getintance();
                    String device = HeroHelper.getDeviceUUID(MainActivity.this);
                    Call<ResponseLogin> loginCall = api.loginuser(device, holder.edtEmail.getText().toString(),
                            holder.edtPassword.getText().toString());
                    loginCall.enqueue(new Callback<ResponseLogin>() {
                        @Override
                        public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                            dialogInterface.dismiss();
                            if (response.isSuccessful()) {
                                String result = response.body().getResult();
                                String msg = response.body().getMsg();
                                if (result.equals("true")) {
                                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                         startActivity(new Intent(MainActivity.this, HalamanUtamaActivity.class));
                                    manager = new SessionManager(MainActivity.this);
                                    Data datauser =response.body().getData();
                                    String token = response.body().getToken();
                                    manager.createLoginSession(token);
                                    manager.setEmail(datauser.getUserEmail());
                                    manager.setIduser(datauser.getIdUser());
                                    finish();
                                } else {
                                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseLogin> call, Throwable t) {
                            dialogInterface.dismiss();
                            Toast.makeText(MainActivity.this, "check ur connection" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

        });
        dialogregis.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogregis.show();

    }

    private void registeruser() {
        AlertDialog.Builder register = new AlertDialog.Builder(this);
        register.setTitle(R.string.register);
        register.setMessage(R.string.messageregister);
        LayoutInflater inflater = LayoutInflater.from(this);
        View tampilanregister = inflater.inflate(R.layout.layout_register, null);
        final ViewHolder holder = new ViewHolder(tampilanregister);
        //set view ke dialog
        register.setView(tampilanregister);
        register.setPositiveButton("submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String email = holder.edtEmail.getText().toString();
                String nama = holder.edtName.getText().toString();
                String password = holder.edtPassword.getText().toString();
                String phone = holder.edtPhone.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Snackbar.make(rootlayout, R.string.requireemail, Snackbar.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(nama)) {
                    Snackbar.make(rootlayout, R.string.requirename, Snackbar.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Snackbar.make(rootlayout, R.string.requirepassword, Snackbar.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(phone)) {
                    Snackbar.make(rootlayout, R.string.requirephone, Snackbar.LENGTH_SHORT).show();
                } else if (holder.edtPassword.getText().toString().length() < 6) {
                    Snackbar.make(rootlayout, R.string.minimumpassword, Snackbar.LENGTH_SHORT).show();
                } else {
                    prosesregister(email, nama, password, phone, dialogInterface);
                }
            }
        });
        register.show();

    }

    private void prosesregister(String email, String nama, String password, String phone, final DialogInterface dialogInterface) {
        final ProgressDialog builder = ProgressDialog.show(this, "proses register", "loading . . .");
        //call retrofit
        RestApi api = InitRetrofit.getintance();
        Call<ResponseRegisterrobo> registerroboCall = api.registeruser(
                nama, email, password, phone
        );
        registerroboCall.enqueue(new Callback<ResponseRegisterrobo>() {
            @Override
            public void onResponse(Call<ResponseRegisterrobo> call, Response<ResponseRegisterrobo> response) {
                if (response.isSuccessful()) {
                    builder.dismiss();
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")) {
                        Snackbar.make(rootlayout, msg, Snackbar.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    } else {
                        Snackbar.make(rootlayout, msg, Snackbar.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(MainActivity.this, "tidak ada response", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ResponseRegisterrobo> call, Throwable t) {
                dialogInterface.dismiss();
                builder.dismiss();
                Toast.makeText(MainActivity.this, "cek koneksi anda" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    static class ViewHolder {
        @BindView(R.id.edtEmail)
        MaterialEditText edtEmail;
        @BindView(R.id.edtPassword)
        MaterialEditText edtPassword;
        @BindView(R.id.edtName)
        MaterialEditText edtName;
        @BindView(R.id.edtPhone)
        MaterialEditText edtPhone;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
