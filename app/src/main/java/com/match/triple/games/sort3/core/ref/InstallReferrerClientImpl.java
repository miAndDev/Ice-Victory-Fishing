package com.match.triple.games.sort3.core.ref;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;


import com.match.triple.games.sort3.core.Strigerium;

import java.lang.annotation.Retention;
import java.util.List;

class InstallReferrerClientImpl extends InstallReferrerClient {
  private static final int PLAY_STORE_MIN_APP_VER = 80837300;

    private static final String SERVICE_PACKAGE_NAME = Strigerium.INSTANCE.getServicePackageVending();
    private static final String SERVICE_NAME = Strigerium.INSTANCE.getServiceNameInstallReferrer();
    private static final String SERVICE_ACTION_NAME = Strigerium.INSTANCE.getServiceActionBindInstallReferrer();

  @IntDef({
    ClientState.DISCONNECTED,
    ClientState.CONNECTING,
    ClientState.CONNECTED,
    ClientState.CLOSED
  })
  @Retention(SOURCE)
  public @interface ClientState {
    int DISCONNECTED = 0;
    int CONNECTING = 1;
    int CONNECTED = 2;
    int CLOSED = 3;
  }

  private @ClientState int mClientState = ClientState.DISCONNECTED;

  private final Context mApplicationContext;

  private IGetInstallReferrerService mService;

  private ServiceConnection mServiceConnection;

  public InstallReferrerClientImpl(@NonNull Context context) {
    mApplicationContext = context.getApplicationContext();
  }

  @Override
  public boolean isReady() {
    return mClientState == ClientState.CONNECTED && mService != null && mServiceConnection != null;
  }

  @Override
  public void startConnection(@NonNull InstallReferrerStateListener listener) {
    if (isReady()) {
      listener.onInstallReferrerSetupFinished(InstallReferrerResponse.OK);
      return;
    }

    if (mClientState == ClientState.CONNECTING) {
      listener.onInstallReferrerSetupFinished(InstallReferrerResponse.DEVELOPER_ERROR);
      return;
    }

    if (mClientState == ClientState.CLOSED) {
      listener.onInstallReferrerSetupFinished(InstallReferrerResponse.DEVELOPER_ERROR);
      return;
    }
    mServiceConnection = new InstallReferrerServiceConnection(listener);
    Intent serviceIntent = new Intent(SERVICE_ACTION_NAME);
    serviceIntent.setComponent(new ComponentName(SERVICE_PACKAGE_NAME, SERVICE_NAME));
    List<ResolveInfo> intentServices =
        mApplicationContext.getPackageManager().queryIntentServices(serviceIntent, 0);

    if (!intentServices.isEmpty()) {
      ResolveInfo resolveInfo = intentServices.get(0);
      if (resolveInfo.serviceInfo != null) {
        String packageName = resolveInfo.serviceInfo.packageName;
        String className = resolveInfo.serviceInfo.name;
        if (SERVICE_PACKAGE_NAME.equals(packageName)
            && className != null
            && isPlayStoreCompatible()) {
          Intent explicitServiceIntent = new Intent(serviceIntent);
          boolean connectionResult =
              mApplicationContext.bindService(
                  explicitServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
            if (!connectionResult) {
                mClientState = ClientState.DISCONNECTED;
                listener.onInstallReferrerSetupFinished(InstallReferrerResponse.SERVICE_UNAVAILABLE);
            }
        } else {
          mClientState = ClientState.DISCONNECTED;
          listener.onInstallReferrerSetupFinished(InstallReferrerResponse.FEATURE_NOT_SUPPORTED);
        }
          return;
      }
    }
    mClientState = ClientState.DISCONNECTED;
    listener.onInstallReferrerSetupFinished(InstallReferrerResponse.FEATURE_NOT_SUPPORTED);
  }

  @Override
  public void endConnection() {
    mClientState = ClientState.CLOSED;
    if (mServiceConnection != null) {
      mApplicationContext.unbindService(mServiceConnection);
      mServiceConnection = null;
    }
    mService = null;
  }
  @Override
  public ReferrerDetails getInstallReferrer() throws RemoteException {
    if (!isReady()) {
      throw new IllegalStateException("not connected");
    }
    Bundle paramsBundle = new Bundle();
    paramsBundle.putString(Strigerium.INSTANCE.getPackageName(), mApplicationContext.getPackageName());
    try {
        return new ReferrerDetails(mService.getInstallReferrer(paramsBundle));
    } catch (RemoteException e) {
      mClientState = ClientState.DISCONNECTED;
      throw e;
    }
  }
  private boolean isPlayStoreCompatible() {
    PackageManager mPm = mApplicationContext.getPackageManager();
    try {
      PackageInfo info = mPm.getPackageInfo(SERVICE_PACKAGE_NAME, PackageManager.GET_META_DATA);
      int versionCode = info.versionCode;
      return versionCode >= PLAY_STORE_MIN_APP_VER;
    } catch (PackageManager.NameNotFoundException e) {
      return false;
    }
  }

  private final class InstallReferrerServiceConnection implements ServiceConnection {
    private final InstallReferrerStateListener mListener;

    private InstallReferrerServiceConnection(@NonNull InstallReferrerStateListener listener) {
        mListener = listener;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
      mService = IGetInstallReferrerService.Stub.asInterface(iBinder);
      mClientState = ClientState.CONNECTED;
      mListener.onInstallReferrerSetupFinished(InstallReferrerResponse.OK);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
      mService = null;
      mClientState = ClientState.DISCONNECTED;
      mListener.onInstallReferrerServiceDisconnected();
    }
  }
}