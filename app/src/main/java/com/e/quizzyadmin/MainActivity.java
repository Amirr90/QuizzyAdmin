package com.e.quizzyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.e.quizzyadmin.Adapter.CategoryAdapter;
import com.e.quizzyadmin.Adapter.HomeAdapter0;
import com.e.quizzyadmin.Model.CategoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;
    private String TAG = "MainActivity";
    Button update_played_count_btn;
    RecyclerView recyclerView;
    CategoryAdapter adapter;
    // List<List<CategoryModel>> categoryModels;
    List<CategoryModel> categoryModels;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        db = FirebaseFirestore.getInstance();

        update_played_count_btn = (Button) findViewById( R.id.played_count_btn );
        progressBar = (ProgressBar) findViewById( R.id.progressBar );


        /*updateSetsNumber( "CATEGORIES" );
        updateQuestion( "CATEGORIES" );

        updateSetsNumber( "Education Quiz" );
        updateQuestion( "Education Quiz" );

        updateSetsNumber( "RANDOM QUIZ" );
        updateQuestion( "RANDOM QUIZ" );*/

        //setAllSetActiveStatus(false);

        //setAllSetTimestamp();

        categoryModels = new ArrayList<>();
        adapter = new CategoryAdapter( categoryModels, this );
        recyclerView = (RecyclerView) findViewById( R.id.cat_rec );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        recyclerView.setAdapter( adapter );
        loadData( adapter, categoryModels );

    }


    public void setProgressBar(boolean status) {
        progressBar.setVisibility( status ? View.VISIBLE : View.GONE );
        recyclerView.setVisibility( status ? View.GONE : View.VISIBLE );

    }

    private void loadData(final CategoryAdapter adapter, final List<CategoryModel> categoryModels) {

        db.collection( "CATEGORIES" )
                .get()
                .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText( MainActivity.this, "Category: " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
                            return;
                        }
                        QuerySnapshot snapshots = task.getResult();
                        for (int a = 0; a < snapshots.size(); a++) {
                            String id = snapshots.getDocuments().get( a ).getId();
                            String title = (String) snapshots.getDocuments().get( a ).get( "title" );
                            String image = (String) snapshots.getDocuments().get( a ).get( "image" );
                            boolean isActive = (Boolean) snapshots.getDocuments().get( a ).get( "isActive" );
                            long question = (Long) snapshots.getDocuments().get( a ).get( "question" );
                            long sets = (Long) snapshots.getDocuments().get( a ).get( "sets" );
                            boolean showOnHomePage = (Boolean) snapshots.getDocuments().get( a ).get( "showOnHomePage" );
                            categoryModels.add( new CategoryModel( title, image, isActive, question, sets, showOnHomePage, "CATEGORIES", id ) );
                            adapter.notifyDataSetChanged();
                        }
                    }
                } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( MainActivity.this, "Category: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );


        db.collection( "RANDOM QUIZ" )
                .get()
                .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText( MainActivity.this, "Random Quiz: " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
                            return;
                        }
                        QuerySnapshot snapshots = task.getResult();
                        for (int a = 0; a < snapshots.size(); a++) {
                            String id = snapshots.getDocuments().get( a ).getId();
                            String title = (String) snapshots.getDocuments().get( a ).get( "title" );
                            String image = (String) snapshots.getDocuments().get( a ).get( "image" );
                            boolean isActive = (Boolean) snapshots.getDocuments().get( a ).get( "isActive" );
                            long question = (Long) snapshots.getDocuments().get( a ).get( "question" );
                            long sets = (Long) snapshots.getDocuments().get( a ).get( "sets" );
                            boolean showOnHomePage = (Boolean) snapshots.getDocuments().get( a ).get( "showOnHomePage" );
                            categoryModels.add( new CategoryModel( title, image, isActive, question, sets, showOnHomePage, "RANDOM QUIZ", id ) );
                        }
                        adapter.notifyDataSetChanged();
                    }
                } );

        db.collection( "Education Quiz" )
                .get()
                .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText( MainActivity.this, "error: " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
                            return;
                        }
                        QuerySnapshot snapshots = task.getResult();
                        for (int a = 0; a < snapshots.size(); a++) {
                            String id = snapshots.getDocuments().get( a ).getId();
                            String title = (String) snapshots.getDocuments().get( a ).get( "title" );
                            String image = (String) snapshots.getDocuments().get( a ).get( "image" );
                            boolean isActive = (Boolean) snapshots.getDocuments().get( a ).get( "isActive" );
                            long question = (Long) snapshots.getDocuments().get( a ).get( "question" );
                            long sets = (Long) snapshots.getDocuments().get( a ).get( "sets" );
                            boolean showOnHomePage = (Boolean) snapshots.getDocuments().get( a ).get( "showOnHomePage" );
                            categoryModels.add( new CategoryModel( title, image, isActive, question, sets, showOnHomePage, "Education Quiz", id ) );
                        }
                        adapter.notifyDataSetChanged();
                        setProgressBar( false );
                    }
                } );


    }


    private void setAllSetTimestamp() {
        final Map<String, Object> map = new HashMap<>();

        db.collection( "Sets" )
                .get().addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot snapshots = task.getResult();
                    for (int a = 0; a < snapshots.size(); a++) {
                        map.put( "timestamp", System.currentTimeMillis() );
                        String setId = snapshots.getDocuments().get( a ).getId();
                        db.collection( "Sets" ).document( setId )
                                .update( map );

                    }
                }
            }
        } );


    }

    private void setAllSetActiveStatus(boolean status) {
        final Map<String, Object> map = new HashMap<>();
        map.put( "isActive", status );
        db.collection( "Sets" )
                .get().addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot snapshots = task.getResult();
                    for (int a = 0; a < snapshots.size(); a++) {
                        String setId = snapshots.getDocuments().get( a ).getId();
                        db.collection( "Sets" ).document( setId )
                                .update( map );

                    }
                }
            }
        } );


    }

    private void updateQuestion(final String ref) {

        final StringBuilder builder = new StringBuilder();

        //getting Ids
        db.collection( ref )
                .get()
                .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (!task.isSuccessful()) {
                            return;
                        }
                        QuerySnapshot snapshots = task.getResult();
                        for (int a = 0; a < snapshots.size(); a++) {
                            String cat_id = snapshots.getDocuments().get( a ).getId();
                            String category = (String) snapshots.getDocuments().get( a ).get( "title" );
                            builder.append( category + " " );

                            getQuestionCount( category, builder, cat_id, ref );

                        }
                        //  Toast.makeText( MainActivity.this, "Completed", Toast.LENGTH_SHORT ).show();

                    }

                } );

    }

    private void updateSetsNumber(final String ref) {
        final StringBuilder builder = new StringBuilder();

        //getting Ids
        db.collection( ref )
                .get()
                .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (!task.isSuccessful()) {
                            Toast.makeText( MainActivity.this, "id not matched", Toast.LENGTH_SHORT ).show();
                            return;
                        }
                        QuerySnapshot snapshots = task.getResult();
                        for (int a = 0; a < snapshots.size(); a++) {
                            String cat_id = snapshots.getDocuments().get( a ).getId();
                            String category = (String) snapshots.getDocuments().get( a ).get( "title" );
                            builder.append( category + " " );

                            getSetsCount( category, builder, cat_id, ref );
                        }

                    }

                } );

    }

    private void getSetsCount(final String category, final StringBuilder builder, final String cat_id, final String ref) {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection( "Sets" )
                .whereEqualTo( "isActive", true )
                .whereEqualTo( "category", category )
                .addSnapshotListener( new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        Map<String, Object> map = new HashMap<>();
                        int size;
                        if (queryDocumentSnapshots.isEmpty()) {
                            size = 0;

                        } else {
                            size = queryDocumentSnapshots.size();
                        }


                        map.put( "sets", size );
                        db.collection( ref )
                                .document( cat_id )
                                .update( map )
                                .addOnSuccessListener( new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d( TAG, "onSuccess: Sets" );
                                        // getQuestionCount(  );
                                    }
                                } ).addOnFailureListener( new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e( TAG, "onFailure: sets" );
                            }
                        } );


                    }
                } );

    }


    private void getQuestionCount(final String category, final StringBuilder builder, final String cat_id, final String ref) {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection( "Question" )
                .whereEqualTo( "isActive", true )
                .whereEqualTo( "category", category )
                .addSnapshotListener( new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        Map<String, Object> map = new HashMap<>();
                        int size;
                        if (queryDocumentSnapshots.isEmpty()) {
                            size = 0;

                        } else {
                            size = queryDocumentSnapshots.size();
                        }
                        builder.append( category + ": size: " + size );
                        builder.append( " \n" );

                        map.put( "question", size );
                        db.collection( ref )
                                .document( cat_id )
                                .update( map )
                                .addOnSuccessListener( new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d( TAG, "onSuccess: Que" );
                                    }
                                } ).addOnFailureListener( new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e( TAG, "onFailure: Que" );
                            }
                        } );


                    }
                } );

    }

    public void fun() {
        /*db.collection( "Sets" )
                .whereEqualTo( "isActive",true )
                .whereEqualTo( "category",category )
                .addSnapshotListener( new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if (!queryDocumentSnapshots.isEmpty()){
                            for (int a=0;a<queryDocumentSnapshots.size();a++){
                                String setID=queryDocumentSnapshots.getDocuments().get( a ).getId();

                                db.collection( "Question" )
                                        .whereEqualTo( "isActive", true )
                                        .whereEqualTo( "category", category )
                                        .whereEqualTo( "setId",setID )
                                        .addSnapshotListener( new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                Map<String, Object> map = new HashMap<>();
                                                int size;
                                                if (queryDocumentSnapshots.isEmpty()) {
                                                    size = 0;

                                                } else {
                                                    size = queryDocumentSnapshots.size();
                                                }
                                                builder.append( category + ": size: " + size );
                                                builder.append( " \n" );

                                                map.put( "question", size );
                                                db.collection( ref )
                                                        .document( cat_id )
                                                        .update( map )
                                                        .addOnSuccessListener( new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d( TAG, "onSuccess: Que" );
                                                            }
                                                        } ).addOnFailureListener( new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.e( TAG, "onFailure: Que" );
                                                    }
                                                } );


                                            }
                                        } );
                            }
                        }
                    }
                } );*/
    }

    public void updatePlayedCount(View view) {
        update_played_count_btn.setText( "Updating" );
        db.collection( "Sets" )
                .get()
                .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshots = task.getResult();
                            for (int a = 0; a < snapshots.size(); a++) {
                                //Get Sets ID present in Sets Collection
                                String setId = snapshots.getDocuments().get( a ).getId();
                                checkSetsIdInPlayed( setId );

                            }
                        }
                    }

                    private void checkSetsIdInPlayed(final String setId) {
                        db.collection( "PLAYED_QUIZ" )
                                .whereEqualTo( "setId", setId )
                                .get()
                                .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            int playedSetsNumber = task.getResult().size();
                                            updateToSetsData( playedSetsNumber, setId );
                                        }
                                    }
                                } );
                    }

                    private void updateToSetsData(int playedSetsNumber, String setId) {
                        Map<String, Object> map = new HashMap<>();
                        map.put( "played", playedSetsNumber );

                        db.collection( "Sets" )
                                .document( setId )
                                .update( map )
                                .addOnSuccessListener( new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        update_played_count_btn.setText( "Updated Successfully" );
                                        update_played_count_btn.setEnabled( false );
                                    }
                                } ).addOnFailureListener( new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                update_played_count_btn.setText( " Not Completed" );
                            }
                        } );
                    }
                } );
    }
}
