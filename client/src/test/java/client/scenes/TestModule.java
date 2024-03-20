/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package client.scenes;

import client.ConfigLoader;
import client.MyWebSocketClient;
import client.utils.SceneManager;
import client.utils.ServerUtils;
import commons.Event;
import com.google.inject.*;
import com.google.inject.Module;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Set;
import org.mockito.Mockito;

public class TestModule implements Module {

    /**
     * It injects our view controllers
     * 
     * @param binder used for injecting
     */
    @Override
    public void configure(Binder binder) {
        // binder.bind(MainCtrl.class).in(Scopes.SINGLETON);
        // binder.bind(LoginCtrl.class).in(Scopes.SINGLETON);
    }

    /**
     * Instantiate the websocket dependency
     * 
     * @param config the ConfigLoader instance which will be injected
     * @param mainCtrl the Main Controller
     * @return the instantiated websocket
     * @throws URISyntaxException if the server address is wrong
     */
    @Provides
    @Singleton
    public MyWebSocketClient provideMyWebSocketClient(ConfigLoader config, MainCtrl mainCtrl)
            throws URISyntaxException {
        MyWebSocketClient mock = Mockito.mock(MyWebSocketClient.class);
        return mock;
        // return new MyWebSocketClient(config, mainCtrl);
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
     * Provides the server utils for mocking. It gives a fake event for the id "testCode"
     *
     * @param webSocketClient An implementation of webSocketClient. Won't be used since it is a
     *        mock.
     * @return mock ServerUtils
     */
    @Provides
    @Singleton
    public ServerUtils provideServerUtils(MyWebSocketClient webSocketClient) {
        ServerUtils mock = Mockito.mock(ServerUtils.class);

        Event event = new Event("testCode", "name", LocalDateTime.now(), Set.of());
        event.setExpenses(Set.of());
        Mockito.when(mock.getEventById(event.getInviteCode())).thenReturn(event);

        return mock;
    }

    /**
     * Provides the MainCtrl for mocking. It gives a fake SceneManager.
     *
     * @return mock MainCtrl
     */
    @Provides
    @Singleton
    public MainCtrl provideMainCtrl() {
        MainCtrl mock = Mockito.mock(MainCtrl.class);

        Mockito.when(mock.getSceneManager()).thenReturn(Mockito.mock(SceneManager.class));

        return mock;
    }
}
