package tech.berjis.mwakazyadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AllCategoriesActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    RecyclerView categoryRecycler;
    DatabaseReference dbRef;

    List<Categories> categoriesList;
    AllCategoriesAdapter allCategoriesAdapter;
    SearchableSpinner categoryPer;

    String per = "", categoryId = "";

    ImageView newCategory;
    EditText categoryName, categoryPrice;
    ImageView categoryImage;
    ConstraintLayout pageConstraint, newConstraint;
    TextView saveCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_categories);

        initVars();
    }

    private void initVars() {

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.keepSynced(true);

        categoryPer = findViewById(R.id.categoryPer);
        categoryRecycler = findViewById(R.id.categoryRecycler);
        pageConstraint = findViewById(R.id.pageConstraint);
        newConstraint = findViewById(R.id.newConstraint);
        newCategory = findViewById(R.id.newCategory);
        categoryName = findViewById(R.id.categoryName);
        categoryPrice = findViewById(R.id.categoryPrice);
        categoryImage = findViewById(R.id.categoryImage);
        saveCategory = findViewById(R.id.saveCategory);


        loadCategories();
        loadSpinner();
        setOnClicks();
    }

    private void setOnClicks() {
        newCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageConstraint.setVisibility(View.GONE);
                newConstraint.setVisibility(View.VISIBLE);
                categoryId = String.valueOf(System.currentTimeMillis() / 1000L);
            }
        });
        categoryPer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                per = categoryPer.getSelectedItem().toString();
                Toast.makeText(AllCategoriesActivity.this, per, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        categoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        saveCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCategory();
            }
        });
    }

    private void saveCategory() {
        dbRef.child("Categories").child(categoryId).child("name").setValue(categoryName.getText().toString());
        dbRef.child("Categories").child(categoryId).child("price").setValue(Long.parseLong(categoryPrice.getText().toString()));
        dbRef.child("Categories").child(categoryId).child("per").setValue(per);
        newConstraint.setVisibility(View.GONE);
        loadCategories();
        pageConstraint.setVisibility(View.VISIBLE);
    }

    private void selectImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                postImage(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Error :" + error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void postImage(Uri filePath) {
        long unixTime = System.currentTimeMillis() / 1000L;
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        if (filePath != null) {

            final StorageReference ref = FirebaseStorage.getInstance().getReference().child("Category Images/" + categoryId + unixTime + ".jpg");
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    final String image_url = downloadUrl.toString();
                                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                                    dbRef.child("Categories").child(categoryId).child("image").setValue(image_url);
                                    Picasso.get().load(image_url).into(categoryImage);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AllCategoriesActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        } else {
            progressDialog.dismiss();
        }
    }

    private void loadSpinner() {
        List<String> perList = new ArrayList<>();
        perList.add("Choose Unit");
        perList.add("N/A");
        perList.add("Kilo");
        perList.add("Hour");
        perList.add("Hour");
        perList.add("Hour");
        perList.add("Hour");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, perList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryPer.setAdapter(adapter);
    }

    private void loadCategories() {
        categoryRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        categoriesList = new ArrayList<>();
        dbRef.child("Categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoriesList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Categories l = snap.getValue(Categories.class);
                    categoriesList.add(l);
                }
                Collections.shuffle(categoriesList);
                allCategoriesAdapter = new AllCategoriesAdapter(AllCategoriesActivity.this, categoriesList);
                categoryRecycler.setAdapter(allCategoriesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}