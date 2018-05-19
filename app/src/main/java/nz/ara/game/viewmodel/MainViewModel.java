package nz.ara.game.viewmodel;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
//import android.databinding.BaseObservable;
//import android.databinding.Bindable;

import nz.ara.game.model.impl.game.GameImpl;
import nz.ara.game.model.in.point.Point;

/**
 * Created by yac0105 on 18/05/2018.
 */
public class MainViewModel  {

    //context
    private Context context;
    //model
    private GameImpl gameModel;

    private String[] levels;

    public final ObservableList<Point> wallLeftPointList = new ObservableArrayList<>();

    public final ObservableList<Point> wallAbovePointList = new ObservableArrayList<>();

    public MainViewModel(Context context){
        this.context = context;

        this.init();


    }

    private void init(){
        gameModel = new GameImpl();
        levels = gameModel.getLevels();
        this.setLevels(levels);
    }

   // @Bindable
    public String[] getLevels() {
        levels = gameModel.getLevels();
        return levels;
    }

    public void setLevels(String[] levels) {
        this.levels = levels;
    }


}
