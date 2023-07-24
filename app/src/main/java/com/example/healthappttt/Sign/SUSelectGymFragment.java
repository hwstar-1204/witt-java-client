package com.example.healthappttt.Sign;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthappttt.Data.User.LocData;
import com.example.healthappttt.R;
import com.example.healthappttt.databinding.FragmentSuSelectGymBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SUSelectGymFragment extends Fragment implements OnMapReadyCallback, LocationListener {
    FragmentSuSelectGymBinding binding;

    private LocationAdapter locationAdapter;
    private List<LocData> searchResults;


    private static final String Body = "#4A5567";
    private static final String Signature = "#05C78C";
    private static final String White = "#ffffff";
    private static final String Backgrount_2 = "#D1D8E2";



    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String ARG_LAT = "lat";
    private static final String ARG_LON = "lon";
    private static final String ARG_GYM = "gym";

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private double lat, lon, gymLat, gymLon;
    private String gymName;

    private boolean isSelected;


    PlacesClient placesClient;
    private MapView mapView;
    private GoogleMap googleMap;
    private LocationManager locationManager;

    private Location location;

    public static SUSelectGymFragment newInstance(double lat, double lon, String gymName) {
        SUSelectGymFragment fragment = new SUSelectGymFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_LAT, lat);
        args.putDouble(ARG_LON, lon);
        args.putString(ARG_GYM, gymName);

        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lat = getArguments().getDouble(ARG_LAT);
            lon = getArguments().getDouble(ARG_LON);
            gymName = getArguments().getString(ARG_GYM);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSuSelectGymBinding.inflate(inflater);

        searchResults = new ArrayList<>();

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( getActivity(), new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION}, 0 );
        } else {
        }


        // Initialize fused location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        // Initialize Places API
        Places.initialize(requireContext(), getString(R.string.google_places_api_key));
        placesClient = Places.createClient(requireContext());
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // Update location every 10 seconds
        // Create location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update current location
                    updateCurrentLocation(location);
                }
            }
        };

        // Listen to text changes in the search EditText

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (gymName != null) {
            binding.selectGym.setText(gymName);
            binding.selectGym.setTextColor(Color.parseColor(Body));
            binding.mapIcon.setColorFilter(Color.parseColor(Signature));
            binding.nextBtn.setBackground(getContext().getDrawable(R.drawable.rectangle_green_20dp));
            binding.nextBtn.setTextColor(Color.parseColor(White));
            isSelected = true;
        }

        setRecyclerView();

        binding.backBtn.setOnClickListener(v -> {
            ((SignUpActivity) getActivity()).goToSelectGender(-1, -1, false);
        });

        binding.skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SignUpActivity) getActivity()).goToInputPerf(lat, lon, null, 0, 0);
            }
        });

        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return false;
            }
        });

        binding.nextBtn.setOnClickListener(v -> {
            if (isSelected) {
                gymName = binding.selectGym.getText().toString();

                ((SignUpActivity) getActivity()).goToInputPerf(lat, lon, gymName, gymLat, gymLon);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        binding = null;
    }

    private void setRecyclerView() {
        locationAdapter = new LocationAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(locationAdapter);
    }

    private void startLocationUpdates() {
        // Request location updates
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        // Stop location updates
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void updateCurrentLocation(Location location) {
        // Update latitude and longitude text views
        lat = location.getLatitude();
        lon = location.getLongitude();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        this.location = location;
        // Handle location change
        updateCurrentLocation(location);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check location permission
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Start location updates
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        stopLocationUpdates();
    }

    private void performSearch(final String query) {
        searchResults.clear();

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> searchTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    OkHttpClient client = new OkHttpClient().newBuilder().build();

                    HttpUrl.Builder urlBuilder = HttpUrl.parse("https://maps.googleapis.com/maps/api/place/autocomplete/json").newBuilder();
                    urlBuilder.addQueryParameter("input", query);
                    urlBuilder.addQueryParameter("types", "establishment");
                    urlBuilder.addQueryParameter("location", String.valueOf(lat) + ", " + String.valueOf(lon)); // 현재 위치
                    urlBuilder.addQueryParameter("radius", "50000");
                    urlBuilder.addQueryParameter("strictbounds", "true");
                    urlBuilder.addQueryParameter("components", "country:KR"); // 주소에 대한 국가 필터링
                    urlBuilder.addQueryParameter("key", getString(R.string.google_places_api_key)); // 실제 Places API 키로 대체
                    urlBuilder.addQueryParameter("language", "ko"); // 한국어로 결과 요청

                    String url = urlBuilder.build().toString();
                    Request request = new Request.Builder()
                            .url(url)
                            .method("GET", null)
                            .build();

                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        return response.body().string();
                    } else {
                        Log.e("Error", "Request failed with code: " + response.code());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String responseBody) {
                if (responseBody != null) {
                    parseResponseAndAddLocations(responseBody);
                }
            }
        };

        searchTask.execute();
    }

    private void parseResponseAndAddLocations(String responseBody) {
        Log.d(TAG, "parseResponseAndAddLocations: " + responseBody);
        // TODO: Parse the response body and extract the required information
        // You can use JSON parsing libraries like Gson or JSONObject to parse the response

        try {
            JSONObject responseJson = new JSONObject(responseBody);
            JSONArray predictions = responseJson.getJSONArray("predictions");

            List<LocData> associatedLocations = new ArrayList<>();

            for (int i = 0; i < predictions.length(); i++) {
                JSONObject prediction = predictions.getJSONObject(i);
                JSONArray terms = prediction.getJSONArray("terms");
                String buildingName = terms.getJSONObject(0).getString("value");
                String placeId = prediction.getString("place_id");
                associatedLocations.add(new LocData(buildingName, placeId));
            }

            // Clear previous search results
            searchResults.clear();

            // Add the associated locations to the search results
            searchResults.addAll(associatedLocations);

            // Notify the adapter that the data set has changed
            locationAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }

    private class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {
        LocData L;

        class LocationViewHolder extends RecyclerView.ViewHolder {
            TextView locationTextView;
            TextView locationDetailView;

            LocationViewHolder(@NonNull View itemView) {
                super(itemView);

                locationTextView = itemView.findViewById(R.id.locationTextView);
                locationDetailView = itemView.findViewById(R.id.locationDetailView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle location item click
                        L = searchResults.get(getAbsoluteAdapterPosition());
                        Log.d(TAG, "onClicklll: "+L.getName()+"aaaa"+L.getId());
                        getPlaceDetails(L.getId());
//  ----------------------------------------------------------------------------------------------
                        binding.selectGym.setText(L.getName());
                        binding.selectGym.setTextColor(Color.parseColor(Body));
                        binding.mapIcon.setColorFilter(Color.parseColor(Signature));
                        binding.nextBtn.setBackground(getContext().getDrawable(R.drawable.rectangle_green_20dp));
                        binding.nextBtn.setTextColor(Color.parseColor(White));
                        isSelected = true;
//  ----------------------------------------------------------------------------------------------
//                        binding.mapIcon.tint
                        // Perform any action on location item click
                    }
                });
            }
        }

        @NonNull
        @Override
        public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.select_healthroom, parent, false);
            return new LocationViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
            L = searchResults.get(position);
            holder.locationTextView.setText(L.getName());
//            holder.locationDetailView.setText();
        }

        @Override
        public int getItemCount() {
            return searchResults.size();
        }

        private void getPlaceDetails(String placeId) {

            List<Place.Field> fields = Arrays.asList(Place.Field.LAT_LNG);

            FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, fields);

            placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                @Override
                public void onSuccess(FetchPlaceResponse response) {
                    Place place = response.getPlace();
                    LatLng latLng = place.getLatLng();
                    gymLat = latLng.latitude;
                    gymLon = latLng.longitude;
                    Log.d(TAG, "wwwwwww: " + gymLat + " aaaa " + gymLon);
                    // Perform any action with latitude and longitude values
//                    ((SignUpActivity) getActivity()).replaceFragment(SUInputNameFragment.newInstance(email, lat, lon, latitude, longitude, L.getName()));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle the failure
                }
            });
        }
    }
}
