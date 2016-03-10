package barqsoft.footballscores.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by xiaoma on 10/03/16.
 */
public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory (Intent intent) {

        WidgetDataProvider dataProvider = new WidgetDataProvider(
                getApplicationContext(), intent);
        return dataProvider;

    }
}
