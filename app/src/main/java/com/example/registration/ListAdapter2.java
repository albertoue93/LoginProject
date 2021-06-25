package com.example.registration;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

class ListAdapter2 extends RecyclerView.Adapter<ListAdapter2.MyViewHolder> {
    private ListActivity2 activity;
    private List<Model> mList;
    private List<Model> mListOriginal;

    public ListAdapter2(ListActivity2 activity, List<Model> mList) {
        this.activity = activity;
        this.mList = mList;
        mListOriginal = new ArrayList<>();
        mListOriginal.addAll(mList);
    }

    @NonNull
    @NotNull
    @Override
    public ListAdapter2.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.list_group2, parent , false);
        return new ListAdapter2.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ListAdapter2.MyViewHolder holder, int position) {
        holder.title.setText(mList.get(position).getTitle());
        holder.desc.setText(mList.get(position).getDesc());
        holder.date.setText(mList.get(position).getDate());
        holder.time.setText(mList.get(position).getTime());
        boolean isExpanded = mList.get(position).isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout expandableLayout;
        TextView title, desc, date, time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_text);
            desc = itemView.findViewById(R.id.desc_text);
            date = itemView.findViewById(R.id.date_text);
            time = itemView.findViewById(R.id.time_text);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model model=mList.get(getAdapterPosition());
                    model.setExpanded(!model.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}
