package jp.cordea.mapboxdemo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.views.MapView;

/**
 * Created by Yoshihiro Tanaka on 16/03/18.
 */
public class CustomAdapter implements MapView.InfoWindowAdapter {

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null;
    }

}
