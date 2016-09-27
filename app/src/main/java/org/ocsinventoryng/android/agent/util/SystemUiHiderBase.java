/*
 * Copyright 2013-2016, OCSInventory-NG/AndroidAgent contributors
 *
 * This file is part of OCSInventory-NG/AndroidAgent.
 *
 * OCSInventory-NG/AndroidAgent is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OCSInventory-NG/AndroidAgent is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OCSInventory-NG/AndroidAgent. If not, see <http://www.gnu.org/licenses/>
 */
package org.ocsinventoryng.android.agent.util;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;

/**
 * A base implementation of {@link SystemUiHider}. Uses APIs available in all API levels to show and hide the status bar.
 */
public class SystemUiHiderBase extends SystemUiHider {
    /**
     * Whether or not the system UI is currently visible. This is a cached value from calls to {@link #hide()} and {@link
     * #show()}.
     */
    private boolean mVisible = true;

    /**
     * Constructor not intended to be called by clients. Use {@link SystemUiHider#getInstance} to obtain an instance.
     */
    protected SystemUiHiderBase(Activity activity, View anchorView, int flags) {
        super(activity, anchorView, flags);
    }

    @Override
    public void setup() {
        if ((mFlags & FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES) == 0) {
            mActivity.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    @Override
    public boolean isVisible() {
        return mVisible;
    }

    @Override
    public void hide() {
        if ((mFlags & FLAG_FULLSCREEN) != 0) {
            mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                           WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        mOnVisibilityChangeListener.onVisibilityChange(false);
        mVisible = false;
    }

    @Override
    public void show() {
        if ((mFlags & FLAG_FULLSCREEN) != 0) {
            mActivity.getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        mOnVisibilityChangeListener.onVisibilityChange(true);
        mVisible = true;
    }
}
