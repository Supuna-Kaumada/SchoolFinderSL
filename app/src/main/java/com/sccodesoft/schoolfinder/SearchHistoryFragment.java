package com.sccodesoft.schoolfinder;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchHistoryFragment extends Fragment {

    private View searchHistoryView;
    private RecyclerView searchresultList;

    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    String currentuserid;


    public SearchHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        searchHistoryView =  inflater.inflate(R.layout.fragment_search_history, container, false);

        searchresultList = (RecyclerView) searchHistoryView.findViewById(R.id.search_history_list_recycler);
        searchresultList.setLayoutManager(new LinearLayoutManager(getContext()));

        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();

        return searchHistoryView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<SearchHistory> options =
                new FirebaseRecyclerOptions.Builder<SearchHistory>()
                .setQuery(rootRef.child("Users").child(currentuserid).child("Searchhistory"),SearchHistory.class)
                .build();

        FirebaseRecyclerAdapter<SearchHistory,HistoryViewHolder> adapter =
                new FirebaseRecyclerAdapter<SearchHistory, HistoryViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull HistoryViewHolder holder, int position, @NonNull SearchHistory model) {

                        holder.SchoolNamesh.setText(model.getSchool());
                        holder.SchoolAddresssh.setText(model.getAddress());
                        holder.SearchTypesh.setText(model.getStype());
                        holder.Markssh.setText(model.getMarks());

                    }

                    @NonNull
                    @Override
                    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_history_layout,parent,false);
                        HistoryViewHolder historyViewHolder = new HistoryViewHolder(view);
                        return historyViewHolder;
                    }
                };
        searchresultList.setAdapter(adapter);
        adapter.startListening();

    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder
    {
        public TextView SchoolNamesh,SchoolAddresssh,SearchTypesh,Markssh;

        public HistoryViewHolder(View itemView) {
            super(itemView);

            SchoolNamesh = itemView.findViewById(R.id.SchoolNamesh);
            SchoolAddresssh = itemView.findViewById(R.id.SchoolAddressh);
            SearchTypesh = itemView.findViewById(R.id.SearchTypesh);
            Markssh = itemView.findViewById(R.id.SchoolMarkssh);

        }
    }
}