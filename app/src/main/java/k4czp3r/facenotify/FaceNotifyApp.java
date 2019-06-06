package k4czp3r.facenotify;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.topjohnwu.superuser.ContainerApp;
import com.topjohnwu.superuser.Shell;

import androidx.annotation.NonNull;

public class FaceNotifyApp extends ContainerApp {
    //ADBMOBID: ca-app-pub-2847518732058964~3723283789
    //banner: ca-app-pub-2847518732058964/5093750363
    public static final String TAG = FaceNotifyApp.class.getCanonicalName();

    private static Application application;
    public static Application getApplication(){
        return application;
    }
    public static Context getAppContext(){
        return getApplication().getApplicationContext();
    }

    public void onCreate(){
        super.onCreate();
        application = this;
    }

    static {
        Shell.Config.setFlags(Shell.FLAG_REDIRECT_STDERR);
        Shell.Config.addInitializers(FaceNotifyAppInitializer.class);
    }

    private static class FaceNotifyAppInitializer extends Shell.Initializer {
        @Override
        public boolean onInit(Context context, @NonNull Shell shell){
            return true;
        }
    }
}
