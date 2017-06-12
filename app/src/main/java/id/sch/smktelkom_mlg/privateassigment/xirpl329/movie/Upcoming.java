package id.sch.smktelkom_mlg.privateassigment.xirpl329.movie;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

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

import id.sch.smktelkom_mlg.privateassigment.xirpl329.movie.Adapter.GridViewAdapter;
import id.sch.smktelkom_mlg.privateassigment.xirpl329.movie.Model.Movie;
import id.sch.smktelkom_mlg.privateassigment.xirpl329.movie.Service.API;
import id.sch.smktelkom_mlg.privateassigment.xirpl329.movie.Service.GsonGetRequest;
import id.sch.smktelkom_mlg.privateassigment.xirpl329.movie.Service.VolleyController;


/**
 * A simple {@link Fragment} subclass.
 */
public class Upcoming extends Fragment {

    GridView gv;

    public Upcoming() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);
        Upcoming.FetchMovies fetchMovies = new Upcoming.FetchMovies();
        String url = "http://api.themoviedb.org/3/movie/upcoming";
        fetchMovies.execute(url);
        gv = (GridView) view.findViewById(R.id.movie_list);
        return view;
    }

    public class FetchMovies extends AsyncTask<String, Void, List<Movie>> {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        private final String LOG_TAG = Upcoming.FetchMovies.class.getSimpleName();

        @Override
        protected void onPreExecute() {

            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loading movies...");
            progressDialog.show();
            super.onPreExecute();
        }

        protected String FetchMovies(String... params) {
            String url = "http://api.themoviedb.org/3/movie/popular";
            GsonGetRequest<Movie> myRequest = new GsonGetRequest<Movie>
                    (url, Movie.class, null, new Response.Listener<Movie>() {

                        @Override
                        public void onResponse(Movie response) {
                            Log.d("FLOW", "onResponse: " + (new Gson().toJson(response)));
                        }

                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("FLOW", "onErrorResponse: ", error);
                        }
                    });
            VolleyController.getInstance(getActivity()).addToRequestQueue(myRequest);

            String sortingCriteria = params[0];
            Uri builtUri = Uri.parse(API.API_URL).buildUpon()
                    .appendQueryParameter("sort_by", sortingCriteria + ".desc")
                    .appendQueryParameter("api_key", API.API_KEY)
                    .build();
            String response;
            try {
                response = getJSON(builtUri);
                return response;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public String getJSON(Uri builtUri) {
            InputStream inputStream;
            StringBuffer buffer;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJson = null;

            try {
                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                inputStream = urlConnection.getInputStream();
                buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                moviesJson = buffer.toString();
            } catch (IOException e) {
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {

                    }
                }
            }
            return moviesJson;
        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            List<Movie> data = new ArrayList<>();

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

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    JSONArray data_json = result.getJSONArray("results");
                    for (int i = 0; i < data_json.length(); i++) {
                        Movie movie = new Movie();
                        JSONObject object = data_json.getJSONObject(i);

                        movie.setId(object.getInt("id"));
                        movie.setPoster_path("http://image.tmdb.org/t/p/w185" + object.getString("poster_path"));
                        movie.setPoster_path("http://image.tmdb.org/t/p/w185" + object.getString("poster_path"));
                        movie.setBackdrop_path("http://image.tmdb.org/t/p/w780" + object.getString("backdrop_path"));
                        movie.setRelease_date(object.getString("release_date"));
                        movie.setTitle(object.getString("title"));
                        movie.setOverview(object.getString("overview"));
                        movie.setVote_average(object.getLong("vote_average"));
                        data.add(movie);
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
        protected void onPostExecute(final List<Movie> movies) {
            super.onPostExecute(movies);
            progressDialog.hide();
            GridViewAdapter gridViewAdapter = new GridViewAdapter(getActivity().getApplicationContext(), movies);
            gv.setAdapter(gridViewAdapter);

            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Movie movie = movies.get(position);
                    Intent intent = new Intent(getActivity(), MovieDetail.class);
                    intent.putExtra("poster_path", movie.getPoster_path());
                    intent.putExtra("backdrop_path", movie.getBackdrop_path());
                    intent.putExtra("year", movie.getRelease_date());
                    intent.putExtra("release", movie.getRelease_date());
                    intent.putExtra("sinopsis", movie.getOverview());
                    intent.putExtra("title", movie.getTitle());
                    intent.putExtra("duration", movie.getVote_average());
                    intent.putExtra("id", movie.getID());

                    startActivity(intent);
                }
            });
        }
    }

}
