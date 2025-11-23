package com.towhid.posprinter.ui.theme

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.posprinter.IDeviceConnection
import net.posprinter.POSConnect
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PrinterModule {

    @Provides
    @Singleton
    fun provideDeviceConnection(): IDeviceConnection {
        return POSConnect.createDevice(POSConnect.DEVICE_TYPE_BLUETOOTH)
    }
}