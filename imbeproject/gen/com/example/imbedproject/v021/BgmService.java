/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\mabi\\workspace\\imbe3\\src\\com\\example\\imbedproject\\v021\\BgmService.aidl
 */
package com.example.imbedproject.v021;
public interface BgmService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.example.imbedproject.v021.BgmService
{
private static final java.lang.String DESCRIPTOR = "com.example.imbedproject.v021.BgmService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.example.imbedproject.v021.BgmService interface,
 * generating a proxy if needed.
 */
public static com.example.imbedproject.v021.BgmService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.example.imbedproject.v021.BgmService))) {
return ((com.example.imbedproject.v021.BgmService)iin);
}
return new com.example.imbedproject.v021.BgmService.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_bgmStart:
{
data.enforceInterface(DESCRIPTOR);
this.bgmStart();
reply.writeNoException();
return true;
}
case TRANSACTION_bgmStop:
{
data.enforceInterface(DESCRIPTOR);
this.bgmStop();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.example.imbedproject.v021.BgmService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void bgmStart() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_bgmStart, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void bgmStop() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_bgmStop, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_bgmStart = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_bgmStop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public void bgmStart() throws android.os.RemoteException;
public void bgmStop() throws android.os.RemoteException;
}
