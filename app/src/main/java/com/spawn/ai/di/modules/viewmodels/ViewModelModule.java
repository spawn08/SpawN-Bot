package com.spawn.ai.di.modules.viewmodels;

import com.spawn.ai.SpawnAiApplication;
import com.spawn.ai.viewmodels.ClassifyIntentViewModel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.multibindings.IntoMap;

@Module
@InstallIn(ApplicationComponent.class)
public abstract class ViewModelModule {

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory viewModelFactory);

    @Binds
    @IntoMap
    @ViewModelKey(ClassifyIntentViewModel.class)
    public abstract ViewModel bindClassifyIntentViewModel(ClassifyIntentViewModel classifyIntentViewModel);
}
