package com.almareng.earthquakemonitor.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;

public final class SyncAuthenticator extends AbstractAccountAuthenticator {

    public SyncAuthenticator(final Context context) {
        super(context);
    }

    // No properties to edit.
    @Override
    public Bundle editProperties(final AccountAuthenticatorResponse r, final String s) {
        throw new UnsupportedOperationException();
    }

    // Because we're not actually adding an account to the device, just return null.
    @Override
    public Bundle addAccount(final AccountAuthenticatorResponse r,
                             final String s,
                             final String s2,
                             final String[] strings,
                             final Bundle bundle) throws NetworkErrorException {
        return null;
    }

    // Ignore attempts to confirm credentials
    @Override
    public Bundle confirmCredentials(final AccountAuthenticatorResponse r, final Account account, final Bundle bundle)
            throws NetworkErrorException {
        return null;
    }

    // Getting an authentication token is not supported
    @Override
    public Bundle getAuthToken(final AccountAuthenticatorResponse r,
                               final Account account,
                               final String s,
                               final Bundle bundle) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    // Getting a label for the auth token is not supported
    @Override
    public String getAuthTokenLabel(final String s) {
        throw new UnsupportedOperationException();
    }

    // Updating user credentials is not supported
    @Override
    public Bundle updateCredentials(final AccountAuthenticatorResponse r,
                                    final Account account,
                                    final String s,
                                    final Bundle bundle) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    // Checking features for the account is not supported
    @Override
    public Bundle hasFeatures(final AccountAuthenticatorResponse r,
                              final Account account,
                              final String[] strings) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }
}
