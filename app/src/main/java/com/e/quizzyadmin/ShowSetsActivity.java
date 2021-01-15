package com.e.quizzyadmin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShowSetsActivity extends AppCompatActivity {

    String CAT_ID;
    Button addNewCategoryBtn;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    TextView setSetsCounttv;
    private String TAG = "ShowSetsActivity";
    List<DocumentSnapshot> setsList;
    SetsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_show_sets );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        addNewCategoryBtn = (Button) findViewById( R.id.played_count_btn );
        progressBar = (ProgressBar) findViewById( R.id.progressBar );
        setSetsCounttv = (TextView) findViewById( R.id.set_setsCount );

        if (getIntent().hasExtra( "cat_id" )) {
            CAT_ID = getIntent().getStringExtra( "cat_id" );
            showAllSets( CAT_ID );
            setToolbar( toolbar, CAT_ID );
        } else {
            finish();
        }


    }

    private void showAllSets(String cat_id) {
        setsList = new ArrayList<>();
        adapter = new SetsAdapter( setsList, this );
        recyclerView = (RecyclerView) findViewById( R.id.sets_rec );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        recyclerView.setAdapter( adapter );
        loadSetData();

    }

    public void setProgressBar(boolean status) {
        progressBar.setVisibility( status ? View.VISIBLE : View.GONE );
        recyclerView.setVisibility( status ? View.GONE : View.VISIBLE );

    }

    private void loadSetData() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection( "Sets" )
                .whereEqualTo( "category", CAT_ID )
                .orderBy( "timestamp", Query.Direction.DESCENDING )
                .get()
                .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshots = task.getResult();

                            if (snapshots != null && !task.getResult().isEmpty()) {
                                for (int a = 0; a < snapshots.size(); a++) {
                                    setsList.add( snapshots.getDocuments().get( a ) );
                                }

                                setSetsCounttv.setText( task.getResult().size() + " Results" );

                                adapter.notifyDataSetChanged();
                                setProgressBar( false );
                            }
                        }
                    }
                } )
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d( TAG, "onFailure: " + e.getLocalizedMessage() );
                        setProgressBar( false );
                        Toast.makeText( ShowSetsActivity.this, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
                    }
                } );
    }

    public void addCategory(View view) {
        startActivity( new Intent( this, AddNewCategory.class ).putExtra( "cat_id", CAT_ID ) );
    }

    private class SetsAdapter extends RecyclerView.Adapter<SetsAdapter.MyViewHolder> {
        List<DocumentSnapshot> setslist;
        Context context;

        public SetsAdapter(List<DocumentSnapshot> setslist, Context context) {
            this.setslist = setslist;
            this.context = context;
        }

        @NonNull
        @Override
        public SetsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.sets_view, parent, false );
            return new MyViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull final SetsAdapter.MyViewHolder myViewHolder, final int position) {
            String title = setslist.get( position ).getString( "title" );
            String id = setslist.get( position ).getId();
            if (!title.equals( "" ))
                myViewHolder.title.setText( title + "\n" + id );
            else {
                myViewHolder.title.setText( "No title\n" + id );
            }

            boolean isActive = setslist.get( position ).getBoolean( "isActive" );
            myViewHolder.isActive.setChecked( isActive );
            myViewHolder.isNew.setChecked( setslist.get( position ).getBoolean( "isNew" ) );

            long diff = setslist.get( position ).getLong( "difficulty" );
            final int DIFF = (int) diff;
            switch (DIFF) {
                case 0:
                    myViewHolder.difficulty.setText( "Difficulty: Easy" );
                    break;
                case 1:
                    myViewHolder.difficulty.setText( "Difficulty: Medium" );
                    break;
                case 2:
                    myViewHolder.difficulty.setText( "Difficulty: Hard" );
                    break;
            }

            myViewHolder.question.setText( "Questions: " + setslist.get( position ).getLong( "questions" ) + "" );

            myViewHolder.isActive.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String setId = setslist.get( position ).getId();
                    String title = setslist.get( position ).getString( "title" );
                    String field = "isActive";
                    setToDatabase( myViewHolder.isActive.isChecked(), setId, field, myViewHolder, title );

                }
            } );


            myViewHolder.isNew.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String setId = setslist.get( position ).getId();
                    String title = setslist.get( position ).getString( "title" );
                    String field = "isNew";
                    setToDatabase( myViewHolder.isNew.isChecked(), setId, field, myViewHolder, title );
                }
            } );


            if (!isActive) {
                myViewHolder.layout.setBackgroundColor( getResources().getColor( R.color.grey ) );
            } else {
                myViewHolder.layout.setBackgroundColor( getResources().getColor( R.color.white ) );
            }


            myViewHolder.title.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String setId = setslist.get( position ).getId();
                    showEditTitleDialog( myViewHolder, setId );
                }
            } );

            myViewHolder.difficulty.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String setId = setslist.get( position ).getId();
                    showDifficultyDialog( setId, myViewHolder, DIFF );
                }
            } );

            myViewHolder.title.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String id = setslist.get( position ).getId();
                    String title = setslist.get( position ).getString( "title" );
                    setChangeTitleDialog( id, myViewHolder, title );
                }
            } );

            myViewHolder.layout.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String setId = setslist.get( position ).getId();
                    context.startActivity( new Intent( context, ShowQuestionActivity.class )
                            .putExtra( "cat_id", CAT_ID )
                            .putExtra( "set_id", setId ) );
                }
            } );

            myViewHolder.layout.setOnLongClickListener( new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final String setId = setsList.get( position ).getId();
                    new AlertDialog.Builder( context )
                            .setTitle( "Delete This Set" )
                            .setPositiveButton( "DELETE", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    db.collection( "Sets" )
                                            .document( setId )
                                            .delete()
                                            .addOnSuccessListener( new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText( ShowSetsActivity.this, "Deleted", Toast.LENGTH_SHORT ).show();
                                                    setsList.clear();
                                                    loadSetData();
                                                }
                                            } )
                                            .addOnFailureListener( new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText( ShowSetsActivity.this, "could't delete", Toast.LENGTH_SHORT ).show();
                                                }
                                            } );
                                    dialog.cancel();
                                }
                            } ).show();
                    return true;
                }
            } );

            Log.d( TAG, "onBindViewHolder: " + setslist.get( position ) );

        }

        @Override
        public int getItemCount() {
            return setslist.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView title, difficulty, question;
            private Switch isActive, isNew;
            RelativeLayout layout;

            public MyViewHolder(@NonNull View itemView) {
                super( itemView );
                isActive = (Switch) itemView.findViewById( R.id.switch2 );
                isNew = (Switch) itemView.findViewById( R.id.switch1 );
                title = (TextView) itemView.findViewById( R.id.setsTitle );
                difficulty = (TextView) itemView.findViewById( R.id.diff );
                question = (TextView) itemView.findViewById( R.id.qst_count );
                layout = (RelativeLayout) itemView.findViewById( R.id.cat_view_lay );
            }

        }
    }

    private void setChangeTitleDialog(final String setId, final SetsAdapter.MyViewHolder myViewHolder, String title) {

        // creating the EditText widget programatically
        final EditText editText = new EditText( ShowSetsActivity.this );

        editText.setText( title );
        // create the AlertDialog as final
        final AlertDialog dialog = new AlertDialog.Builder( ShowSetsActivity.this )
                .setMessage( "Change Title" )
                .setTitle( "Change title here..." )
                .setView( editText )

                // Set the action buttons
                .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        final String eText = editText.getText().toString();
                        if (!eText.equals( "" )) {
                            Map<String, Object> map = new HashMap<>();
                            dialog.dismiss();
                            map.put( "title", eText );
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection( "Sets" )
                                    .document( setId )
                                    .update( map ).addOnSuccessListener( new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //
                                    // String[] ar = getResources().getStringArray( R.array.choices );
                                    myViewHolder.title.setText( eText );
                                    //  setProgressBar( false );
                                }
                            } ).addOnFailureListener( new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // setProgressBar( false );
                                    Toast.makeText( ShowSetsActivity.this, "can't update", Toast.LENGTH_SHORT ).show();
                                }
                            } );
                        } else {
                            Toast.makeText( ShowSetsActivity.this, "title is empty", Toast.LENGTH_SHORT ).show();
                        }
                    }
                } )

                .setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // removes the AlertDialog in the screen
                    }
                } )
                .create();

        // set the focus change listener of the EditText
        // this part will make the soft keyboard automaticall visible
        editText.setOnFocusChangeListener( new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE );
                }
            }
        } );

        dialog.show();
    }


    public void showDifficultyDialog(final String setId, final SetsAdapter.MyViewHolder myViewHolder, int DIFF) {
        AlertDialog.Builder builder = new AlertDialog.Builder( ShowSetsActivity.this );
        // Set the dialog title
        builder.setTitle( "Choose One" )
                .setSingleChoiceItems( R.array.choices, DIFF, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }

                } )
                // Set the action buttons
                .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        final int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        // Toast.makeText( ShowSetsActivity.this, "selectedPosition: " + selectedPosition, Toast.LENGTH_SHORT ).show();
                        Map<String, Object> map = new HashMap<>();
                        dialog.dismiss();
                        map.put( "difficulty", selectedPosition );
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection( "Sets" )
                                .document( setId )
                                .update( map ).addOnSuccessListener( new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                String[] ar = getResources().getStringArray( R.array.choices );
                                myViewHolder.difficulty.setText( "Difficulty: " + ar[selectedPosition] );
                                //  setProgressBar( false );
                            }
                        } ).addOnFailureListener( new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // setProgressBar( false );
                                Toast.makeText( ShowSetsActivity.this, "can't update", Toast.LENGTH_SHORT ).show();
                            }
                        } );

                    }
                } )

                .setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // removes the dialog from the screen

                    }
                } )

                .show();
    }

    private void showEditTitleDialog(SetsAdapter.MyViewHolder myViewHolder, String setId) {

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

    private void setToDatabase(final boolean checked, String setId, final String field, final SetsAdapter.MyViewHolder myViewHolder, final String title) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> map = new HashMap<>();
        map.put( field, checked );
        db.collection( "Sets" )
                .document( setId )
                .update( map )
                .addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText( ShowSetsActivity.this, field + ": " + checked, Toast.LENGTH_SHORT ).show();
                        if (!field.equalsIgnoreCase( "isNew" )) {
                            if (checked) {
                                myViewHolder.layout.setBackgroundColor( getResources().getColor( R.color.white ) );
                                sendNotificationApi( checked, title );
                            } else {
                                myViewHolder.layout.setBackgroundColor( getResources().getColor( R.color.grey ) );
                            }
                        }
                    }
                } )
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText( ShowSetsActivity.this, "can't updated " + e.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
                    }
                } );


    }

    private void sendNotificationApi(boolean checked, String title) {

        String BASE_URL = "https://us-central1-icofirebaseproject-e16e8.cloudfunctions.net/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl( BASE_URL )
                .addConverterFactory( GsonConverterFactory.create() )
                .build();

        RetrofitService service = retrofit.create( RetrofitService.class );

        Call<Object> call = service.sendNotification( checked, title );
        call.enqueue( new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()){
                    Toast.makeText( ShowSetsActivity.this, "send to notification", Toast.LENGTH_SHORT ).show();
                    return;
                }
                Toast.makeText( ShowSetsActivity.this, "send to notification", Toast.LENGTH_SHORT ).show();
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText( ShowSetsActivity.this, "failed: "+t.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );

    }


    @Override
    protected void onResume() {
        setsList.clear();
        loadSetData();
        super.onResume();
    }
}
