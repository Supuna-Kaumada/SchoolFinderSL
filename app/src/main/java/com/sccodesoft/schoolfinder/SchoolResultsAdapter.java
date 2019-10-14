package com.sccodesoft.schoolfinder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class SchoolResultsAdapter extends  RecyclerView.Adapter<SchoolResultsAdapter.ViewHolder>{

    Context mcontext;
    private List<Schools> mSchools;

    public SchoolResultsAdapter(Context mcontext, List<Schools> mSchools) {
        this.mcontext = mcontext;
        this.mSchools = mSchools;
    }

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public SchoolResultsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View singleItemLayout = LayoutInflater.from(mcontext).inflate(R.layout.all_school_layout,parent,false);
        ViewHolder myViewHolder = new ViewHolder(singleItemLayout);
        return myViewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        // Set item views based on your views and data model
        viewHolder.SchoolName.setText(mSchools.get(position).getName());
        viewHolder.SchoolAddress.setText(mSchools.get(position).getAddress());
        viewHolder.SchoolWebsite.setText(mSchools.get(position).getWebsite());
        viewHolder.SchoolType.setText(mSchools.get(position).getType());
        viewHolder.SchoolRating.setText(mSchools.get(position).getRating());
        viewHolder.SchoolReligion.setText(mSchools.get(position).getReligion());
        viewHolder.SchoolMedium.setText(mSchools.get(position).getMedium());
        viewHolder.SchoolPhone.setText(mSchools.get(position).getPhone());
        viewHolder.SchoolCategory.setText(mSchools.get(position).getCategory());
        viewHolder.Distance.setText(mSchools.get(position).getDist());
        viewHolder.itemView.setTag(position);

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mSchools.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView SchoolName,SchoolAddress,SchoolPhone,SchoolWebsite,SchoolType,SchoolRating,SchoolReligion,SchoolMedium,SchoolCategory,Distance;

        public Button SelectSchool;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);


            SchoolName = itemView.findViewById(R.id.SchoolName);
            SchoolAddress = itemView.findViewById(R.id.SchoolAddress);
            SchoolPhone = itemView.findViewById(R.id.SchoolPhone);
            SchoolWebsite = itemView.findViewById(R.id.SchoolWebsite);
            SchoolType = itemView.findViewById(R.id.SchoolType);
            SchoolRating = itemView.findViewById(R.id.SchoolRating);
            SchoolReligion = itemView.findViewById(R.id.SchoolReligion);
            SchoolMedium = itemView.findViewById(R.id.SchoolMedium);
            SchoolCategory = itemView.findViewById(R.id.SchoolCategory);
            Distance = itemView.findViewById(R.id.SchoolDistance);

            SelectSchool = itemView.findViewById(R.id.select_sch_button);
            SelectSchool.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(itemView, position);
                        }
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {

        }
    }


}
