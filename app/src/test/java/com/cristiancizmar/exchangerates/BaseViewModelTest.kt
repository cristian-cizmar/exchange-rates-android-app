package com.cristiancizmar.exchangerates

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule
import org.mockito.Mockito
import org.mockito.Mockito.mock

abstract class BaseViewModelTest {

    protected val lifecycleOwner: LifecycleOwner = mock()
    private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(lifecycleOwner)

    protected val loadingObserver: Observer<Boolean> = mock()

    //used to mock a live data field
    @get:Rule
    var rule = InstantTaskExecutorRule()

    protected fun registerLifecycle() {
        Mockito.`when`(lifecycleOwner.lifecycle).thenReturn(lifecycleRegistry)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    companion object {

        @BeforeClass
        @JvmStatic
        fun setupRxSchedulers() {
            RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.trampoline() }
            RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
            RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        }

        @AfterClass
        @JvmStatic
        fun resetRxSchedulers() {
            RxJavaPlugins.reset()
            RxAndroidPlugins.reset()
        }
    }
}