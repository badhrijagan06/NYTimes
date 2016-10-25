package com.example.badhri.nytimes.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.badhri.nytimes.R;
import com.example.badhri.nytimes.models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by badhri on 10/22/16.
 */
public class ArticlesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.tvHeadline) public TextView tvHeadline;
        @BindView(R.id.ivThumbnail)public ImageView ivThumbnail;
        Context context;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.context = context;

            tvHeadline.setOnClickListener(this);
            ivThumbnail.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(null, view, getAdapterPosition(), view.getId());
        }
    }

    class ViewHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.tvHeadline2) public TextView tvHeadline;
        Context context;

        public ViewHolder2(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.context = context;

            tvHeadline.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            //passing the clicked position to the parent class
            onItemClickListener.onItemClick(null, view, getAdapterPosition(), view.getId());
        }
    }

    private List<Article> mArticles;
    // Store the context for easy access
    private Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;
    private final int LARGE = 0, SMALL = 1;

    @Override
    public int getItemViewType(int position) {
        if (mArticles.get(position).getThumbNail().isEmpty())
            return SMALL;
        else
            return LARGE;
    }

    // Pass in the contact array into the constructor
    public ArticlesAdapter(Context context, List<Article> articles,
                           AdapterView.OnItemClickListener onItemClickListener) {
        mArticles = articles;
        mContext = context;
        this.onItemClickListener = onItemClickListener;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder viewHolder;

        if (viewType == LARGE) {
            View itemView = inflater.inflate(R.layout.news_item, parent, false);
            viewHolder = new ViewHolder(itemView, context);
        } else {
            View itemView = inflater.inflate(R.layout.news_item2, parent, false);
            viewHolder = new ViewHolder2(itemView, context);
        }

        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Article article = mArticles.get(position);

        switch (viewHolder.getItemViewType()) {
            case LARGE:
                ViewHolder v1 = (ViewHolder)viewHolder;
                v1.tvHeadline.setText(article.getHeadline());
                Picasso.with(mContext).load(article.getThumbNail()).resize(mContext.getResources()
                        .getDisplayMetrics().widthPixels/3, 0)
                        .placeholder(R.drawable.camera_icon_circle_21)
                        .error(R.drawable.error_128).into(v1.ivThumbnail);
                break;
            default:
                ViewHolder2 v2 = (ViewHolder2)viewHolder;
                v2.tvHeadline.setWidth(mContext.getResources().getDisplayMetrics().widthPixels/3);
                v2.tvHeadline.setText(article.getHeadline());
                break;
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mArticles.size();
    }

}
