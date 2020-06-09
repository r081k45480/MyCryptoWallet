package robii.cryptowallet.model.comparators;

import java.util.Comparator;

import robii.cryptowallet.model.Coin;



public class CoinAmountComparator implements Comparator<Coin>{
    @Override
    public int compare(Coin coin, Coin t1) {
        return  -1*Double.compare(coin.getInput(), t1.getInput());
    }
}
