package nz.ara.game.view.activity;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Spinner;
import com.example.yac0105.game.R;
import com.example.yac0105.game.databinding.ActivityMainBinding;

import nz.ara.game.view.views.MapView;
import nz.ara.game.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private Spinner level_spinner;

    private MapView mapView;

    private String level_string = "Level-10";

    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        level_spinner = (Spinner) findViewById(R.id.level_spinner);

        level_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                //level_string = (String) level_spinner.getSelectedItem();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });


        //mapView = findViewById(R.id.mapview);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mainViewModel = new MainViewModel(this,level_string);

        //mapView.setWallAbovePointList(mainViewModel.getGameModel().getMazeBean().getWallAbovePointList());
        //mapView.setWallLeftPointList(mainViewModel.getGameModel().getMazeBean().getWallLeftPointList());

        binding.setMainViewModel(mainViewModel);

    }

    public static int getScreenWidthInDPs(Context context){

        final FrameLayout frameLayout = (FrameLayout) ((Activity)context).findViewById(R.id.frameLayout);

        int b = frameLayout.getMeasuredHeight();

        DisplayMetrics dm = new DisplayMetrics();

        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int widthInDP = Math.round(dm.widthPixels / dm.density);
        return widthInDP;

    }

    public static int getScreenHeightInDPs(Context context){
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);

        int heightInDP = Math.round(dm.heightPixels / dm.density);
        return heightInDP;
    }
}
