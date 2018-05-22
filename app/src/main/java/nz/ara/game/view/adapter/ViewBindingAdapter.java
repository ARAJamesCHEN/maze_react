package nz.ara.game.view.adapter;

import android.databinding.BindingAdapter;
import android.databinding.ObservableList;
import android.view.View;

import nz.ara.game.model.in.point.Point;
import nz.ara.game.view.views.MapView;

/**
 * Created by yac0105 on 22/05/2018.
 */

public class ViewBindingAdapter {

    @BindingAdapter("app:itemsWallAboveStr")
    public static void setItemsWallAboveStr(MapView mapView, String wallAbovePointListStr) {
        mapView.setItemsWallAboveStr(wallAbovePointListStr);
    }

    @BindingAdapter("app:itemsWallLeftStr")
    public static void setItemsWallLeftStr(MapView mapView, String wallAbovePointListStr) {
        mapView.setItemsWallLeftStr(wallAbovePointListStr);
    }

}
