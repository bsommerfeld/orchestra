package de.bsommerfeld.orchestra.ui.view;

import com.google.inject.Inject;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.Modality;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Provides and manages multiple JavaFX Stage instances.
 * 
 * <p>This class allows the creation and management of multiple windows in the application.
 * Each stage can be identified by a unique name and can display different views.
 * 
 * <p>The StageProvider works alongside the ViewProvider to allow different views
 * to be displayed in different windows.
 */
public class StageProvider {

    private final Map<String, Stage> stages = new ConcurrentHashMap<>();
    private final ViewProvider viewProvider;
    
    private Stage primaryStage;

    @Inject
    public StageProvider(ViewProvider viewProvider) {
        this.viewProvider = viewProvider;
    }

    /**
     * Sets the primary stage of the application.
     * 
     * <p>This method should be called once during application startup,
     * typically from the {@code start} method of the JavaFX Application class.
     * 
     * @param primaryStage the primary stage of the application
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        stages.put("primary", primaryStage);
    }

    /**
     * Gets the primary stage of the application.
     * 
     * @return the primary stage of the application
     * @throws IllegalStateException if the primary stage has not been set
     */
    public Stage getPrimaryStage() {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage has not been set");
        }
        return primaryStage;
    }

    /**
     * Creates a new stage with the specified name and configuration.
     * 
     * <p>If a stage with the specified name already exists, it will be returned instead.
     * 
     * @param name the unique name of the stage
     * @param stageConfigurator a consumer that configures the stage (can be {@code null})
     * @return the created or existing stage
     */
    public Stage createStage(String name, Consumer<Stage> stageConfigurator) {
        Stage stage = stages.computeIfAbsent(name, k -> {
            Stage newStage = new Stage();
            if (primaryStage != null) {
                newStage.initOwner(primaryStage);
            }
            return newStage;
        });
        
        if (stageConfigurator != null) {
            stageConfigurator.accept(stage);
        }
        
        return stage;
    }

    /**
     * Creates a new stage with the specified name.
     * 
     * <p>If a stage with the specified name already exists, it will be returned instead.
     * 
     * @param name the unique name of the stage
     * @return the created or existing stage
     */
    public Stage createStage(String name) {
        return createStage(name, null);
    }

    /**
     * Gets a stage by its name.
     * 
     * @param name the name of the stage
     * @return the stage with the specified name, or {@code null} if no such stage exists
     */
    public Stage getStage(String name) {
        return stages.get(name);
    }

    /**
     * Removes a stage by its name.
     * 
     * <p>This method does not close the stage, it only removes it from the provider.
     * To close the stage, call {@code stage.close()} before or after removing it.
     * 
     * @param name the name of the stage to remove
     * @return the removed stage, or {@code null} if no stage with the specified name exists
     */
    public Stage removeStage(String name) {
        return stages.remove(name);
    }

    /**
     * Creates a new modal dialog stage with the specified name and configuration.
     * 
     * <p>A modal dialog blocks input to other windows in the application until it is closed.
     * 
     * @param name the unique name of the stage
     * @param stageConfigurator a consumer that configures the stage (can be {@code null})
     * @return the created or existing stage
     */
    public Stage createModalStage(String name, Consumer<Stage> stageConfigurator) {
        return createStage(name, stage -> {
            stage.initModality(Modality.APPLICATION_MODAL);
            if (stageConfigurator != null) {
                stageConfigurator.accept(stage);
            }
        });
    }

    /**
     * Shows a view in a stage with the specified name.
     * 
     * <p>If the stage does not exist, it will be created.
     * 
     * @param <T> the type of the view controller
     * @param stageName the name of the stage
     * @param viewClass the class of the view controller
     * @param title the title of the stage (can be {@code null} to keep the current title)
     * @param stageConfigurator a consumer that configures the stage (can be {@code null})
     * @param viewConfigurator a consumer that configures the view controller (can be {@code null})
     */
    public <T> void showView(String stageName, Class<T> viewClass, String title,
                            Consumer<Stage> stageConfigurator, Consumer<T> viewConfigurator) {
        ViewWrapper<T> viewWrapper = viewProvider.requestView(viewClass);
        Parent root = viewWrapper.parent();
        T controller = viewWrapper.controller();
        
        if (viewConfigurator != null) {
            viewConfigurator.accept(controller);
        }
        
        Stage stage = createStage(stageName, stageConfigurator);
        
        if (stage.getScene() == null) {
            stage.setScene(new Scene(root));
        } else {
            stage.getScene().setRoot(root);
        }
        
        if (title != null) {
            stage.setTitle(title);
        }
        
        stage.show();
        stage.toFront();
    }

    /**
     * Shows a view in a stage with the specified name.
     * 
     * <p>If the stage does not exist, it will be created.
     * 
     * @param <T> the type of the view controller
     * @param stageName the name of the stage
     * @param viewClass the class of the view controller
     * @param title the title of the stage (can be {@code null} to keep the current title)
     */
    public <T> void showView(String stageName, Class<T> viewClass, String title) {
        showView(stageName, viewClass, title, null, null);
    }

    /**
     * Shows a view in a new modal dialog.
     * 
     * <p>A modal dialog blocks input to other windows in the application until it is closed.
     * 
     * @param <T> the type of the view controller
     * @param stageName the name of the stage
     * @param viewClass the class of the view controller
     * @param title the title of the stage (can be {@code null} to keep the current title)
     * @param stageConfigurator a consumer that configures the stage (can be {@code null})
     * @param viewConfigurator a consumer that configures the view controller (can be {@code null})
     */
    public <T> void showModalView(String stageName, Class<T> viewClass, String title,
                                 Consumer<Stage> stageConfigurator, Consumer<T> viewConfigurator) {
        showView(stageName, viewClass, title, stage -> {
            stage.initModality(Modality.APPLICATION_MODAL);
            if (stageConfigurator != null) {
                stageConfigurator.accept(stage);
            }
        }, viewConfigurator);
    }

    /**
     * Shows a view in a new modal dialog.
     * 
     * <p>A modal dialog blocks input to other windows in the application until it is closed.
     * 
     * @param <T> the type of the view controller
     * @param stageName the name of the stage
     * @param viewClass the class of the view controller
     * @param title the title of the stage (can be {@code null} to keep the current title)
     */
    public <T> void showModalView(String stageName, Class<T> viewClass, String title) {
        showModalView(stageName, viewClass, title, null, null);
    }

    /**
     * Closes a stage by its name.
     * 
     * @param name the name of the stage to close
     * @return {@code true} if the stage was found and closed, {@code false} otherwise
     */
    public boolean closeStage(String name) {
        Stage stage = stages.get(name);
        if (stage != null) {
            stage.close();
            return true;
        }
        return false;
    }

    /**
     * Closes all stages except the primary stage.
     */
    public void closeAllSecondaryStages() {
        stages.forEach((name, stage) -> {
            if (!name.equals("primary") && stage != primaryStage) {
                stage.close();
            }
        });
    }
}