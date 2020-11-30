package com.proyect.yapp_alpha_00;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText usuario, nombre, telefono, email, contraseña, confirmar;
    Button registrar;
    TextView txt_inciar;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usuario = findViewById(R.id.usuario);
        nombre = findViewById(R.id.nombre);
        telefono = findViewById(R.id.telefono);
        email = findViewById(R.id.email);
        contraseña = findViewById(R.id.contraseña);
        confirmar = findViewById(R.id.confirmarContraseña);
        registrar = findViewById(R.id.registrar);
        txt_inciar = findViewById(R.id.txt_iniciar);

        auth = FirebaseAuth.getInstance();

        txt_inciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Danos un segundo");
                pd.show();

                String str_usuario = usuario.getText().toString().trim();
                String str_nombre = nombre.getText().toString().trim();
                String str_telefono = telefono.getText().toString().trim();
                String str_email = email.getText().toString().trim();
                String str_contraseña = contraseña.getText().toString().trim();
                String str_confirmar = confirmar.getText().toString().trim();

                if(TextUtils.isEmpty(str_usuario) || TextUtils.isEmpty(str_nombre)
                        || TextUtils.isEmpty(str_telefono) || TextUtils.isEmpty(str_email)
                        || TextUtils.isEmpty(str_contraseña) || TextUtils.isEmpty(str_confirmar)){
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                }
                else if(str_contraseña.length() < 8){
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "Minimo 8 caracteres en la contraseña", Toast.LENGTH_SHORT).show();
                }
                else if(!str_contraseña.equals(str_confirmar)){
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                }
                else{
                    Registrar(str_usuario, str_nombre, str_telefono, str_email, str_contraseña);
                }
            }
        });
    }
    private void Registrar(String usuario, String nombre, String telefono, String email, String contraseña){
        auth.createUserWithEmailAndPassword(email, contraseña)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userId = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(userId);

                            HashMap<String, Object> hashmap = new HashMap<>();
                            hashmap.put("id", userId);
                            hashmap.put("usuario", usuario.toLowerCase());
                            hashmap.put("nombre", nombre);
                            hashmap.put("telefono", telefono);
                            hashmap.put("bio", "");
                            hashmap.put("img", "https://firebasestorage.googleapis.com/v0/b/yapp-beta.appspot.com/o/usuario.png?alt=media&token=b3fb065b-ed83-4259-96a4-3cf6d31b4f3c");

                            reference.setValue(hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        pd.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this, SurveyActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                        else{
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, "Usuario ya registrado", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}