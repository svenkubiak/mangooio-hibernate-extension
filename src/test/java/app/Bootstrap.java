package app;

import com.google.inject.Singleton;

import controllers.ApplicationController;
import io.mangoo.interfaces.MangooBootstrap;
import io.mangoo.routing.Bind;
import io.mangoo.routing.On;

@Singleton
public class Bootstrap implements MangooBootstrap {

    @Override
    public void initializeRoutes() {
        Bind.controller(ApplicationController.class)
            .withRoutes(On.get().to("/").respondeWith("index"));
    }
    
    @Override
    public void applicationInitialized() {
        // Do nothing for now
    }

    @Override
    public void applicationStarted() {
        // Do nothing for now
    }

    @Override
    public void applicationStopped() {
        // Do nothing for now
    }
}