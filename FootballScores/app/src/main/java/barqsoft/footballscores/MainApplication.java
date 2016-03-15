package barqsoft.footballscores;

import android.app.Application;

import java.security.Provider;

import barqsoft.footballscores.api.FixtureManager;
import barqsoft.footballscores.api.ProviderFactory;



/**
 * Created by xiaoma on 14/03/16.
 */
public class MainApplication extends Application {
    private FixtureManager mFixtureManager;
    private barqsoft.footballscores.api.Provider mProvider;
    private static MainApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        sInstance.initializeInstance();
    }

    protected void initializeInstance() {
        // do all your initialization here
        mFixtureManager = new FixtureManager();
        ProviderFactory mProviderFactory = new ProviderFactory(this, mFixtureManager);
        mProvider = mProviderFactory.getProvider();
    }

    public FixtureManager getFixtureManager() {
        return mFixtureManager;
    }

    public barqsoft.footballscores.api.Provider getProvider() {
        return mProvider;
    }
}
