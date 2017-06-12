package id.sch.smktelkom_mlg.privateassigment.xirpl329.movie;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import id.sch.smktelkom_mlg.privateassigment.xirpl329.movie.Adapter.RecycleListAdapter;

public class MovieDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";

    RecyclerView listView;
    TextView txtYear, txtDuration, txtRelase, txtTitle, txtSinopsis;

    ImageView imageView, imageView2;

    int movie_id = 0;

    public MovieDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            //String title mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle("");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.movieitem_detail, container, false);

        txtYear = (TextView) rootView.findViewById(R.id.txt_year);
        txtDuration = (TextView) rootView.findViewById(R.id.txt_duration);
        txtRelase = (TextView) rootView.findViewById(R.id.txt_release);
        txtSinopsis = (TextView) rootView.findViewById(R.id.txt_sinopsis);
        txtTitle = (TextView) rootView.findViewById(R.id.txt_title);
        imageView = (ImageView) rootView.findViewById(R.id.img_background);
        imageView2 = (ImageView) rootView.findViewById(R.id.imageView2);
        listView = (RecyclerView) rootView.findViewById(R.id.listView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setAutoMeasureEnabled(true);

        Bundle bundle = getArguments();

        listView.setLayoutManager(linearLayoutManager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            listView.setNestedScrollingEnabled(false);
        }

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity());
        linearLayoutManager2.setAutoMeasureEnabled(true);

        if (imageView != null) {
            Glide.with(getActivity()) //
                    .load(bundle.getString("backdrop_path")) //
                    //.placeholder(R.drawable.placeholder) //
                    //.error(R.drawable.error) //
                    //.fit() //
                    .into(imageView);
        }

        Glide.with(getActivity()) //
                .load(bundle.getString("poster_path")) //
                //.placeholder(R.drawable.placeholder) //
                //.error(R.drawable.error) //
                //.fit() //
                .into(imageView2);

        String release_date = bundle.getString("year");
        txtYear.setText(release_date.split("-")[0]);
        txtTitle.setText(bundle.getString("title"));
        txtDuration.setText("Rating : " + String.valueOf(bundle.getFloat("duration", 0)));

        txtRelase.setText("Release on :" + bundle.getString("release"));
        txtSinopsis.setText(bundle.getString("sinopsis"));
        movie_id = bundle.getInt("id", 0);

        GetTrailer getTrailer = new GetTrailer();
        String url = "http://api.themoviedb.org/3/movie/" + movie_id + "/videos";
        getTrailer.execute(url);
        return rootView;
    }

    public class GetTrailer extends AsyncTask<String, Void, List<Trailer>> {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        private final String LOG_TAG = GetTrailer.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Trailer> doInBackground(String... params) {
            List<Trailer> data = new ArrayList<>();
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonResult = "";
            try {
                final String BASE_URL = params[0];

                final String API_KEY = "?api_key=21d22e0ffa464aaefeec2e66b681f55c";
                Uri builtUri = Uri.parse(BASE_URL + API_KEY).buildUpon()
                        .build();
                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                jsonResult = buffer.toString();
                JSONObject result = new JSONObject(jsonResult);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    JSONArray data_json = result.getJSONArray("results");
                    for (int i = 0; i < data_json.length(); i++) {
                        Trailer trailer = new Trailer();
                        JSONObject object = data_json.getJSONObject(i);

                        trailer.setTitle(object.getString("name"));
                        trailer.setUrl("http://www.youtube.com/watch?v=" + object.getString("key"));
                        data.add(trailer);
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(final List<Trailer> trailers) {
            super.onPostExecute(trailers);
            progressDialog.hide();
            RecycleListAdapter recycleListAdapter = new RecycleListAdapter(getActivity(), trailers, getActivity());
            listView.setAdapter(recycleListAdapter);
        }
    }
}
