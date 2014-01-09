package de.inovex.chromecast.presentation;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.MediaRouteButton;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.cast.ApplicationChannel;
import com.google.cast.ApplicationMetadata;
import com.google.cast.ApplicationSession;
import com.google.cast.CastContext;
import com.google.cast.CastDevice;
import com.google.cast.MediaRouteAdapter;
import com.google.cast.MediaRouteHelper;
import com.google.cast.MediaRouteStateChangeListener;
import com.google.cast.SessionError;

import java.io.IOException;

public class MainActivity extends FragmentActivity implements MediaRouteAdapter {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String APP_ID = "APP_ID";

    private CastContext mCastContext;
    private CastDevice mDevice;
    private ApplicationSession mSession;
    private PresentationStream mPresentationStream;

    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private MenuItem mMediaRouteItem;
    private MediaRouteButton mMediaRouteButton;
    private MediaRouter.Callback mMediaRouterCallback;

    private LinearLayout mButtonContainer;
    private Button mPreviousButton;
    private Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPresentationStream = new PresentationStream();

        mCastContext = new CastContext(getApplicationContext());
        MediaRouteHelper.registerMinimalMediaRouteProvider(mCastContext, this);
        mMediaRouter = MediaRouter.getInstance(getApplicationContext());
        mMediaRouteSelector = MediaRouteHelper.buildMediaRouteSelector(MediaRouteHelper.CATEGORY_CAST);
        mMediaRouterCallback = new CustomMediaRouterCallback();
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback, MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);

        mButtonContainer = (LinearLayout)findViewById(R.id.main_buttoncontainer);

        mNextButton = (Button)findViewById(R.id.main_nextbutton);
        mNextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSession != null && mSession.hasChannel()) {
                    mPresentationStream.nextSlide();
                }
            }
        });

        mPreviousButton = (Button)findViewById(R.id.main_prevbutton);
        mPreviousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSession != null && mSession.hasChannel()) {
                    mPresentationStream.previousSlide();
                }
            }
        });

    }

    private void endSession() {
        if ((mSession != null) && (mSession.hasStarted())) {
            try {
                mSession.endSession();
            } catch (IOException e) {
                Log.e(TAG, "Failed to end the session.", e);
            } catch (IllegalStateException e) {
                Log.e(TAG, "Unable to end session.", e);
            } finally {
                mSession = null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        endSession();
        mMediaRouter.removeCallback(mMediaRouterCallback);
        MediaRouteHelper.unregisterMediaRouteProvider(mCastContext);
        mCastContext.dispose();
        mCastContext = null;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        mMediaRouteItem = menu.findItem(R.id.action_mediaroute_cast);
        mMediaRouteButton = (MediaRouteButton) mMediaRouteItem.getActionView();
        mMediaRouteButton.setRouteSelector(mMediaRouteSelector);
        return true;
    }

    @Override
    public void onDeviceAvailable(CastDevice castDevice, String routeId, MediaRouteStateChangeListener mediaRouteStateChangeListener) {
        mDevice = castDevice;

        mSession = new ApplicationSession(mCastContext, mDevice);
        try {
            mSession.startSession(APP_ID);
        } catch (IOException e) {
            Log.e(TAG, "Failed to open a session", e);
        }
        mSession.setListener(new ApplicationSession.Listener() {
            @Override
            public void onSessionStarted(ApplicationMetadata applicationMetadata) {
                ApplicationChannel channel = mSession.getChannel();
                if (channel != null) {
                    channel.attachMessageStream(mPresentationStream);
                    mPresentationStream.start();
                    mButtonContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSessionStartFailed(SessionError sessionError) {
            }

            @Override
            public void onSessionEnded(SessionError sessionError) {
                mButtonContainer.setVisibility(View.GONE);
                if (!mMediaRouter.isRouteAvailable(mMediaRouteSelector, 0)) {
                    mMediaRouteItem.setVisible(false);
                }
            }
        });
    }

    @Override
    public void onSetVolume(double v) {

    }

    @Override
    public void onUpdateVolume(double v) {

    }

    private class CustomMediaRouterCallback extends MediaRouter.Callback {
        @Override
        public void onRouteAdded(MediaRouter router, MediaRouter.RouteInfo route) {
            mMediaRouteItem.setVisible(true);
            super.onRouteAdded(router, route);
        }

        @Override
        public void onRouteRemoved(MediaRouter router, MediaRouter.RouteInfo route) {
            endSession();
            super.onRouteRemoved(router, route);
        }

        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo route) {
            MediaRouteHelper.requestCastDeviceForRoute(route);
        }

        @Override
        public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo route) {
            mDevice = null;
            endSession();
        }
    }
}
