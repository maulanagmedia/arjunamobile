package id.net.gmedia.absensipsp.Adapter;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * Created by Bayu on 24/01/2018.
 */

public class CustomLinearLayoutManager extends LinearLayoutManager {
    public CustomLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }
    @Override
    public boolean canScrollVertically() {
        return false;
    }
}

