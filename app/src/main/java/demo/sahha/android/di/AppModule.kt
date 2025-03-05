package demo.sahha.android.di

import android.content.Context
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.NodeClient
import com.google.android.gms.wearable.Wearable
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import demo.sahha.android.domain.manager.HealthServiceManager
import demo.sahha.android.domain.manager.WearableMessageManager
import demo.sahha.android.framework.manager.HealthServiceManagerImpl
import demo.sahha.android.framework.manager.WearableMessageManagerImpl
import demo.sahha.android.presentation.util.TextManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideHealthManager(
        @ApplicationContext context: Context,
    ): HealthServiceManager {
        return HealthServiceManagerImpl(context)
    }

    @Provides
    @Singleton
    fun provideWearableMessageManager(
        nodeClient: NodeClient,
        messageClient: MessageClient,
    ): WearableMessageManager {
        return WearableMessageManagerImpl(
            nodeClient,
            messageClient
        )
    }

    @Provides
    @Singleton
    fun provideTextManager(): TextManager {
        return TextManager()
    }

    @Provides
    @Singleton
    fun provideNodeClient(
        @ApplicationContext context: Context,
    ): NodeClient {
        return Wearable.getNodeClient(context)
    }

    @Provides
    @Singleton
    fun provideMessageClient(
        @ApplicationContext context: Context,
    ): MessageClient {
        return Wearable.getMessageClient(context)
    }
}