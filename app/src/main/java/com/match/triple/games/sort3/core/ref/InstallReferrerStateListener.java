package com.match.triple.games.sort3.core.ref;

public interface InstallReferrerStateListener {
  void onInstallReferrerSetupFinished(@InstallReferrerClient.InstallReferrerResponse int responseCode);
  void onInstallReferrerServiceDisconnected();
}