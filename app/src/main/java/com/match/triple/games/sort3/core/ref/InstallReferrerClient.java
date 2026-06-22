package com.match.triple.games.sort3.core.ref;

import android.content.Context;
import android.os.RemoteException;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class InstallReferrerClient {

  @Retention(RetentionPolicy.SOURCE)
  @IntDef({
    InstallReferrerResponse.SERVICE_DISCONNECTED,
    InstallReferrerResponse.OK,
    InstallReferrerResponse.SERVICE_UNAVAILABLE,
    InstallReferrerResponse.FEATURE_NOT_SUPPORTED,
    InstallReferrerResponse.DEVELOPER_ERROR
  })
  public @interface InstallReferrerResponse {
    int SERVICE_DISCONNECTED = -1;
    int OK = 0;
    int SERVICE_UNAVAILABLE = 1;
    int FEATURE_NOT_SUPPORTED = 2;
    int DEVELOPER_ERROR = 3;
  }

  public static final class Builder {
    private final Context mContext;

    private Builder(Context context) {
      mContext = context;
    }

    @UiThread
    public InstallReferrerClient build() {
      if (mContext == null) {
        throw new IllegalArgumentException("Please provide a valid Context.");
      }

      return new InstallReferrerClientImpl(mContext);
    }
  }

  @UiThread
  public static Builder newBuilder(@NonNull Context context) {
    return new Builder(context);
  }

  @UiThread
  public abstract boolean isReady();

  @UiThread
  public abstract void startConnection(@NonNull final InstallReferrerStateListener listener);

  @UiThread
  public abstract void endConnection();

  @UiThread
  public abstract ReferrerDetails getInstallReferrer() throws RemoteException;
}