package nz.ara.game.viewmodel;

import android.content.Context;

import com.kelin.mvvmlight.base.ViewModel;

import nz.ara.game.model.impl.game.GameImpl;

/**
 * Created by yac0105 on 18/05/2018.
 */
public class MainViewModel implements ViewModel {

    //context
    private Context context;
    //model
    private GameImpl gameModel;

    public String[] levels;

    public MainViewModel(Context context){
        this.context = context;

        this.init();


    }

    private void init(){
        gameModel = new GameImpl();
        levels = gameModel.getLevels();
    }

}
