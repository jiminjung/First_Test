package com.example.firsttest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.firsttest.databinding.ActivitySignUpBinding;

import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private ActivitySignUpBinding binding;
    RequestQueue queue;
    boolean canRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        queue = Volley.newRequestQueue(SignUpActivity.this);

        binding.imageValid.setVisibility(View.INVISIBLE);
        binding.imageInvalid.setVisibility(View.INVISIBLE);
        canRegister = false;

        binding.signupID.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    public void onResponse(String response) {
                        try {
                            // String으로 그냥 못 보냄으로 JSON Object 형태로 변형하여 전송
                            // 서버 통신하여 회원가입 성공 여부를 jsonResponse로 받음
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean isValid = jsonResponse.getBoolean("isValid");

                            if (isValid) {
                                binding.imageValid.setVisibility(View.VISIBLE);
                                binding.imageInvalid.setVisibility(View.INVISIBLE);
                                binding.signupID.setTextColor(Color.parseColor("#2C9C2A"));
                                canRegister = true;
                            } else {
                                binding.imageValid.setVisibility(View.INVISIBLE);
                                binding.imageInvalid.setVisibility(View.VISIBLE);
                                binding.signupID.setTextColor(Color.RED);
                                canRegister = false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                };
                String id = charSequence.toString();
                ValidateRequest validateRequest = new ValidateRequest(id, responseListener);
                queue.add(validateRequest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String id = binding.signupID.getText().toString();
                String password = binding.signupPW.getText().toString();
                String confirm_password = binding.confirmPW.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    public void onResponse(String response) {
                        try {
                            // String으로 그냥 못 보냄으로 JSON Object 형태로 변형하여 전송
                            // 서버 통신하여 회원가입 성공 여부를 jsonResponse로 받음
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) { // 회원가입이 가능한다면
                                Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();//액티비티를 종료시킴(회원등록 창을 닫음)
                            } else {// 회원가입이 안된다면
                                Toast.makeText(getApplicationContext(), "회원가입에 실패했습니다. 다시 한 번 확인해 주세요.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                if(password.equals(confirm_password) && canRegister) {
                    SignUpRequest signupRequest = new SignUpRequest(id, password, responseListener);
                    queue.add(signupRequest);
                }
                else if(!password.equals(confirm_password)){
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "아이디가 이미 존재합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}