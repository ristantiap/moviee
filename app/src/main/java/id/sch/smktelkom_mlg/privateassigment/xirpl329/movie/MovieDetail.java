package id.sch.smktelkom_mlg.privateassigment.xirpl329.movie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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
import id.sch.smktelkom_mlg.privateassigment.xirpl329.movie.Adapter.TrailerListAdapter;

public class MovieDetail extends AppCompatActivity {

    TextView txtYear, txtDuration, txtRelase, txtTitle, txtSinopsis;
    ImageView imageView, imageView2;
    RecyclerView listView;
    int movie_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            actionBar.setTitle(intent.getStringExtra("title"));
        }
        txtYear = (TextView) findViewById(R.id.txt_year);
        txtDuration = (TextView) findViewById(R.id.txt_duration);
        txtRelase = (TextView) findViewById(R.id.txt_release);
        txtSinopsis = (TextView) findViewById(R.id.txt_sinopsis);
        txtTitle = (TextView) findViewById(R.id.txt_title);

        imageView = (ImageView) findViewById(R.id.img_background);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        listView = (RecyclerView) findViewById(R.id.listView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setAutoMeasureEnabled(true);

        listView.setLayoutManager(linearLayoutManager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            listView.setNestedScrollingEnabled(false);
        }
        // Trigger the download of the URL asynchronously into the image view.
        Glide.with(getApplicationContext()) //
                .load(intent.getStringExtra("backdrop_path")) //
                //.placeholder(R.drawable.placeholder) //
                //.error(R.drawable.error) //
                //.fit() //
                .into(imageView);

        Glide.with(getApplicationContext()) //
                .load(intent.getStringExtra("poster_path")) //
                //.placeholder(R.drawable.placeholder) //
                //.error(R.drawable.error) //
                //.fit() //
                .into(imageView2);

        String release_date = intent.getStringExtra("year");
        txtYear.setText(release_date.split("-")[0]);
        txtTitle.setText(intent.getStringExtra("title"));
        txtDuration.setText("Rating : " + String.valueOf(intent.getFloatExtra("duration", 0)));

        txtRelase.setText("Release on " + intent.getStringExtra("release"));
        txtSinopsis.setText(intent.getStringExtra("sinopsis"));
        movie_id = intent.getIntExtra("id", 0);
        GetTrailer getTrailer = new GetTrailer();
        String url = "http://api.themoviedb.org/3/movie/" + movie_id + "/videos";
        getTrailer.execute(url);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    public class GetTrailer extends AsyncTask<String, Void, List<Trailer>> {
        final ProgressDialog progressDialog = new ProgressDialog(MovieDetail.this);
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

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
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
            TrailerListAdapter trailerListAdapter = new TrailerListAdapter(getApplicationContext(), trailers);
            RecycleListAdapter recycleListAdapter = new RecycleListAdapter(getApplicationContext(), trailers, MovieDetail.this);

            listView.setAdapter(recycleListAdapter);
        }
    }
}
