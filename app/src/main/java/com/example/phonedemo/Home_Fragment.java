package com.example.phonedemo;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Home_Fragment extends Fragment {
    private static final String TAG = "Home_Fragment";
    private static final String URL = "http://10.30.50.122/phone_shop/getPhone.php";

    private RecyclerView recyclerView;
    private List<Movie> movieList;
    private StoreAdapter mAdapter;
    ProgressBar progressBar;

    public Home_Fragment() {
        // Required empty public constructor
    }

    public static Home_Fragment newInstance(String param1, String param2) {
        Home_Fragment fragment = new Home_Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        movieList = new ArrayList<>();
        mAdapter = new StoreAdapter(getActivity(), movieList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1, RecyclerView.VERTICAL, false);

        // recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(8), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        fetchStoreItems();
        // progressBar.setVisibility(View.VISIBLE);
        mAdapter.setOnItemClickListener(new StoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Intent intent = new Intent(getContext(), ProductActivity.class);
                startActivity(intent);
            }
        });
        return view;

    }

    private void fetchStoreItems() {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.i(TAG, response.toString());
                        if (response == null) {
                            Toast.makeText(getContext(), "Couldn't fetch the store items! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        movieList.clear();
//                        List<Movie> items = new Gson().fromJson(response.toString(), new TypeToken<List<Movie>>() {
//                        }.getType());
//                        movieList.addAll(items);

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                movieList.add(new Movie(jsonObject.getString("phone_name"),
                                        jsonObject.getString("phone_price"), jsonObject.getString("phone_description"),
                                        jsonObject.getString("phone_image")));
                            } catch (JSONException e) {
                                Toast.makeText(getContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());

            }
        });
        requestQueue.add(jsonArrayRequest);
//        MyApplication.getInstance().addToRequestQueue(request);
    }

//    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
//
//        private int spanCount;
//        private int spacing;
//        private boolean includeEdge;
//
//        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
//            this.spanCount = spanCount;
//            this.spacing = spacing;
//            this.includeEdge = includeEdge;
//        }
//
//        @Override
//        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//            int position = parent.getChildAdapterPosition(view); // item position
//            int column = position % spanCount; // item column
//
//            if (includeEdge) {
//                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
//                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
//
//                if (position < spanCount) { // top edge
//                    outRect.top = spacing;
//                }
//                outRect.bottom = spacing; // item bottom
//            } else {
//                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
//                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
//                if (position >= spanCount) {
//                    outRect.top = spacing; // item top
//                }
//            }
//        }
//    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    static class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.MyViewHolder> {
        private Context context;
        private List<Movie> movieList;
        private OnItemClickListener listener;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView name, description, price, phoneName;
            public ImageView thumbnail;
            ProgressBar progressBar;

            public MyViewHolder(final View view) {
                super(view);
                phoneName = view.findViewById(R.id.name);
                name = view.findViewById(R.id.phoneName);
                price = view.findViewById(R.id.price);
                description = view.findViewById(R.id.description);
                thumbnail = view.findViewById(R.id.thumbnail);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listener != null) {
                            listener.onItemClick(view, getAdapterPosition());
                        }
                    }
                });
            }
        }


        public StoreAdapter(Context context, List<Movie> movieList) {
            this.context = context;
            this.movieList = movieList;
        }

        public interface OnItemClickListener {
            void onItemClick(View itemView, int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }
//        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        };


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final Movie movie = movieList.get(position);
            holder.name.setText(movie.getName());
            holder.price.setText(movie.getPrice());
            holder.description.setText(movie.getDescription());
            Glide.with(context)
                    .load(movie.getImage())
                    .into(holder.thumbnail);

        }

        @Override
        public int getItemCount() {
            return movieList.size();
        }
    }
}

