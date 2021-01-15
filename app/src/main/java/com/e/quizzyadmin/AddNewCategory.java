package com.e.quizzyadmin;

import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class AddNewCategory extends AppCompatActivity {
    EditText title, number;

    String CAT_ID;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_new_category );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        title = (EditText) findViewById( R.id.editText );
        number = (EditText) findViewById( R.id.editText2 );
        progressBar=(ProgressBar)findViewById( R.id.progressBar2 ) ;

        setToolbar( toolbar, "Add New Set" );
        if (getIntent().hasExtra( "cat_id" )) {
            CAT_ID = getIntent().getStringExtra( "cat_id" );
        } else {
            finish();
        }

    }


    private void setToolbar(Toolbar toolbar, String id) {
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setDisplayShowHomeEnabled( true );
        getSupportActionBar().setTitle( id );
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void uploadNewSet(View view) {

        String mTitle = title.getText().toString();
        String mNumber = number.getText().toString();
        if (mTitle == null || mTitle.equals( "" )) {
            title.setError( "Required" );
        } else if (mNumber == null || mNumber.equals( "" )) {
            number.setError( "Required" );
        } else {
            progressBar.setVisibility( View.VISIBLE );
            Map<String, Object> map = new HashMap<>();
            map.put( "category", CAT_ID );
            map.put( "description", "" );
            map.put( "difficulty", 0 );
            map.put( "image", "" );
            map.put( "isActive", false );
            map.put( "isNew", false );
            map.put( "played", 0 );
            map.put( "questions", Long.parseLong( mNumber ) );
            map.put( "timestamp", System.currentTimeMillis() );
            map.put( "title", mTitle );
            Toast.makeText( this, "Ok" + mTitle, Toast.LENGTH_SHORT ).show();

            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection( "Sets" )
                    .add( map )
                    .addOnSuccessListener( new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String id = documentReference.getId();
                            Map<String, Object> map = new HashMap<>();
                            map.put( "setId", id );
                            db.collection( "Sets" )
                                    .document( id )
                                    .update( map ).addOnSuccessListener( new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText( AddNewCategory.this, "New Set Added Successfully", Toast.LENGTH_SHORT ).show();
                                    progressBar.setVisibility(View.GONE);
                                    finish();
                                }
                            } );
                        }
                    } ).addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText( AddNewCategory.this, "can't Add new set", Toast.LENGTH_SHORT ).show();
                    progressBar.setVisibility(View.GONE);
                }
            } );


        }

    }

}
