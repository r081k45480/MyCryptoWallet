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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;


import robii.cryptowallet.controler.CoinManager;
import robii.cryptowallet.controler.CoinManagerImpl;
import robii.cryptowallet.controler.db.MyDatabase;
import robii.cryptowallet.model.Buying;
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
                    inputTestData();
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

    public static void openAddNewCoin(Activity parent){
        Intent intent = new Intent(parent, AddNewBuyin.class);
        parent.startActivityForResult(intent,AddNewBuyin.REQUEST_CODE);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        try {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu, menu);
            return true;
        } catch (Exception e){
            Log.e("ERROR", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_new_button_menu_item:
                openAddNewCoin(this);
                return true;
            case R.id.refresh_menu_item:
                onRefresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void inputTestData() throws ExecutionException, InterruptedException {
        final Buying btc = new Buying();
        btc.setSymbol("BTC");
        btc.setDate(new Date(117, 11, 3));
        btc.setInput(200.0);
        btc.setAmount(0.04075);
        btc.setPrice(dolarToEuro(7225.0));

        final Buying eth1= new Buying();
        eth1.setSymbol("ETH");
        eth1.setDate(new Date(117, 11, 20));
        eth1.setInput(50.0);
        eth1.setAmount(0.15);
        eth1.setPrice(dolarToEuro(420.0));

        final Buying eth2= new Buying();
        eth2.setSymbol("ETH");
        eth2.setDate(new Date(117, 12, 1));
        eth2.setInput(50.0);
        eth2.setAmount(0.151);
        eth2.setPrice(dolarToEuro(720.0));

        final Buying bcc= new Buying();
        bcc.setSymbol("BCH");
        bcc.setDate(new Date(118, 1,1 ));
        bcc.setInput(50.0);
        bcc.setAmount(0.13675);
        bcc.setPrice(dolarToEuro(2400.0));

        final Buying btg= new Buying();
        btg.setSymbol("BTG");
        btg.setDate(new Date(118, 1,20 ));
        btg.setInput(50.0);
        btg.setAmount(0.71073);
        btg.setPrice(dolarToEuro(236.0));

        final Buying dash = new Buying();
        dash.setSymbol("DASH");
        dash.setDate(new Date(118, 1,20 ));
        dash.setInput(50.0);
        dash.setAmount(0.12566);
        dash.setPrice(dolarToEuro(1024.0));

        final Buying xrp = new Buying();
        xrp.setSymbol("XRP");
        xrp.setDate(new Date(118, 1,20 ));
        xrp.setInput(50.0);
        xrp.setAmount(70.2389);
        xrp.setPrice(dolarToEuro(2.1));

        List<Buying> oo1 = Common.getFuture(new Callable<List<Buying>>() {
            @Override
            public List<Buying> call() {
                database.buyingDao().insertAll(btc, eth1, eth2, bcc, xrp, btg, dash);
                return null;
            }
        }).get();

    }

    public static double dolarToEuro(double in){
        return in*0.85;
    }

}
