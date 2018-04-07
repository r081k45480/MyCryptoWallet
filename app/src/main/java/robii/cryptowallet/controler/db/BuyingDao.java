package robii.cryptowallet.controler.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import robii.cryptowallet.model.Buying;

/**
 * Created by Robert Sabo on 19-Feb-18.
 */

@Dao
public interface BuyingDao {

    @Query("SELECT * FROM Buying WHERE symbol = :symbol")
    List<Buying> getAllBySymbol(String symbol);

    @Insert
    void insertAll(Buying... buyings);

    @Delete
    void delete(Buying b);

    @Query("SELECT symbol, sum(input) as input, max(date) as date, sum(input/price) as price " +
            "FROM Buying WHERE input > 0 GROUP BY symbol")
    List<Buying> getGrouped();

    @Query("SELECT symbol, sum(input) AS input, max(date) AS date, max(input/price) AS price FROM Buying GROUP BY symbol HAVING symbol =:symbol LIMIT 1")
    Buying getGrouped(String symbol);
}
