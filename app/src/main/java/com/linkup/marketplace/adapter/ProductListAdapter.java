package com.linkup.marketplace.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.linkup.marketplace.R;
import com.linkup.marketplace.dao.CommentDao;
import com.linkup.marketplace.dao.DatabaseHelper;
import com.linkup.marketplace.dao.LikeDao;
import com.linkup.marketplace.dao.SharedPreferencesHelper;
import com.linkup.marketplace.model.Comment;
import com.linkup.marketplace.model.Product;

import java.io.File;
import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductViewHolder> {

    private final Context context;
    private final List<Product> productList;
    private final int currentUserId;
    private final LikeDao likeDao;
    private final CommentDao commentDao;
    private final DatabaseHelper dbHelper;

    public ProductListAdapter(Context context, List<Product> productList, int currentUserId) {
        this.context = context;
        this.productList = productList;
        this.currentUserId = currentUserId;

        dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        likeDao = new LikeDao(db);
        commentDao = new CommentDao(db);
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, imgUserProfile, imgLike;
        TextView tvFullDescription,tvProductName, tvProductPrice,tvProductDescription, tvUserName, tvContact, tvCategory, tvUploadDate, tvLikeCount, commentCountText;
      Button btnShowComment, btnPostComment;
        EditText etComment;
        LinearLayout commentSection, commentListLayout;
        TextView tvToggleIcon;
        LinearLayout descriptionToggleLayout;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            imgUserProfile = itemView.findViewById(R.id.imgUserProfile);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductDescription = itemView.findViewById(R.id.tvProductDescription);
            tvFullDescription = itemView.findViewById(R.id.tvFullDescription);
            tvToggleIcon = itemView.findViewById(R.id.tvToggleIcon);
            descriptionToggleLayout = itemView.findViewById(R.id.descriptionToggleLayout);

            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvContact = itemView.findViewById(R.id.tvContact);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvUploadDate = itemView.findViewById(R.id.tvUploadDate);
            imgLike = itemView.findViewById(R.id.imgLike);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);

            btnShowComment = itemView.findViewById(R.id.btnShowComment);
            btnPostComment = itemView.findViewById(R.id.btnPostComment);
            etComment = itemView.findViewById(R.id.etComment);
            commentSection = itemView.findViewById(R.id.commentSection);
            commentListLayout = itemView.findViewById(R.id.commentListLayout);

            commentCountText = itemView.findViewById(R.id.commentCountText);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_list, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product p = productList.get(position);

        holder.tvProductName.setText(p.getProductName());
        holder.tvProductPrice.setText("৳ " + p.getProductPrice());

        holder.tvProductDescription.setText("Description:");
        holder.tvFullDescription.setText(p.getProductDescription());
        holder.tvToggleIcon.setText("🔽"); // Initial state
        holder.tvUserName.setText(p.getUserName() != null ? p.getUserName() : "Unknown");
        holder.tvContact.setText("📞 " + p.getContactNumber());
        holder.tvCategory.setText("Category: " + p.getCategory());
        holder.tvUploadDate.setText("Uploaded: " + p.getUploadDate());

        loadImage(p.getProductImage(), holder.imgProduct, R.drawable.placeholder, R.drawable.error_image);

        if ("default_profile.jpg".equals(p.getUserProfilePhoto())) {
            holder.imgUserProfile.setImageResource(R.drawable.default_profile);
        } else {
            loadImage(p.getUserProfilePhoto(), holder.imgUserProfile, R.drawable.user_placeholder, R.drawable.default_profile);
        }

        int productId = p.getProductId();

        // Like Section
        if (likeDao.isProductLikedByUser(currentUserId, productId)) {
            holder.imgLike.setImageResource(R.drawable.ic_like_filled);
        } else {
            holder.imgLike.setImageResource(R.drawable.ic_like_outline);
        }

        holder.tvLikeCount.setText(likeDao.countLikesForProduct(productId) + " Likes");

        int commentCount = commentDao.getCommentCountByProductId(productId); // ✅ Add this
        holder.commentCountText.setText(commentCount + " Comments");         // ✅ Add this

        holder.imgLike.setOnClickListener(v -> {
            if (likeDao.isProductLikedByUser(currentUserId, productId)) {
                if (likeDao.removeLike(currentUserId, productId)) {
                    holder.imgLike.setImageResource(R.drawable.ic_like_outline);
                    Toast.makeText(context, "Unliked", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (likeDao.addLike(currentUserId, productId)) {
                    holder.imgLike.setImageResource(R.drawable.ic_like_filled);
                    Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show();
                }
            }
            int updatedCount = likeDao.countLikesForProduct(productId);
            holder.tvLikeCount.setText(updatedCount + " Likes");
        });

        // Show/Hide Comments
        holder.btnShowComment.setOnClickListener(v -> {
            if (holder.commentSection.getVisibility() == View.GONE) {
                holder.commentSection.setVisibility(View.VISIBLE);
                showComments(productId, holder.commentListLayout);
            } else {
                holder.commentSection.setVisibility(View.GONE);
            }
        });

        // Post Comment
        holder.btnPostComment.setOnClickListener(v -> {
            String commentText = holder.etComment.getText().toString().trim();
            if (!commentText.isEmpty()) {
                SharedPreferencesHelper preferencesHelper = new SharedPreferencesHelper(context);
                String username = preferencesHelper.getName();
                Comment comment = new Comment(0, productId, username, commentText);
                commentDao.addComment(comment);
                holder.etComment.setText("");
                showComments(productId, holder.commentListLayout);

                // ✅ Update comment count after posting
                int updatedCount = commentDao.getCommentCountByProductId(productId);
                holder.commentCountText.setText(updatedCount + " Comments");
            }
        });
//        holder.tvProductDescription.setOnClickListener(v -> {
//            if (holder.tvFullDescription.getVisibility() == View.GONE) {
//                holder.tvFullDescription.setVisibility(View.VISIBLE);
//            } else {
//                holder.tvFullDescription.setVisibility(View.GONE);
//            }
//        });
        holder.descriptionToggleLayout.setOnClickListener(v -> {
            if (holder.tvFullDescription.getVisibility() == View.GONE) {
                holder.tvFullDescription.setVisibility(View.VISIBLE);
                holder.tvToggleIcon.setText("🔼");
            } else {
                holder.tvFullDescription.setVisibility(View.GONE);
                holder.tvToggleIcon.setText("🔽");
            }
        });

        Log.d("ProductAdapter", "Product: " + p.getProductName());
    }

    // Show comments in layout
    private void showComments(int productId, LinearLayout layout) {
        layout.removeAllViews();
        List<Comment> comments = commentDao.getCommentsByProductId(productId); 
        for (Comment c : comments) {
            TextView tv = new TextView(context);
            tv.setText(c.getUserName() + ": " + c.getCommentText());
            tv.setPadding(5, 5, 5, 5);
            layout.addView(tv);
        }
    }

    // Load image with Glide
    private void loadImage(String path, ImageView imageView, int placeholderRes, int errorRes) {
        if (path == null || path.isEmpty()) {
            imageView.setImageResource(errorRes);
            return;
        }

        if (path.startsWith("content://")) {
            Glide.with(context)
                    .load(Uri.parse(path))
                    .placeholder(placeholderRes)
                    .error(errorRes)
                    .into(imageView);
        } else {
            File file = new File(path);
            if (file.exists()) {
                Glide.with(context)
                        .load(file)
                        .placeholder(placeholderRes)
                        .error(errorRes)
                        .into(imageView);
            } else {
                imageView.setImageResource(errorRes);
            }
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
