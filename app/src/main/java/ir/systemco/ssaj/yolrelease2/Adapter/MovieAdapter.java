package ir.systemco.ssaj.yolrelease2.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ir.systemco.ssaj.yolrelease2.R;
import ir.systemco.ssaj.yolrelease2.model.Model;

import java.util.ArrayList;



public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyHolder> {

    private Context context;
    private ArrayList<Model> models;

    public MovieAdapter(Context context, ArrayList<Model> models) {
        this.context = context;
        this.models = models;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view,null);
        MyHolder myHolder = new MyHolder(view);

        return myHolder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

        holder.imageView.setImageResource(models.get(position).getImageId());
        holder.tvTitle.setText(models.get(position).getMovieTitle());
        holder.tvDesc.setText(models.get(position).getMovieDesc());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView tvTitle;
        TextView tvDesc;

        public MyHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imv1);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDesc = (TextView) itemView.findViewById(R.id.tvDescription);

        }
    }
}
