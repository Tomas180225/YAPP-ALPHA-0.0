package com.proyect.yapp_alpha_00;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class resetPasswordActivity extends AppCompatActivity {

    private EditText correoReset;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        correoReset = findViewById(R.id.txtCorreoResetContra);
        Button enviarCorreo = findViewById(R.id.botonResetContra);
        firebaseAuth = FirebaseAuth.getInstance();

        enviarCorreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correo = correoReset.getText().toString();
                if(correo.isEmpty())
                    Toast.makeText(resetPasswordActivity.this, "Por favor ingresa un correo válido", Toast.LENGTH_SHORT).show();
                else
                    firebaseAuth.sendPasswordResetEmail(correo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(resetPasswordActivity.this, "Te hemos enviado un correo, por favor verifica tu bandeja de entrada", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(resetPasswordActivity.this, LoginActivity.class));
                            }

                            else {
                                String Error = task.getException().getMessage();
                                Toast.makeText(resetPasswordActivity.this, "Ha ocurrido un error: "+Error, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
            }
        });

    }
}
