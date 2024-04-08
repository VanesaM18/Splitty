/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;

import java.net.URISyntaxException;

import client.utils.EmailManager;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;

import client.scenes.AddEditTagsCtrl;
import client.scenes.AppConfigurationCtrl;
import client.scenes.ExpenseCtrl;
import client.scenes.ExpenseTypeCtrl;
import client.scenes.InviteScreenCtrl;
import client.scenes.LoginCtrl;
import client.scenes.MainCtrl;
import client.scenes.ManagementCtrl;
import client.scenes.OpenDebtsCtrl;
import client.scenes.OverviewCtrl;
import client.scenes.ParticipantsCtrl;
import client.scenes.SettingsCtrl;
import client.scenes.StartScreenCtrl;
import client.scenes.StatisticsCtrl;
import client.utils.SceneManager;

public class MyModule implements Module {

    /**
     * It injects our view controllers
     * 
     * @param binder used for injecting
     */
    @Override
    public void configure(Binder binder) {
        binder.bind(SceneManager.class).in(Scopes.SINGLETON);
        binder.bind(AddEditTagsCtrl.class).in(Scopes.SINGLETON);
        binder.bind(AppConfigurationCtrl.class).in(Scopes.SINGLETON);
        binder.bind(ExpenseCtrl.class).in(Scopes.SINGLETON);
        binder.bind(ExpenseTypeCtrl.class).in(Scopes.SINGLETON);
        binder.bind(InviteScreenCtrl.class).in(Scopes.SINGLETON);
        binder.bind(LoginCtrl.class).in(Scopes.SINGLETON);
        binder.bind(MainCtrl.class).in(Scopes.SINGLETON);
        binder.bind(ManagementCtrl.class).in(Scopes.SINGLETON);
        binder.bind(OpenDebtsCtrl.class).in(Scopes.SINGLETON);
        binder.bind(OverviewCtrl.class).in(Scopes.SINGLETON);
        binder.bind(ParticipantsCtrl.class).in(Scopes.SINGLETON);
        binder.bind(SettingsCtrl.class).in(Scopes.SINGLETON);
        binder.bind(StartScreenCtrl.class).in(Scopes.SINGLETON);
        binder.bind(StatisticsCtrl.class).in(Scopes.SINGLETON);
    }

    /**
     * Instantiate the websocket dependency
     * 
     * @param config   the ConfigLoader instance which will be injected
     * @param mainCtrl the Main Controller
     * @return the instantiated websocket
     * @throws URISyntaxException if the server address is wrong
     */
    @Provides
    @Singleton
    public MyWebSocketClient provideMyWebSocketClient(ConfigLoader config, MainCtrl mainCtrl)
            throws URISyntaxException {
        return new MyWebSocketClient(config, mainCtrl);
    }

    /**
     * Provides the configuration file for our client
     * 
     * @return the instance referring to our configuration file
     */
    @Provides
    @Singleton
    public ConfigLoader provideConfigLoader() {
        return new ConfigLoader();
    }

    /**
     * Provides the email manager for our client
     *
     * @return the instance referring to our email manager
     */
    @Provides
    @Singleton
    public EmailManager provideEmailManager(ConfigLoader config) {
        return new EmailManager(config);
    }
}
