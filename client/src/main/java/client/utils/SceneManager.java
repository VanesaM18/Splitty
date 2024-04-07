package client.utils;

import client.scenes.MainCtrl;
import commons.Event;

import java.util.EmptyStackException;
import java.util.Stack;

import com.google.inject.Inject;

public class SceneManager {
    private MainCtrl mainCtrl;
    private final Stack<SceneEnum> sceneHistory = new Stack<>();;

    private Event event;

    /**
     * Create a scene manager
     *
     * @param mainCtrl The main controller
     */
    @Inject
    public SceneManager(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    /**
     * gets the current event.
     * 
     * @return current event.
     */
    public Event getEvent() {
        return event;
    }

    /**
     * gets the current scene from the scene history.
     * 
     * @return current scene.
     */
    private SceneEnum getCurrentScene() {
        return sceneHistory.peek();
    }

    /**
     * navigates to the previous scene.
     */
    public void goBack() {
        sceneHistory.pop();
        showScene(sceneHistory.peek(), this.event);
    }

    /**
     * shows the current scene.
     */
    public void showCurrentScene() {
        showScene(sceneHistory.peek(), this.event);
    }

    /**
     * pushes a new scene to the scene history stack.
     * 
     * @param scene scene
     */
    public void pushScene(SceneEnum scene) {
        if (this.sceneHistory.isEmpty() || this.sceneHistory.peek() != scene)
            sceneHistory.push(scene);
    }

    /**
     * pops the current scene from the scene history stack.
     * 
     * @return the popped scene.
     */
    public SceneEnum popScene() {
        try {
            return sceneHistory.pop();
        } catch (EmptyStackException e) {
            return SceneEnum.START;
        }
    }

    /**
     * pushes a new scene to the scene history stack
     * with an associated event.
     * 
     * @param scene scene
     * @param event event associated with the scene.
     */
    public void pushScene(SceneEnum scene, Event event) {
        this.event = event != null ? event : this.event;
        this.pushScene(scene);
    }

    /**
     * shows the specified scene.
     * 
     * @param scene scene
     * @param event event associated with the scene.
     */
    public void showScene(SceneEnum scene, Event event) {
        this.event = event;
        switch (scene) {
            case STARTUP:
                mainCtrl.showAppConfiguration();
                break;
            case LOGIN:
                mainCtrl.showLogin();
                break;
            case MANAGEMENT:
                mainCtrl.showManagementOverview();
                break;
            case SETTINGS:
                mainCtrl.showSettings();
                break;
            case START:
                mainCtrl.showStartScreen();
                break;
            case OVERVIEW:
                mainCtrl.showOverviewEvent(event);
                break;
            case INVITE:
                mainCtrl.showInviteScreen(event);
            default:
                break;
        }

    }
}
