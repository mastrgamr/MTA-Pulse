package net.mastrgamr.mtapulse.tools;

import android.content.Context;

import it.carlom.stikkyheader.core.StikkyHeader;
import it.carlom.stikkyheader.core.StikkyHeaderBuilder;
import it.carlom.stikkyheader.core.animator.HeaderStikkyAnimator;

/**
 * Project: MTA Pulse
 * Created: Stuart Smith
 * Date: 3/25/2015
 */

/**
 * My extension to StikkyHeader to allow for AOSP-HeaderGridView list.
 */
public class StikkyHeaderBuilderEx extends StikkyHeaderBuilder {

    protected StikkyHeaderBuilderEx(Context context) {
        super(context);
    }

    public static HeaderGridViewBuilder stickTo(final HeaderGridView gridView) {
        return new HeaderGridViewBuilder(gridView);
    }

    public static class HeaderGridViewBuilder extends StikkyHeaderBuilder {

        private final HeaderGridView mGridView;

        protected HeaderGridViewBuilder(final HeaderGridView gridView) {
            super(gridView.getContext());
            mGridView = gridView;
        }

        @Override
        public StikkyHeaderGridView build() {

            //if the animator has not been set, the default one is used
            if (mAnimator == null) {
                mAnimator = new HeaderStikkyAnimator();
            }

            return new StikkyHeaderGridView(mContext, mGridView, mHeader, mMinHeight, mAnimator);
        }
    }

    @Override
    public StikkyHeader build() {
        return null;
    }
}
