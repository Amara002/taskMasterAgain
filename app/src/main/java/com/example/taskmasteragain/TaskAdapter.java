package com.example.taskmasteragain;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.datastore.generated.model.Task;

import java.util.List;

public class TaskAdapter  extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {


//    private final List<TaskItem> taskItems;
    private final List<Task> taskItemLists;
    private OnTaskItemClickListener listener;

    public TaskAdapter(List<Task> taskItemLists, OnTaskItemClickListener listener) {
        this.taskItemLists = taskItemLists;
        this.listener = listener;
    }

    public interface OnTaskItemClickListener {
        void onItemClicked(int position);
        void onDeleteItem(int position);
    }


    @NonNull

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return  new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder holder, int position) {
        Task item = taskItemLists.get(position);
        holder.title.setText(item.getTitle());
        holder.body.setText(item.getBody());
        holder.state.setText(item.getState());
//        holder.image.setImageResource(item.getImage());

    }

    @Override
    public int getItemCount() {
        return taskItemLists.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView image;
        private TextView title;
        private TextView body;
        private TextView state;
        private TextView delete;

        ViewHolder (@NonNull View itemView,  OnTaskItemClickListener listener){
            super(itemView);

            image = itemView.findViewById(R.id.image_list);
            title = itemView.findViewById(R.id.title_list);
            body = itemView.findViewById(R.id.body_list);
            state = itemView.findViewById(R.id.state_list);
            delete = itemView.findViewById(R.id.delete_list);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(getAdapterPosition());
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteItem(getAdapterPosition());
                }
            });

        }
    }
}
