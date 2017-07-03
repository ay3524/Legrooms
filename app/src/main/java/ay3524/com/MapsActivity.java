package ay3524.com;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ay3524.com.legrooms.R;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ProgressDialog pDialog;
    private ArrayList<MapMarkerInfo> allMarkersMap = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Add a marker in Sydney and move the camera

        performNetworkRequest(googleMap);

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                int searchListLength = allMarkersMap.size();
                for (int i = 0; i < searchListLength; i++) {
                    if (allMarkersMap.get(i).getlTitle().contains(marker.getTitle())) {
                        MapMarkerInfo mapMarkerInfo = allMarkersMap.get(i);
                        setDialogBox(mapMarkerInfo);
                    }
                }
                return true;
            }
        });

    }

    private void setDialogBox(MapMarkerInfo mapMarkerInfo) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MapsActivity.this);
        LayoutInflater inflater = LayoutInflater.from(MapsActivity.this);
        @SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        TextView title = (TextView) dialogView.findViewById(R.id.title);
        TextView description = (TextView) dialogView.findViewById(R.id.desc);
        TextView address = (TextView) dialogView.findViewById(R.id.address);
        TextView email = (TextView) dialogView.findViewById(R.id.email);
        title.setText(mapMarkerInfo.getlTitle());
        description.setText(mapMarkerInfo.getDesc());
        address.setText(mapMarkerInfo.getAddress());
        email.setText(mapMarkerInfo.getEmail());
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void performNetworkRequest(final GoogleMap mMap) {
        // Tag used to cancel the request
        String tag_string_req = "req_map";

        pDialog.setMessage("Searching...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                "https://app.legrooms.com/api/listing/Meeting/Bangalore", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("MAP", "Map Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);

                    boolean responseBooleanValue = jObj.getBoolean("success");

                    if (responseBooleanValue) {
                        JSONArray jsonArray = jObj.getJSONArray("listings");
                        parseJSON(jsonArray, mMap);

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Log.e("JSONError", e.getMessage());
                    Toast.makeText(getApplicationContext(), "Error with the server!", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.getMessage());
                hideDialog();
            }
        });
        strReq.setRetryPolicy(new DefaultRetryPolicy(5 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void parseJSON(JSONArray jsonArray, GoogleMap mMap) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String lTitle = jsonObject.getString("lTitle");
                String longitude = jsonObject.getString("longitude");
                String latitude = jsonObject.getString("latitude");

                MapMarkerInfo mapMarkerInfo = new MapMarkerInfo();
                //mapMarkerInfo.setPrice(jsonObject.getString("price"));
                mapMarkerInfo.setlRules(jsonObject.getString("lRules"));
                mapMarkerInfo.setDesc(jsonObject.getString("desc"));
                mapMarkerInfo.setlTitle(lTitle);
                mapMarkerInfo.setLongitude(longitude);
                mapMarkerInfo.setLatitude(latitude);
                mapMarkerInfo.setAddress(jsonObject.getString("address"));
                mapMarkerInfo.setEmail(jsonObject.getString("email"));

                setMarkerOnMap(latitude, longitude, mMap, lTitle);

                allMarkersMap.add(mapMarkerInfo);
            } catch (JSONException e) {
                Log.e("LATLNG Error", e.getMessage());
            }
        }
    }

    private void setMarkerOnMap(String latitude, String longitude, GoogleMap mMap, String lTitle) {
        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        mMap.addMarker(new MarkerOptions().position(latLng).title(lTitle));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mMap.getMaxZoomLevel();
        mMap.animateCamera(CameraUpdateFactory.zoomTo(6.0f));
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
