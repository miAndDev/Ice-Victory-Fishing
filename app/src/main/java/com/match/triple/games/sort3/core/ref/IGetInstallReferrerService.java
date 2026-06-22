package com.match.triple.games.sort3.core.ref;

import android.annotation.SuppressLint;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import com.match.triple.games.sort3.core.Strigerium;


public interface IGetInstallReferrerService extends IInterface {
    Bundle getInstallReferrer(Bundle var1) throws RemoteException;

    abstract class Stub extends Binder implements IGetInstallReferrerService {
        private static final String DESCRIPTOR = Strigerium.INSTANCE.getAidlDescriptorInstallReferrer();
        static final int TRANSACTION_getInstallReferrer = 1;

        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        public static IGetInstallReferrerService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            } else {
                IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
                return (IGetInstallReferrerService)(iin != null && iin instanceof IGetInstallReferrerService ? (IGetInstallReferrerService)iin : new Proxy(obj));
            }
        }

        public IBinder asBinder() {
            return this;
        }

        @SuppressLint("WrongConstant")
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    Bundle _arg0;
                    if (0 != data.readInt()) {
                        _arg0 = (Bundle)Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }

                    Bundle _result = this.getInstallReferrer(_arg0);
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(1);
                        _result.writeToParcel(reply, TRANSACTION_getInstallReferrer);
                    } else {
                        reply.writeInt(0);
                    }

                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        private static class Proxy implements IGetInstallReferrerService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            public Bundle getInstallReferrer(Bundle paramaters) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                Bundle _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if (paramaters != null) {
                        _data.writeInt(1);
                        paramaters.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }

                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = (Bundle)Bundle.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }
        }
    }
}