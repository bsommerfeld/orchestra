# View System

This package contains the view system for the Orchestra application. It provides a way to load and manage views and their controllers, as well as manage multiple windows (stages) in the application.

## Components

### View Annotation

The `@View` annotation marks a class as a view controller. It is used by the ViewProvider to check if a class is a valid view before attempting to load it.

```java
@View
public class MyController {
    // ...
}
```

### ViewWrapper

The `ViewWrapper` record bundles a JavaFX Parent node (the root node of an FXML scene graph) with its associated controller.

```java
public record ViewWrapper<T>(Parent parent, T controller) {
}
```

### ViewLoader

The `ViewLoader` class is responsible for loading FXML views and their controllers. It uses JavaFX's FXMLLoader to load the view from an FXML file and returns a ViewWrapper containing both the Parent node and its controller.

```java
ViewLoader viewLoader = injector.getInstance(ViewLoader.class);
ViewWrapper<MyController> viewWrapper = viewLoader.loadView(MyController.class);
```

### ViewProvider

The `ViewProvider` class manages a cache of views and provides methods to request views and trigger view changes. It uses the ViewLoader to load views when they are not already in the cache.

```java
ViewProvider viewProvider = injector.getInstance(ViewProvider.class);

// Request a view
ViewWrapper<MyController> viewWrapper = viewProvider.requestView(MyController.class);

// Trigger a view change
viewProvider.triggerViewChange(MyController.class);

// Trigger a view change with configuration
viewProvider.triggerViewChange(MyController.class, controller -> {
    controller.setData(someData);
});

// Register a view change listener
viewProvider.registerViewChangeListener(MyController.class, viewWrapper -> {
    // Do something when the view changes
    Parent root = viewWrapper.parent();
    MyController controller = viewWrapper.controller();
    // ...
});
```

### StageProvider

The `StageProvider` class manages multiple JavaFX Stage instances (windows). It works alongside the ViewProvider to allow different views to be displayed in different windows.

## Using StageProvider

The StageProvider is initialized in the Orchestra class and is available as a singleton through Guice dependency injection.

```java
StageProvider stageProvider = injector.getInstance(StageProvider.class);
```

### Setting the Primary Stage

The primary stage is the main window of the application. It is set in the Orchestra class during application startup.

```java
stageProvider.setPrimaryStage(stage);
```

### Creating and Managing Stages

You can create and manage multiple stages (windows) using the StageProvider.

```java
// Create a new stage
Stage stage = stageProvider.createStage("myStage");

// Create a new modal stage
Stage modalStage = stageProvider.createModalStage("myModalStage");

// Get a stage by name
Stage stage = stageProvider.getStage("myStage");

// Remove a stage
stageProvider.removeStage("myStage");

// Close a stage
stageProvider.closeStage("myStage");

// Close all secondary stages
stageProvider.closeAllSecondaryStages();
```

### Showing Views in Stages

You can show views in stages using the StageProvider.

```java
// Show a view in a stage
stageProvider.showView("myStage", MyController.class, "My Window");

// Show a view in a stage with configuration
stageProvider.showView("myStage", MyController.class, "My Window",
    stage -> {
        // Configure the stage
        stage.setWidth(800);
        stage.setHeight(600);
    },
    controller -> {
        // Configure the controller
        controller.setData(someData);
    }
);

// Show a view in a modal dialog
stageProvider.showModalView("myDialog", DialogController.class, "Dialog");

// Show a view in a modal dialog with configuration
stageProvider.showModalView("myDialog", DialogController.class, "Dialog",
    stage -> {
        // Configure the stage
        stage.setResizable(false);
    },
    controller -> {
        // Configure the controller
        controller.setData(someData);
    }
);
```

## Example: Opening a New Window

Here's an example of how to open a new window with a specific view:

```java
@Inject
private StageProvider stageProvider;

public void openNewWindow() {
    stageProvider.showView("detailView", DetailController.class, "Detail View",
        stage -> {
            stage.setWidth(600);
            stage.setHeight(400);
        },
        controller -> {
            controller.setItem(selectedItem);
        }
    );
}
```

## Example: Opening a Modal Dialog

Here's an example of how to open a modal dialog:

```java
@Inject
private StageProvider stageProvider;

public void openDialog() {
    stageProvider.showModalView("dialog", DialogController.class, "Confirmation",
        null,
        controller -> {
            controller.setOnConfirm(() -> {
                // Handle confirmation
                saveChanges();
            });
        }
    );
}
```

## Handling Views in Multiple Stages

When showing the same view in multiple stages simultaneously, you might encounter a JavaFX error:

```
java.lang.IllegalArgumentException: [Node] is already set as root of another scene
```

This happens because JavaFX doesn't allow a node to be the root of multiple scenes at the same time. By default, the ViewProvider caches views, so when you request the same view multiple times, it returns the same instance with the same root node.

### Solution

To fix this issue, you need to ensure that a new instance of the view is created each time it's shown in a different stage. You can do this by adding the view controller class to the `nonCachedClasses` array in the ViewProvider:

```java
// In ViewProvider.java
private final Class<?>[] nonCachedClasses = {
    MyController.class,  // This view can be shown in multiple stages
    AnotherController.class
};
```

When a class is in this array, the ViewProvider will create a new instance of the view each time it's requested, instead of returning the cached instance. This ensures that each stage has its own unique root node.

### When to Use This Approach

You should add a view controller class to the `nonCachedClasses` array when:

1. The view might be shown in multiple stages simultaneously
2. You're experiencing the "is already set as root of another scene" error
3. You need a fresh instance of the view each time it's requested, regardless of caching

Note that disabling caching for a view means that its state won't be preserved between requests, which might be desirable in some cases but not in others. Consider your specific requirements when deciding whether to cache a view or not.