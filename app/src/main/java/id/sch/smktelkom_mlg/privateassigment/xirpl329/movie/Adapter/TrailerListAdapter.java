package id.sch.smktelkom_mlg.privateassigment.xirpl329.movie.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import id.sch.smktelkom_mlg.privateassigment.xirpl329.movie.R;
import id.sch.smktelkom_mlg.privateassigment.xirpl329.movie.Trailer;

public class TrailerListAdapter extends BaseAdapter {
    private final Context context;
    private List<Trailer> urls = new ArrayList<Trailer>();


    public TrailerListAdapter(Context context, List<Trailer> urls) {
        this.context = context;
        this.urls = urls;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public Trailer getItem(int position) {
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
                    R.layout.item_list_trailer, parent, false);
        }

        TextView txt_title = (TextView) convertView.findViewById(R.id.txt_tittle);

        Trailer trailer = getItem(position);
        txt_title.setText(trailer.getTitle());

        return convertView;
    }
}
