package robii.cryptowallet.controler.db;

import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.Database;

import robii.cryptowallet.model.Buying;
import robii.cryptowallet.model.CoinImageUrl;

/**
 * Created by Robert Sabo on 19-Feb-18.
 */

@Database(entities = {Buying.class, CoinImageUrl.class}, version = 4)
public abstract class MyDatabase extends RoomDatabase {
    public abstract BuyingDao buyingDao();
    public abstract CoinImageUrlDao coinImageUrlDao();
}
