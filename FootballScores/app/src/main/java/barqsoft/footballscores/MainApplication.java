package barqsoft.footballscores;

import android.app.Application;

import barqsoft.footballscores.api.FixtureManager;
import barqsoft.footballscores.api.ProviderFactory;
import barqsoft.footballscores.api.Provider;



/**
 * Created by xiaoma on 14/03/16.
 */
public class MainApplication extends Application {
    private FixtureManager mFixtureManager;
    private Provider mProvider;
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

    public Provider getProvider() {
        return mProvider;
    }
}
