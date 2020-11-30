package com.proyect.yapp_alpha_00;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText email, contrasena;
    Button iniciar, registrar;
    TextView recupContra;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        contrasena = findViewById(R.id.contraseña);
        iniciar = findViewById(R.id.iniciar);
        registrar = findViewById(R.id.registrar);
        recupContra = findViewById(R.id.txt_recuperarContraseña);

        auth = FirebaseAuth.getInstance();

        registrar.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        iniciar.setOnClickListener(v -> {
            ProgressDialog pd = new ProgressDialog(LoginActivity.this);
            pd.setMessage("Danos un momento");
            pd.show();

            String str_email = email.getText().toString().trim();
            String str_contrasena = contrasena.getText().toString().trim();

            if(TextUtils.isEmpty(str_email) ||	TextUtils.isEmpty(str_contrasena)){
                pd.dismiss();
                Toast.makeText(LoginActivity.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            }
            else{
                auth.signInWithEmailAndPassword(str_email, str_contrasena)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            if(task.isSuccessful()){
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Usuarios")
                                        .child(auth.getCurrentUser().getUid());

                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        pd.dismiss();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        pd.dismiss();
                                    }
                                });
                            }
                            else{
                                pd.dismiss();
                                Toast.makeText(LoginActivity.this, "Usuario o contraseña no validos", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        recupContra.setOnClickListener(v -> recupContra());

    }

    private void recupContra(){
        Intent intent = new Intent(LoginActivity.this, resetPasswordActivity.class);
        startActivity(intent);
    }
}