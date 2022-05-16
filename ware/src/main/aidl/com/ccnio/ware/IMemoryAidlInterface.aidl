// IMemoryAidlInterface.aidl
package com.ccnio.ware;

// Declare any non-default types here with import statements
import android.os.ParcelFileDescriptor;
import android.os.SharedMemory;

interface IMemoryAidlInterface {
    ParcelFileDescriptor getParcelFileDescriptor();
    SharedMemory getSharedMemory();
}