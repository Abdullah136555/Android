package com.linkup.marketplace.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.linkup.marketplace.R;
import com.linkup.marketplace.model.Comment;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private Context context;
    private ArrayList<Comment> commentList;

    public CommentAdapter(Context context, ArrayList<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.tvUserName.setText(comment.getUserName());
        holder.tvCommentText.setText(comment.getCommentText());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvCommentText;

        public CommentViewHolder(View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvCommentText = itemView.findViewById(R.id.tvCommentText);
        }
    }
}
