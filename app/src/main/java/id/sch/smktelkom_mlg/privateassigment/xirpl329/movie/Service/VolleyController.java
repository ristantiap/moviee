package id.sch.smktelkom_mlg.privateassigment.xirpl329.movie.Service;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import id.sch.smktelkom_mlg.privateassigment.xirpl329.movie.MainActivity;

public class VolleyController extends Application {

    public static final String TAG = VolleyController.class.getSimpleName();
    private static Context mCtx;
    private static volatile VolleyController mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private VolleyController(Context context) {
        if (mInstance != null) {
            throw new RuntimeException(
                    "Use getInstance() method to get the single instance of this class");
        }
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static VolleyController getInstance(Context context) {
        if (mInstance == null) {
            synchronized (VolleyController.class) {
                if (mInstance == null) mInstance = new VolleyController(context);
            }
        }
        return mInstance;
    }

    public static synchronized VolleyController getInstance(MainActivity.FetchMovies fetchMovies) {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
