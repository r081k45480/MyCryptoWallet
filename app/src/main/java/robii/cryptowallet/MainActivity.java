package robii.cryptowallet;

import android.Manifest;
import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;


import robii.cryptowallet.controler.CoinManager;
import robii.cryptowallet.controler.CoinManagerImpl;
import robii.cryptowallet.controler.db.MyDatabase;
import robii.cryptowallet.model.Buying;
import robii.cryptowallet.model.Coin;
import robii.cryptowallet.model.CoinDetailed;
import robii.cryptowallet.model.CoinImageUrl;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final int PERMISSIONS_REQUEST = 8909;

    public static CoinManager coinManager;
    public static MyDatabase database;
    public static MainActivity me;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ListView myCoinsListView;

    private MyCoinsAdapter myCoinsAdapter;

    public void initMe(){
        me = this;
        new Common(getResources());
        coinManager = new CoinManagerImpl();
    }

    Bundle lastSavedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        lastSavedInstanceState = savedInstanceState;
        checkMyPermissions();

        initMe();

        if(database == null){
            database = Room.databaseBuilder(getApplicationContext(),MyDatabase.class, "mycryptos")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.app.ActionBar ba = getSupportActionBar();
        test();

        mSwipeRefreshLayout = findViewById(R.id.MainContainter);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        myCoinsListView = findViewById(R.id.mycoinsListVew);

        if(myCoinsAdapter == null)
            onRefresh();

    }

    private void checkMyPermissions() {
        ArrayList<String> arrPerm = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            arrPerm.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            arrPerm.add(Manifest.permission.INTERNET);
        }
        if(!arrPerm.isEmpty()) {
            String[] permissions = new String[arrPerm.size()];
            permissions = arrPerm.toArray(permissions);
            ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                int pCount = 0;
                for(int i = 0; i < grantResults.length; i++) {
                    String permission = permissions[i];
                    if(Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)) {
                        if(grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            pCount++;
                        }
                    }
                    if(Manifest.permission.INTERNET.equals(permission)) {
                        if(grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            pCount++;
                        }
                    }
                }
                if(pCount <= 2)
                    Toast.makeText(this,"No permissions..", Toast.LENGTH_LONG).show();// permission denied, boo! Disable the

                break;
            }
        }
    }

    private void test() {
        while(true) {
            try {
                List<Buying> oo = Common.getFuture(new Callable<List<Buying>>() {
                    @Override
                    public List<Buying> call(){
                        return database.buyingDao().getGrouped();
                    }
                }).get();
                if(oo == null || oo.size() ==0 ) {
                    final Buying b = new Buying();
                    b.setInput(500.0);
                    b.setDate(new Date(2017, 11, 3));
                    b.setSymbol("BTC");
                    b.setPrice(5000.0);

                    final Buying b1 = new Buying();
                    b1.setInput(100.0);
                    b1.setDate(new Date(2017, 12, 29));
                    b1.setPrice(10000.0);
                    b1.setSymbol("BTC");


                    final Buying cb = new Buying();
                    cb.setInput(250.0);
                    cb.setDate(new Date(2017,12,10));
                    cb.setPrice(390.0);
                    cb.setSymbol("ETH");

                    final Buying cb1 = new Buying();
                    cb1.setInput(250.0);
                    cb1.setDate(new Date());
                    cb1.setPrice(670.0);
                    cb1.setSymbol("ETH");

                    final Buying cb2 = new Buying();
                    cb2.setInput(50.0);
                    cb2.setDate(new Date());
                    cb2.setPrice(670.0);
                    cb2.setSymbol("ETH");

                    List<Buying> oo111 = Common.getFuture(new Callable<List<Buying>>() {
                        @Override
                        public List<Buying> call() {
                            coinManager.getAllCoins();
                            return null;
                        }
                    }).get();
                    List<CoinImageUrl> urls = Common.getFuture(new Callable<List<CoinImageUrl>>() {
                        @Override
                        public List<CoinImageUrl> call() {
                            return database.coinImageUrlDao().getAll();
                        }
                    }).get();

                    List<Buying> oo1 = Common.getFuture(new Callable<List<Buying>>() {
                        @Override
                        public List<Buying> call() {
                            database.buyingDao().insertAll(b, b1, cb, cb1, cb2);
                            return null;
                        }
                    }).get();

                }
                List<CoinImageUrl> urls = Common.getFuture(new Callable<List<CoinImageUrl>>() {
                    @Override
                    public List<CoinImageUrl> call() {
                        return database.coinImageUrlDao().getAll();
                    }
                }).get();

                List<Buying> oo1 = Common.getFuture(new Callable<List<Buying>>() {
                    @Override
                    public List<Buying> call() {
                        return database.buyingDao().getGrouped();
                    }
                }).get();
                System.out.println("Done!");
                break;
            }catch (Exception e) {
                Log.e("ERROR", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRefresh() {
        try {
            myCoinsAdapter = new MyCoinsAdapter(this);
            myCoinsListView.setAdapter(myCoinsAdapter);

            mSwipeRefreshLayout.setRefreshing(false);
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == AddNewBuyin.REQUEST_CODE) {
            // no refresh!
            if(resultCode == Activity.RESULT_OK){
                ;
            }
        }
    }

}
