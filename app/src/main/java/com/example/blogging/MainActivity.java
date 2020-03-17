package com.example.blogging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import com.squareup.picasso.Picasso;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    FirebaseRecyclerAdapter<Blog,BlogViewHolder> firebaseRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=(RecyclerView)findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Blog");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = databaseReference
                .limitToLast(50)
                .orderByKey();

        FirebaseRecyclerOptions<Blog> options =
                new FirebaseRecyclerOptions.Builder<Blog>()
                        .setQuery(query, Blog.class)
                        .build();
        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(options) {
            @NonNull
            @Override
            public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.blog_row, parent, false);

                return new BlogViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(@NonNull BlogViewHolder holder, int position, @NonNull Blog model) {
                holder.setTitle(model.getTitle());
                holder.setDesc(model.getDescription());
                holder.setImage(getApplicationContext(),model.getImages());
            }
        };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View view;
        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
           view=itemView;
        }
        public void setTitle(String title){
            TextView postTitle=(TextView)view.findViewById(R.id.posttitle);
            postTitle.setText(title);
        }
        public void setDesc(String desc){
            TextView postdesc=(TextView)view.findViewById(R.id.postdes);
            postdesc.setText(desc);
        }
        public void setImage(Context ctx,String image){
            ImageView postimage=(ImageView)view.findViewById(R.id.postimage);
           Picasso.get().load(image).into(postimage);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.action_add){
            startActivity(new Intent(MainActivity.this,PostActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();


    }
}
