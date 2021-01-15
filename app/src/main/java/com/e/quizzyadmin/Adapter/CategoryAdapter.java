package com.e.quizzyadmin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.e.quizzyadmin.Model.CategoryModel;
import com.e.quizzyadmin.R;
import com.e.quizzyadmin.ShowSetsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    List<CategoryModel> categoryModelList;
    Context context;

    public CategoryAdapter(List<CategoryModel> categoryModelList, Context context) {
        this.categoryModelList = categoryModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.cat_view, parent, false );
        return new MyViewHolder( view );

    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryAdapter.MyViewHolder myViewHolder, final int position) {

        myViewHolder.title.setText( categoryModelList.get( position ).getTitle().trim() );
        myViewHolder.isActive.setChecked( categoryModelList.get( position ).isActive() );
        myViewHolder.showOnHomeScreen.setChecked( categoryModelList.get( position ).isShowOnHomePage() );
        String image_url = categoryModelList.get( position ).getImage();
        if (image_url != null && !
                image_url.equals( "" ))
            Picasso.with( context ).load( image_url ).into( myViewHolder.imageView );
        else {
            myViewHolder.imageView.setImageResource( R.drawable.logo_q );
        }
        myViewHolder.qst_count.setText( categoryModelList.get( position ).getQuestion() + " Questions" );

        myViewHolder.sets_count.setText( categoryModelList.get( position ).getSets() + " Sets" );

        myViewHolder.isActive.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String docRef = categoryModelList.get( position ).getId();
                String field = "isActive";
                String Ref = categoryModelList.get( position ).getCollectionRef();
                if (myViewHolder.isActive.isChecked()) {
                    // The toggle is enabledT
                    Toast.makeText( context, categoryModelList.get( position ).getTitle() + " Activate: " + myViewHolder.isActive.isChecked(), Toast.LENGTH_SHORT ).show();
                    setToDatabase( myViewHolder.isActive.isChecked(), docRef, field, Ref );
                } else {
                    // The toggle is disabled
                    Toast.makeText( context, categoryModelList.get( position ).getTitle() + " checked: " + myViewHolder.isActive.isChecked(), Toast.LENGTH_SHORT ).show();
                    setToDatabase( myViewHolder.isActive.isChecked(), docRef, field, Ref );
                }
            }
        } );


        myViewHolder.showOnHomeScreen.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String docRef = categoryModelList.get( position ).getId();
                String field = "showOnHomePage";
                String Ref = categoryModelList.get( position ).getCollectionRef();
                Toast.makeText( context, Ref, Toast.LENGTH_SHORT ).show();
                if (myViewHolder.showOnHomeScreen.isChecked()) {
                    // The toggle is enabledT
                    setToDatabase( myViewHolder.showOnHomeScreen.isChecked(), docRef, field, Ref );
                } else {
                    // The toggle is disabled
                    setToDatabase( myViewHolder.showOnHomeScreen.isChecked(), docRef, field, Ref );
                }
            }
        } );

        myViewHolder.layout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity( new Intent( context, ShowSetsActivity.class )
                        .putExtra( "cat_id", categoryModelList.get( position ).getTitle() ) );
            }
        } );

    }

    private void setToDatabase(boolean checked, String docRef, String field, String Ref) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> map = new HashMap<>();
        map.put( field, checked );
        db.collection( Ref )
                .document( docRef )
                .update( map )
                .addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText( context, "Updated", Toast.LENGTH_SHORT ).show();
                    }
                } )
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText( context, "can't updated " + e.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
                    }
                } );


    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView title, qst_count, sets_count;
        private Switch isActive, showOnHomeScreen;
        private CircleImageView imageView;
        RelativeLayout layout;

        public MyViewHolder(@NonNull View itemView) {
            super( itemView );

            title = (TextView) itemView.findViewById( R.id.setsTitle );
            qst_count = (TextView) itemView.findViewById( R.id.qst_count );
            sets_count = (TextView) itemView.findViewById( R.id.sets_count );
            isActive = (Switch) itemView.findViewById( R.id.switch2 );
            showOnHomeScreen = (Switch) itemView.findViewById( R.id.switch1 );
            imageView = (CircleImageView) itemView.findViewById( R.id.profile_image );
            layout = (RelativeLayout) itemView.findViewById( R.id.cat_view_lay );
        }

    }
}
