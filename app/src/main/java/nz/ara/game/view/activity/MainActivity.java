package nz.ara.game.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.yac0105.game.R;
import com.example.yac0105.game.databinding.ActivityMainBinding;

import java.io.File;

import nz.ara.game.model.em.constvalue.Const;
import nz.ara.game.view.views.MapView;
import nz.ara.game.view.views.RoleView;
import nz.ara.game.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Spinner level_spinner;

    private MapView mapView;

    private RoleView theView;

    private RoleView minView;

    private TextView textViewName;

    private Button reset;

    private Button pause;

    private Button save;

    private Button loadByFile;

    private String level_string = "Level-1";

    private MainViewModel mainViewModel;

    private Context context;

    private ActivityMainBinding binding;

    private int rolePointXShort = 100;

    private int rolePointXLong = 100;

    private int rolePointYShort = 200;

    private int rolePointYLong = 200;

    private float startX;
    private float startY;
    private int offsetsByX;
    private int offsetsByY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mapView = findViewById(R.id.mapview);

        if(mapView == null){
            FrameLayout f = findViewById(R.id.frameLayout);

            mapView = (MapView)f.getChildAt(0);

            theView = (RoleView)f.getChildAt(1);

            minView = (RoleView)f.getChildAt(2);

            theView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    return roleViewOnTouched(event);
                }
            });


        }


        level_spinner = findViewById(R.id.level_spinner);

        level_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                spinnerItemSelected();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });


        reset = findViewById(R.id.button_reset);

        reset.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetButtonClicked();
                }
            }
        );

        pause = findViewById(R.id.button_pause);

        pause.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pauseButtonClicked();
                    }
                }
        );

        save = findViewById(R.id.button_save);

        save.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveButtonClicked();
                    }
                }
        );


        loadByFile = findViewById(R.id.button_new);

        loadByFile.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadByFileButtonClicked();
                    }
                }
        );

        if(mainViewModel == null){
            mainViewModel = new MainViewModel(this,level_string);
        }

        binding.setMainViewModel(mainViewModel);

    }


    private boolean roleViewOnTouched(MotionEvent event){
        rolePointXShort = theView.getRolePointXShort();
        rolePointXLong = theView.getRolePointXLong();
        rolePointYShort = theView.getRolePointYShort();
        rolePointYLong = theView.getRolePointYLong();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX=event.getX();
                startY=event.getY();

                if(mainViewModel.moveThe(rolePointXShort,rolePointXLong,rolePointYShort,rolePointYLong,startX,startY)){
                    theView.invalidate();

                    if(mainViewModel.getGameModel().getTheseus().isHasWon()){
                        theWinDialog();
                    }
                }

                if(mainViewModel.moveMin()){
                    minView.invalidate();
                }

                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if(mainViewModel.moveMin()){
                    minView.invalidate();
                }
                break;
            default:
                return false;
        }

        if(mainViewModel.getGameModel().getMinotaur().isHasEaten()){
            minView.bringToFront();
            minKillTheDialog();
        }

        Log.d(TAG, "Touch Event::" + event.getAction());
        return true;
    }


    /**
     * spinnerItemSelected
     */
    private void spinnerItemSelected(){
        String aNewlevel_string = (String) level_spinner.getSelectedItem();

        if(!level_string.equals(aNewlevel_string)){
            level_string = aNewlevel_string;
            if(mainViewModel == null){
                mainViewModel = new MainViewModel(context,aNewlevel_string);
            }else{
                mainViewModel.initGameImpl(aNewlevel_string);
                theView.bringToFront();
                mapView.invalidate();
                theView.invalidate();
                minView.invalidate();
            }
        }
    }

    private void resetButtonClicked(){
        mainViewModel.initGameImpl(level_string);
        theView.bringToFront();
        mapView.invalidate();
        theView.invalidate();
        minView.invalidate();
    }

    private void pauseButtonClicked(){

        mainViewModel.moveMin();
        mainViewModel.moveMin();

        if(mainViewModel.getGameModel().getMinotaur().isHasEaten()){
            minView.bringToFront();
            minKillTheDialog();
        }

        minView.invalidate();
    }

    private void saveButtonClicked(){
        mainViewModel.initGameImpl(level_string);

        File directory = context.getFilesDir();

        boolean isSuccessful = mainViewModel.save(directory);

        String fileP = directory.getAbsolutePath() + File.separator + Const.LEVEL_FILE_NAME.getValue();

        if(isSuccessful){
            infoDialog("Successful", "Save to " + fileP + " successfully!" );
        }else{
            infoDialog("Failure", "Save to " + fileP + " Fail!" );
        }
    }

    private void loadByFileButtonClicked(){
        mainViewModel.initGameImplByFile(level_string);

        File directory = context.getFilesDir();

        boolean isSuccessful = mainViewModel.save(directory);

        String fileP = directory.getAbsolutePath() + File.separator + Const.LEVEL_FILE_NAME.getValue();

        if(isSuccessful){
            infoDialog("Successful", "Save to " + fileP + " successfully!" );
        }else{
            infoDialog("Failure", "Save to " + fileP + " Fail!" );
        }
    }

    private void infoDialog(String text, String msg){

        final AlertDialog.Builder minKillTheDialog = new AlertDialog.Builder(this);

        minKillTheDialog.setTitle(text);

        minKillTheDialog.setMessage(msg);

        minKillTheDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        minKillTheDialog.show();
    }

    private void minKillTheDialog(){

        final AlertDialog.Builder minKillTheDialog = new AlertDialog.Builder(this);

        minKillTheDialog.setTitle("Minotaur killed Theseus!");

        minKillTheDialog.setMessage("Game Over");

        minKillTheDialog.setPositiveButton("OK",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    minKillTheOptionDialog();
                }
         });
        minKillTheDialog.show();
    }

    private void minKillTheOptionDialog(){
        final AlertDialog.Builder theDialog = new AlertDialog.Builder(this);

        theDialog.setTitle("Do you like to play these level again?");

        theDialog.setMessage("");

        theDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                resetButtonClicked();
            }
        });

        theDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                level_string = "Level-1";
                resetButtonClicked();
                level_spinner.setSelection(0);
            }
        });
        theDialog.show();
    }

    private void theWinDialog(){

        final AlertDialog.Builder minKillTheDialog = new AlertDialog.Builder(this);

        minKillTheDialog.setTitle("Theseus win!");

        minKillTheDialog.setMessage("Congratulations~");

        minKillTheDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        theWinOptionDialog();
                    }
                });
        minKillTheDialog.show();
    }

    private void theWinOptionDialog(){
        final AlertDialog.Builder theDialog = new AlertDialog.Builder(this);

        theDialog.setTitle("Do you like to play these level again?");

        theDialog.setMessage("");

        theDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                resetButtonClicked();
            }
        });

        theDialog.setNegativeButton("Next Level", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                int theNum = mainViewModel.getGameModel().getLevelByLevelStr(level_string);
                level_string = mainViewModel.getGameModel().getLevels()[theNum];
                resetButtonClicked();
                level_spinner.setSelection(theNum);
            }
        });
        theDialog.show();
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
