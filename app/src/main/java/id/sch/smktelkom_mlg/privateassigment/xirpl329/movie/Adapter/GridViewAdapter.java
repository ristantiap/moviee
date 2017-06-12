package id.sch.smktelkom_mlg.privateassigment.xirpl329.movie.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import id.sch.smktelkom_mlg.privateassigment.xirpl329.movie.Model.Movie;
import id.sch.smktelkom_mlg.privateassigment.xirpl329.movie.R;

public class GridViewAdapter extends BaseAdapter {
    private final Context context;
    private List<Movie> urls = new ArrayList<Movie>();


    public GridViewAdapter(Context context, List<Movie> urls) {
        this.context = context;
        this.urls = urls;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public Movie getItem(int position) {
        return urls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_poster, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);

        Movie movie = getItem(position);

        Glide.with(context) //
                .load(movie.getPoster_path()) //
                .placeholder(R.drawable.ic_image_black_24dp) //
                .error(R.drawable.ic_error_black_24dp) //
                .into(imageView);

        return convertView;
    }
}
