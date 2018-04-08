package robii.cryptowallet;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import robii.cryptowallet.model.Buying;

/**
 * Created by Robert Sabo on 09-Apr-18.
 */

public class BuyingsAdapter extends BaseAdapter {

    List<Buying> buyings;

    public BuyingsAdapter(List<Buying> buyingList){
        buyings = buyingList;
    }

    @Override
    public int getCount() {
        return buyings.size();
    }

    @Override
    public Object getItem(int i) {
        return buyings.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
