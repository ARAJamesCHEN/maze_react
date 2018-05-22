package nz.ara.game.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;

import com.example.yac0105.game.BR;
import com.example.yac0105.game.R;

import java.util.ArrayList;
import java.util.List;

import nz.ara.game.model.impl.game.GameImpl;
import nz.ara.game.model.in.point.Point;
import nz.ara.game.view.views.MapView;

/**
 * Created by yac0105 on 18/05/2018.
 */
public class MainViewModel  extends BaseObservable {

    //context
    private Context context;
    //model
    private GameImpl gameModel;

    // binding spnner levels
    private String[] levels;

    public final ObservableList<Point> wallLeftPointList = new ObservableArrayList<>();

    public final ObservableField<String> wallLeftPointListStr = new ObservableField<>();

    public final ObservableField<String> wallAbovePointListStr = new ObservableField<>();

    public final ObservableField<String> wallSquareStr = new ObservableField<>();

    public final ObservableField<String> thePointStr = new ObservableField<>();

    public final ObservableField<String> minPointStr = new ObservableField<>();

    public final ObservableField<String> moveCount = new ObservableField<String>();

    public MainViewModel(Context context, String level_string){
        this.context = context;

        initGameImpl(level_string);
    }

    public void initGameImpl(String level_string){
        gameModel = new GameImpl(level_string);
        wallLeftPointListStr.set(this.gameModel.getMazeBean().getWallLeftPointListStr());
        wallAbovePointListStr.set(this.gameModel.getMazeBean().getWallAbovePointListStr());
        wallSquareStr.set(this.gameModel.getMazeBean().getWallSquareStr());
        thePointStr.set(gameModel.getTheseus().getPosition().across()
                + "," + gameModel.getTheseus().getPosition().down());

        minPointStr.set(gameModel.getMinotaur().getPosition().across()
                + "," + gameModel.getMinotaur().getPosition().down());

        moveCount.set(String.valueOf(gameModel.getMoveCount()));
    }

   @Bindable
    public String[] getLevels() {
        levels = new GameImpl().getLevels();
        return levels;
    }

    public void setLevels(String[] levels) {
        this.levels = levels;
        notifyPropertyChanged(BR.levels);
    }


    public GameImpl getGameModel() {
        return gameModel;
    }

    public void setGameModel(GameImpl gameModel) {
        this.gameModel = gameModel;
    }

}
