package com.proyect.yapp_alpha_00.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.proyect.yapp_alpha_00.Adapters.CategoriesAdapter;
import com.proyect.yapp_alpha_00.Adapters.CategoriesAdapterProfile;
import com.proyect.yapp_alpha_00.MainActivity;
import com.proyect.yapp_alpha_00.Model.Categories;
import com.proyect.yapp_alpha_00.Model.Post;
import com.proyect.yapp_alpha_00.Model.User;
import com.proyect.yapp_alpha_00.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    ImageView image_profile, options;
    EditText edit_name, edit_user, edit_cellphone;
    TextView name, username_p, username, cellphone, posts;
    Button button_edit;
    ImageButton edit_photo;
    RecyclerView recyclerView;
    CategoriesAdapterProfile categoriesAdapter;
    List<Categories> mCategorias;
    List<String> keys;

    FirebaseUser firebaseUser;
    String profileID;

    private Uri mImageUri;
    private StorageTask uploadTask;
    StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("subidos");

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        SharedPreferences categorias = getContext().getSharedPreferences("CATEGORIAS", Context.MODE_PRIVATE);
        profileID = prefs.getString("profileID","none");

        image_profile = view.findViewById(R.id.image_profile);
        name = view.findViewById(R.id.name);
        username_p = view.findViewById(R.id.username_p);
        username = view.findViewById(R.id.username);
        cellphone = view.findViewById(R.id.cellphone);
        posts = view.findViewById(R.id.posts);
        button_edit = view.findViewById(R.id.button_edit);
        button_edit.setTag("editar");

        edit_name = view.findViewById(R.id.edit_name);
        edit_user = view.findViewById(R.id.edit_user);
        edit_cellphone = view.findViewById(R.id.edit_cellphone);
        edit_name.setVisibility(View.GONE);
        edit_user.setVisibility(View.GONE);
        edit_cellphone.setVisibility(View.GONE);
        edit_photo = view.findViewById(R.id.edit_photo);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        mCategorias = new ArrayList<>();
        keys = new ArrayList<>();
        categoriesAdapter = new CategoriesAdapterProfile(getContext(), mCategorias);
        recyclerView.setAdapter(categoriesAdapter);

        userInfo();
        getPosts();

        edit_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1).start(getActivity());
            }
        });

        button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(button_edit.getTag().equals("editando")){
                    button_edit.setTag("editar");
                    edit_name.setVisibility(View.GONE);
                    edit_user.setVisibility(View.GONE);
                    edit_cellphone.setVisibility(View.GONE);
                    updateProfile(edit_user.getText().toString(), edit_name.getText().toString(), edit_cellphone.getText().toString());
                }
                else {
                    edit_name.setVisibility(View.VISIBLE);
                    edit_user.setVisibility(View.VISIBLE);
                    edit_cellphone.setVisibility(View.VISIBLE);
                    button_edit.setText("Listo!");
                    button_edit.setTag("editando");
                }
            }
        });

        return view;
    }

    private void updateProfile(String user, String name, String cellphone){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("nombre", name);
        hashMap.put("usuario", user);
        hashMap.put("telefono", cellphone);

        reference.updateChildren(hashMap);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void update_image(){
        ProgressDialog pd = new ProgressDialog(getContext());
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

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(firebaseUser.getUid());

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("img", myURL);

                        reference.updateChildren(hashMap);

                        pd.dismiss();
                    }
                    else {
                        Toast.makeText(getActivity(),"Algo salio mal :(", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(),e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(getActivity(),"No hay imagen", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.w("ESTADO cod", String.valueOf(requestCode));
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == getActivity().RESULT_OK){
            Log.w("ESTADO","SOUUUUUUUUUUUUUUUUUUUUUUUUUUU");
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();

            update_image();
        }
        else{
            Toast.makeText(getActivity(),"Algo salio mal :(", Toast.LENGTH_SHORT).show();
        }
    }

    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(profileID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(getContext() == null){
                    return;
                }
                User user = snapshot.getValue(User.class);
                Glide.with(getContext()).load(user.getImg()).into(image_profile);
                username_p.setText(user.getUsuario());
                username.setText(user.getUsuario());
                name.setText(user.getNombre());
                cellphone.setText(user.getTelefono());
                putCategories();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("publicaciones");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if(post.getUsuario().equals(profileID)){
                        i++;
                    }
                }

                posts.setText(String.valueOf(i));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void putCategories(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("seguir")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("siguiendo");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                keys.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    keys.add(dataSnapshot.getKey());
                }
                getCategories();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getCategories(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("categorias");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Categories categorie = dataSnapshot.getValue(Categories.class);
                    for(String key: keys){
                        Log.w("ESTADO LLAVE", key);
                        if(categorie.getcName().equals(key)){
                            mCategorias.add(categorie);
                        }
                    }
                }
                Collections.reverse(mCategorias);
                categoriesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}