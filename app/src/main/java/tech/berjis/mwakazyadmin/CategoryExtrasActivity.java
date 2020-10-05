package tech.berjis.mwakazyadmin;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CategoryExtrasActivity extends AppCompatActivity {

    DatabaseReference dbRef;

    RecyclerView categoryExtraRecycler;
    TextView saveCategoryExtra;
    EditText categoryExtraName, categoryExtraPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_extras);

        initVars();
    }

    private void initVars() {
        dbRef = FirebaseDatabase.getInstance().getReference();

        categoryExtraRecycler = findViewById(R.id.categoryExtraRecycler);
    }
}