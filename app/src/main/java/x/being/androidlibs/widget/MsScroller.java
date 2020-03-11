package x.being.androidlibs.widget;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class MsScroller extends Scroller {

    private int mDuration = 1000;

    public MsScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public void setmDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy,
                            int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }
}
