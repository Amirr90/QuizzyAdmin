package com.e.quizzyadmin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowQuestionActivity extends AppCompatActivity {

    String catId, setId;
    Button addNewQuestionBtn;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    TextView setQuestCount_tv;
    QuestionAdapter adapter;
    List<DocumentSnapshot> setsList;
    Button AddNewQuestion;
    private String TAG = "ShowQuestionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_show_question );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        addNewQuestionBtn = (Button) findViewById( R.id.add_new_question );
        progressBar = (ProgressBar) findViewById( R.id.progressBar );
        setQuestCount_tv = (TextView) findViewById( R.id.set_quest_Count );
        AddNewQuestion = (Button) findViewById( R.id.add_new_question );


        if (getIntent().hasExtra( "set_id" )) {
            catId = getIntent().getStringExtra( "cat_id" );
            setId = getIntent().getStringExtra( "set_id" );
            setToolbar( toolbar, catId );
            showAllSQuest( setId );
        } else {
            finish();
        }

    }


    private void showAllSQuest(String cat_id) {
        setsList = new ArrayList<>();
        adapter = new QuestionAdapter( setsList, this );
        recyclerView = (RecyclerView) findViewById( R.id.ques_rec );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        recyclerView.setAdapter( adapter );
        loadSetData();

    }


    private void loadSetData() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection( "Question" )
                .whereEqualTo( "setId", setId )
                .get()
                .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshots = task.getResult();

                            if (task.getResult().isEmpty()) {
                                setProgressBar( false );
                                Toast.makeText( ShowQuestionActivity.this, "No Question found", Toast.LENGTH_SHORT ).show();
                                return;
                            }
                            if (snapshots != null && !task.getResult().isEmpty()) {
                                for (int a = 0; a < snapshots.size(); a++) {
                                    setsList.add( snapshots.getDocuments().get( a ) );
                                }

                                setQuestCount_tv.setText( task.getResult().size() + " Results" );

                                adapter.notifyDataSetChanged();
                                setProgressBar( false );
                            }
                        } else {
                            setProgressBar( false );
                            Toast.makeText( ShowQuestionActivity.this, "No data Found", Toast.LENGTH_SHORT ).show();
                        }
                    }
                } )
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        setProgressBar( false );
                        Toast.makeText( ShowQuestionActivity.this, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
                    }
                } );
    }

    public void addNewQuestion(View view) {
        AddNewQuestion.setText( "ADDING PLEASE WAIT..." );
        AddNewQuestion.setEnabled( false );
        progressBar.setVisibility( View.VISIBLE );
        Map<String, Object> map = new HashMap<>();
        map.put( "answer", "" );
        map.put( "category", catId );
        map.put( "chap_name", "" );
        map.put( "image", "" );
        map.put( "isActive", false );
        map.put( "isNew", false );
        map.put( "optionA", "" );
        map.put( "optionB", "" );
        map.put( "optionC", "" );
        map.put( "optionD", "" );
        map.put( "question", "" );
        map.put( "title", "" );
        map.put( "setId", setId );


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection( "Question" )
                .add( map )
                .addOnCompleteListener( new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility( View.GONE );
                            Toast.makeText( ShowQuestionActivity.this, "New Question Added Successfully", Toast.LENGTH_SHORT ).show();
                            /*DocumentSnapshot snapshot=task.getResult().get().getResult();
                            setsList.add(snapshot);
                            adapter.notifyDataSetChanged(  );*/
                            setsList.clear();
                            loadSetData();
                            AddNewQuestion.setText( "ADD NEW SET" );
                            AddNewQuestion.setEnabled( true );
                        }
                    }
                } )
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText( ShowQuestionActivity.this, "can't Added", Toast.LENGTH_SHORT ).show();
                        progressBar.setVisibility( View.GONE );
                        AddNewQuestion.setText( "ADD NEW SET" );
                        AddNewQuestion.setEnabled( true );
                    }
                } );

    }

    public void setProgressBar(boolean status) {
        progressBar.setVisibility( status ? View.VISIBLE : View.GONE );
        recyclerView.setVisibility( status ? View.GONE : View.VISIBLE );

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

    private class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.MyViewHolder> {
        List<DocumentSnapshot> setslist;
        Context context;

        public QuestionAdapter(List<DocumentSnapshot> setslist, Context context) {
            this.setslist = setslist;
            this.context = context;
        }

        @NonNull
        @Override
        public QuestionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.questions_view, parent, false );
            return new MyViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull final QuestionAdapter.MyViewHolder myViewHolder, final int position) {
            int count = position + 1;
            final String question = setslist.get( position ).getString( "question" );
            String id = setslist.get( position ).getId();
            if (!question.equals( "" ))
                myViewHolder.title.setText( "Q" + count + ": " + question + "\nQueID: " + id );
            else {
                myViewHolder.title.setText( "No Question\nQueID: " + id );
            }

            boolean isActive = setslist.get( position ).getBoolean( "isActive" );
            myViewHolder.isActive.setChecked( isActive );
            myViewHolder.isNew.setChecked( setslist.get( position ).getBoolean( "isNew" ) );

            /*long diff = setslist.get( position ).getLong( "difficulty" );
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
*/
            myViewHolder.tv_OpA.setText( "Option A: " + setslist.get( position ).getString( "optionA" ) + "" );
            myViewHolder.tv_OpB.setText( "Option B: " + setslist.get( position ).getString( "optionB" ) + "" );
            myViewHolder.tv_OpC.setText( "Option C: " + setslist.get( position ).getString( "optionC" ) + "" );
            myViewHolder.tv_OpD.setText( "Option D: " + setslist.get( position ).getString( "optionD" ) + "" );
            myViewHolder.tv_CorrectAns.setText( "Correct Ans: " + setslist.get( position ).getString( "answer" ) + "" );


            myViewHolder.isActive.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String queId = setslist.get( position ).getId();
                    String field = "isActive";
                    setToDatabase( myViewHolder.isActive.isChecked(), queId, field, myViewHolder );

                }
            } );


            myViewHolder.isNew.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String queId = setslist.get( position ).getId();
                    String field = "isNew";
                    setToDatabase( myViewHolder.isNew.isChecked(), queId, field, myViewHolder );
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
                    String queId = setslist.get( position ).getId();
                    showEditQuestionDialog( myViewHolder, queId, question );
                }
            } );

            myViewHolder.layout.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String setId = setslist.get( position ).getId();
                    context.startActivity( new Intent( context, ShowQuestionActivity.class )
                            .putExtra( "cat_id", catId )
                            .putExtra( "set_id", setId ) );
                }
            } );


            myViewHolder.tv_OpA.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String queId = setslist.get( position ).getId();
                    String field = "optionA";
                    String optionText = setslist.get( position ).getString( field );
                    changeOptionDialog( queId, field, myViewHolder, position, optionText, myViewHolder.tv_OpA );
                }
            } );

            myViewHolder.tv_OpB.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String queId = setslist.get( position ).getId();
                    String field = "optionB";
                    String optionText = setslist.get( position ).getString( field );
                    changeOptionDialog( queId, field, myViewHolder, position, optionText, myViewHolder.tv_OpB );
                }
            } );

            myViewHolder.tv_OpC.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String queId = setslist.get( position ).getId();
                    String field = "optionC";
                    String optionText = setslist.get( position ).getString( field );
                    changeOptionDialog( queId, field, myViewHolder, position, optionText, myViewHolder.tv_OpC );
                }
            } );

            myViewHolder.tv_OpD.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String queId = setslist.get( position ).getId();
                    String field = "optionD";
                    String optionText = setslist.get( position ).getString( field );
                    changeOptionDialog( queId, field, myViewHolder, position, optionText, myViewHolder.tv_OpD );
                }
            } );

            myViewHolder.tv_CorrectAns.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String queId = setslist.get( position ).getId();
                    String field = "answer";
                    String optionText = setslist.get( position ).getString( field );
                    changeOptionDialog( queId, field, myViewHolder, position, optionText, myViewHolder.tv_CorrectAns );
                }
            } );


            myViewHolder.layout.setOnLongClickListener( new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final String qstId = setsList.get( position ).getId();
                    new AlertDialog.Builder( context )
                            .setTitle( "Delete This Question" )
                            .setPositiveButton( "DELETE", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    db.collection( "Question" )
                                            .document( qstId )
                                            .delete()
                                            .addOnSuccessListener( new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText( ShowQuestionActivity.this, "Deleted", Toast.LENGTH_SHORT ).show();
                                                    setsList.clear();
                                                    loadSetData();
                                                }
                                            } )
                                            .addOnFailureListener( new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText( ShowQuestionActivity.this, "could't delete", Toast.LENGTH_SHORT ).show();
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

        private void showEditQuestionDialog(final MyViewHolder myViewHolder, final String queId, String question) {

            // creating the EditText widget programatically
            final EditText editText = new EditText( ShowQuestionActivity.this );

            editText.setText( question );
            // create the AlertDialog as final
            final AlertDialog dialog = new AlertDialog.Builder( ShowQuestionActivity.this )
                    .setMessage( "Change Question  here..." )
                    .setTitle( "Change Question" )
                    .setView( editText )
                    // Set the action buttons
                    .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            final String eText = editText.getText().toString();
                            if (!eText.equals( "" )) {
                                Map<String, Object> map = new HashMap<>();
                                dialog.dismiss();
                                map.put( "question", eText );
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection( "Question" )
                                        .document( queId )
                                        .update( map ).addOnSuccessListener( new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        myViewHolder.title.setText( eText );
                                        editText.setText( eText );
                                    }
                                } ).addOnFailureListener( new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText( ShowQuestionActivity.this, "can't update", Toast.LENGTH_SHORT ).show();
                                    }
                                } );
                            } else {
                                Toast.makeText( ShowQuestionActivity.this, "title is empty", Toast.LENGTH_SHORT ).show();
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

        @Override
        public int getItemCount() {
            return setslist.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView title, difficulty, question;
            private Switch isActive, isNew;
            RelativeLayout layout;
            private TextView tv_OpA, tv_OpB, tv_OpC, tv_OpD, tv_CorrectAns;

            public MyViewHolder(@NonNull View itemView) {
                super( itemView );
                isActive = (Switch) itemView.findViewById( R.id.switch2 );
                isNew = (Switch) itemView.findViewById( R.id.switch1 );
                title = (TextView) itemView.findViewById( R.id.setsTitle );
                layout = (RelativeLayout) itemView.findViewById( R.id.cat_view_lay );
                tv_OpA = (TextView) itemView.findViewById( R.id.opt_A );
                tv_OpB = (TextView) itemView.findViewById( R.id.opt_B );
                tv_OpC = (TextView) itemView.findViewById( R.id.opt_C );
                tv_OpD = (TextView) itemView.findViewById( R.id.opt_D );
                tv_CorrectAns = (TextView) itemView.findViewById( R.id.correct_Ans );


            }


        }
    }

    private void changeOptionDialog(final String queId, final String field, final QuestionAdapter.MyViewHolder myViewHolder, int position, String optionText, final TextView tv_Op) {

        // creating the EditText widget programatically
        final EditText editText = new EditText( ShowQuestionActivity.this );
        editText.setText( optionText );


        final AlertDialog dialog = new AlertDialog.Builder( ShowQuestionActivity.this )
                .setMessage( "Change " + field + " text  here..." )
                .setTitle( "Edit " + field )
                .setView( editText )
                // Set the action buttons
                .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        final String eText = editText.getText().toString();
                        if (!eText.equals( "" )) {
                            Map<String, Object> map = new HashMap<>();
                            dialog.dismiss();
                            map.put( field, eText );
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection( "Question" )
                                    .document( queId )
                                    .update( map ).addOnSuccessListener( new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    tv_Op.setText( field + ": " + eText );
                                    editText.setText( eText );
                                }
                            } ).addOnFailureListener( new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText( ShowQuestionActivity.this, "can't update", Toast.LENGTH_SHORT ).show();
                                }
                            } );
                        } else {
                            Toast.makeText( ShowQuestionActivity.this, "title is empty", Toast.LENGTH_SHORT ).show();
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


    private void setToDatabase(final boolean checked, String queId, final String field, final QuestionAdapter.MyViewHolder myViewHolder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> map = new HashMap<>();
        map.put( field, checked );
        db.collection( "Question" )
                .document( queId )
                .update( map )
                .addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText( ShowQuestionActivity.this, field + ": " + checked, Toast.LENGTH_SHORT ).show();
                        if (checked) {
                            myViewHolder.layout.setBackgroundColor( getResources().getColor( R.color.white ) );
                        } else {
                            myViewHolder.layout.setBackgroundColor( getResources().getColor( R.color.grey ) );
                        }
                    }
                } )
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText( ShowQuestionActivity.this, "can't updated " + e.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
                    }
                } );


    }

}
