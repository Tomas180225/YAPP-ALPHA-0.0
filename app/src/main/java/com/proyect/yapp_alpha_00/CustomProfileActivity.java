package com.proyect.yapp_alpha_00;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.proyect.yapp_alpha_00.Adapters.CategoriesAdapterRegister;
import com.proyect.yapp_alpha_00.Adapters.CategoriesAdapterUpdate;
import com.proyect.yapp_alpha_00.Model.Categories;
import com.proyect.yapp_alpha_00.Model.User;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomProfileActivity extends AppCompatActivity {

    ImageView close, image_profile;
    TextView done, edit_photo;
    EditText edit_name, edit_user, edit_cellphone;

    FirebaseUser firebaseUser;

    private Uri mImageUri;
    private StorageTask uploadTask;
    StorageReference storageReference;

    RecyclerView recyclerView;
    List<Categories> categorias;
    List<String> myCategories;
    CategoriesAdapterUpdate categoriesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_profile);

        close = findViewById(R.id.close);
        image_profile = findViewById(R.id.image_profile);
        done = findViewById(R.id.done);
        edit_name = findViewById(R.id.edit_name);
        edit_user = findViewById(R.id.edit_user);
        edit_cellphone = findViewById(R.id.edit_cellphone);
        close = findViewById(R.id.close);
        edit_photo = findViewById(R.id.edit_photo);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        recyclerView.setLayoutManager(linearLayoutManager);
        categorias = new ArrayList<>();
        myCategories = new ArrayList<>();
        categoriesAdapter = new CategoriesAdapterUpdate(getApplicationContext(), categorias);
        recyclerView.setAdapter(categoriesAdapter);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("subidas");

        putCategories();

        Log.w("Estado", "Prueba 1");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.w("NUMERO", "1");
                User user = snapshot.getValue(User.class);
                edit_user.setText(user.getUsuario());
                edit_name.setText(user.getNombre());
                edit_cellphone.setText(user.getTelefono());
                Glide.with(CustomProfileActivity.this).load(user.getImg()).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edit_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1).start(CustomProfileActivity.this);
            }
        });

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1).start(CustomProfileActivity.this);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("SELECTED", MODE_PRIVATE);
                Map<String, ?> keys = preferences.getAll();
                for (Map.Entry<String, ?> entry : keys.entrySet()) {
                    myCategories.add(entry.getValue().toString());
                }
                if (keys.size() == 0) {
                    Toast.makeText(CustomProfileActivity.this, "Debes escoger por lo menos una categoria", Toast.LENGTH_SHORT).show();
                } else {
                    updateProfile(edit_user.getText().toString(), edit_name.getText().toString(), edit_cellphone.getText().toString(), myCategories);

                }
            }
        });

    }

    private void updateProfile(String user, String name, String cellphone, List<String> myCategories){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("nombre", name);
        hashMap.put("usuario", user);
        hashMap.put("telefono", cellphone);

        reference.updateChildren(hashMap);
        updateCategories(myCategories);
        finish();
    }

    private void updateCategories(List<String> myCategories){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("seguir").child(firebaseUser.getUid()).child("siguiendo");

        reference.removeValue();
        HashMap<String, Object> hashMap2 = new HashMap<String, Object>();
        for(int i = 0; i < myCategories.size(); i++){
            hashMap2.put((String) myCategories.get(i), true);
        }
        reference.updateChildren(hashMap2);
    }


    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void update_image(){
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Subiendo");
        pd.show();

        if(mImageUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(mImageUri));

            uploadTask = fileReference.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloarUri = task.getResult();
                        String myURL = downloarUri.toString();

                        Log.w("Estado", "Prueba 3");
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(firebaseUser.getUid());

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("img", ""+myURL);

                        reference.updateChildren(hashMap);

                        pd.dismiss();
                    }
                    else {
                        Toast.makeText(CustomProfileActivity.this,"Algo salio mal :(", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CustomProfileActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(CustomProfileActivity.this,"No hay imagen", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();

            update_image();
        }
        else{
            Toast.makeText(CustomProfileActivity.this,"Algo salio mal :(", Toast.LENGTH_SHORT).show();
        }
    }

    private void putCategories(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("categorias");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categorias.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Categories categorie = dataSnapshot.getValue(Categories.class);
                    categorias.add(categorie);
                }
                Collections.reverse(categorias);
                categoriesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}