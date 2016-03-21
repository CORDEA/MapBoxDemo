package jp.cordea.mapboxdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapFragment extends Fragment {

    private static final String accessToken = "";

    @Bind(R.id.direction)
    FloatingActionButton directionButton;

    @Bind(R.id.marker)
    FloatingActionButton markerButton;

    @Bind(R.id.map_view)
    MapView mapView;

    public MapFragment() {
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);

        mapView.setAccessToken(accessToken);
        mapView.setStyleUrl(Style.MAPBOX_STREETS);
        mapView.setInfoWindowAdapter(new MapView.InfoWindowAdapter() {
            @Nullable
            @Override
            public View getInfoWindow(@NonNull final Marker marker) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_tooltip, null, false);
                ImageButton button = (ImageButton) view.findViewById(R.id.close);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        marker.hideInfoWindow();
                        marker.remove();
                    }
                });
                TextView latitude = (TextView) view.findViewById(R.id.latitude);
                TextView longitude = (TextView) view.findViewById(R.id.longitude);
                latitude.setText(marker.getTitle());
                longitude.setText(marker.getSnippet());
                return view;
            }
        });
        mapView.onCreate(savedInstanceState);

        final View directionCustomView = LayoutInflater.from(getContext()).inflate(R.layout.direction_dialog, null, false);
        final EditText latDepartureView = (EditText) directionCustomView.findViewById(R.id.latitude_departure);
        final EditText lonDepartureView = (EditText) directionCustomView.findViewById(R.id.longitude_departure);
        final AlertDialog directionDialog = new AlertDialog
                .Builder(getContext())
                .setTitle(R.string.direction_dialog_title)
                .setView(directionCustomView)
                .setPositiveButton(R.string.dialog_positive_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText latArrivalView = (EditText) directionCustomView.findViewById(R.id.latitude_arrival);
                        EditText lonArrivalView = (EditText) directionCustomView.findViewById(R.id.longitude_arrival);
                        try {
                            Float latDeparture = Float.parseFloat(latDepartureView.getText().toString());
                            Float lonDeparture = Float.parseFloat(lonDepartureView.getText().toString());
                            Float latArrival = Float.parseFloat(latArrivalView.getText().toString());
                            Float lonArrival = Float.parseFloat(lonArrivalView.getText().toString());
                            String url = getRequestUrl(latDeparture, lonDeparture, latArrival, lonArrival);
                            getDirection(url);
                        } catch (NumberFormatException | IOException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                })
                .create();
        directionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng = mapView.getLatLng();
                latDepartureView.setText(String.format(Locale.US, "%f", latLng.getLatitude()));
                lonDepartureView.setText(String.format(Locale.US, "%f", latLng.getLongitude()));
                directionDialog.show();
            }
        });

        final View markerCustomView = LayoutInflater.from(getContext()).inflate(R.layout.marker_dialog, null, false);
        final EditText latitudeView = (EditText) markerCustomView.findViewById(R.id.latitude);
        final EditText longitudeView = (EditText) markerCustomView.findViewById(R.id.longitude);
        final AlertDialog markerDialog = new AlertDialog
                .Builder(getContext())
                .setTitle(R.string.marker_dialog_title)
                .setView(markerCustomView)
                .setPositiveButton(R.string.dialog_positive_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String latitudeString = latitudeView.getText().toString();
                            String longitudeString = longitudeView.getText().toString();
                            Float latitude = Float.parseFloat(latitudeString);
                            Float longitude = Float.parseFloat(longitudeString);
                            MarkerOptions options = new MarkerOptions()
                                    .position(new LatLng(latitude, longitude));
                            options.title(latitudeString);
                            options.snippet(longitudeString);
                            mapView.addMarker(options);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                })
                .create();
        markerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng = mapView.getLatLng();
                latitudeView.setText(String.format(Locale.US, "%f", latLng.getLatitude()));
                longitudeView.setText(String.format(Locale.US, "%f", latLng.getLongitude()));
                markerDialog.show();
            }
        });
        return view;
    }

    private void getDirection(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                PolylineOptions options = parseJson(response.body().string());
                if (options != null) {
                    mapView.addPolyline(options);
                }
            }
        });
    }

    private String getRequestUrl(Float latDep, Float lonDep, Float latArr, Float lonArr) {
        String latLng = String.format(Locale.US, "%f,%f;%f,%f", lonDep, latDep, lonArr, latArr);
        return String.format("https://api.mapbox.com/v4/directions/mapbox.driving/%s.json?access_token=%s", latLng, accessToken);
    }

    private PolylineOptions parseJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("routes");
            jsonObject = jsonArray.getJSONObject(0).getJSONObject("geometry");
            jsonArray = jsonObject.getJSONArray("coordinates");
            PolylineOptions options = new PolylineOptions();
            options.color(ContextCompat.getColor(getContext(), R.color.colorAccent));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONArray array = jsonArray.getJSONArray(i);
                options.add(new LatLng(array.getDouble(1), array.getDouble(0)));
            }
            return options;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
