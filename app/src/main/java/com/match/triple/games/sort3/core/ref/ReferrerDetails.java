package com.match.triple.games.sort3.core.ref;

import android.os.Bundle;

import com.match.triple.games.sort3.core.Strigerium;


public class ReferrerDetails {
  private final Bundle mOriginalBundle;


    private static final String KEY_INSTALL_REFERRER = Strigerium.INSTANCE.getReferrerKeyInstall();
    private static final String KEY_REFERRER_CLICK_TIMESTAMP = Strigerium.INSTANCE.getReferrerKeyClickTimestamp();
    private static final String KEY_INSTALL_BEGIN_TIMESTAMP = Strigerium.INSTANCE.getReferrerKeyInstallBeginTimestamp();

  public ReferrerDetails(Bundle referrerBundle) {
    mOriginalBundle = referrerBundle;
  }

  public String getInstallReferrer() {
    return mOriginalBundle.getString(KEY_INSTALL_REFERRER);
  }

  public long getReferrerClickTimestampSeconds() {
    return mOriginalBundle.getLong(KEY_REFERRER_CLICK_TIMESTAMP);
  }

  public long getInstallBeginTimestampSeconds() {
    return mOriginalBundle.getLong(KEY_INSTALL_BEGIN_TIMESTAMP);
  }
}