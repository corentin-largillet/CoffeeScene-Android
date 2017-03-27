package com.geronimostudios.ui;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.geronimostudios.ui.annotations.CoffeeScene;
import com.geronimostudios.ui.annotations.Scene;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * <p>The {@link SceneManager} is used to initialize an {@link Activity},
 * {@link ViewGroup}, {@link android.app.Fragment} or
 * {@link android.support.v4.app.Fragment} annotated with {@link CoffeeScene}.</p>
 *
 * {@link SceneManager} is also used to switch the scenes.
 */
public final class SceneManager {

    /**
     * The default animation adapter.
     */
    private static SceneAnimationAdapter sDefaultSceneAnimationAdapter
            = new SceneAnimationAdapter() {
        @Override
        public void showView(View view, boolean animate) {
            if (animate) {
                AnimationHelper.showView(view);
            } else {
                view.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void hideView(View view, boolean animate) {
            if (animate) {
                AnimationHelper.hideView(view);
            } else {
                view.setVisibility(View.GONE);
            }
        }
    };

    /**
     * A dictionary of {@link ScenesMeta} associated with their view, activity or fragment.
     */
    private static Dictionary<Object, ScenesMeta> sScenesMeta = new Hashtable<>();

    /**
     * <p>Parse the annotation {@link CoffeeScene} of an {@link Activity}
     * and add the scenes into the {@link Activity}.</p>
     *
     * <p>This method will add each scene into {@link Activity} with
     * {@link Activity#setContentView(int)} (View)}.</p>
     *
     * @param activity an {@link Activity} that has a {@link CoffeeScene}
     */
    public static void create(@NonNull Activity activity) {
        activity.setContentView(
                doCreate(
                        activity,
                        activity,
                        sDefaultSceneAnimationAdapter,
                        new FrameLayout(activity)
                )
        );
    }

    /**
     * <p>Parse the annotation {@link CoffeeScene} of an {@link Activity}
     * and add the scenes into the {@link Activity}.</p>
     *
     * <p>This method will add each scene into {@link Activity} with
     * {@link Activity#setContentView(int)} (View)}.</p>
     *
     * @param activity an {@link Activity} that has a {@link CoffeeScene}
     * @param adapter The {@link SceneAnimationAdapter} to be used.
     */
    public static void create(@NonNull Activity activity,
                              @Nullable SceneAnimationAdapter adapter) {
        activity.setContentView(
                doCreate(activity, activity, adapter, new FrameLayout(activity))
        );
    }

    /**
     * <p>Parse the annotation {@link CoffeeScene} of a {@link ViewGroup}
     * and add the scenes into the {@link ViewGroup}.</p>
     *
     * <p>This method will add each scene into {@link ViewGroup} with
     * {@link ViewGroup#addView(View)}.</p>
     *
     * @param view a {@link ViewGroup} that has a {@link CoffeeScene}
     */
    public static void create(@NonNull ViewGroup view) {
        doCreate(view.getContext(), view, sDefaultSceneAnimationAdapter, view);
    }

    /**
     * <p>Parse the annotation {@link CoffeeScene} of a {@link ViewGroup}
     * and add the scenes into the {@link ViewGroup}.</p>
     *
     * <p>This method will add each scene into {@link ViewGroup} with
     * {@link ViewGroup#addView(View)}.</p>
     *
     * @param view a {@link ViewGroup} that has a {@link CoffeeScene}
     * @param adapter The {@link SceneAnimationAdapter} to be used.
     */
    public static void create(@NonNull ViewGroup view,
                              @Nullable SceneAnimationAdapter adapter) {
        doCreate(view.getContext(), view, adapter, view);
    }

    /**
     * Switch to another {@link Scene}.
     *
     * @param activity The parent activity.
     * @param scene The scene id. See {@link Scene#scene()}.
     */
    public static void scene(@NonNull Activity activity, int scene) {
        doChangeScene(activity, scene);
    }

    /**
     * Switch to another {@link Scene}.
     *
     * @param view The holder view.
     * @param scene The scene id. See {@link Scene#scene()}.
     */
    public static void scene(@NonNull ViewGroup view, int scene) {
        doChangeScene(view, scene);
    }

    /**
     * Parse the annotation {@link CoffeeScene}, of an object.
     * Creates the scenes and add them into a view group.
     * Save the metadata of those scenes into {@link SceneManager#sScenesMeta}.
     *
     * @param context Holding context for the inflater
     * @param object The associated activity, view or fragments that has a {@link CoffeeScene}
     * @param adapter The animation adapter to be used.
     * @param root The root view group in which the scenes will be added.
     *
     * @return The root view group that contains the scenes
     */
    private static ViewGroup doCreate(@NonNull Context context,
                                      @NonNull Object object,
                                      @Nullable SceneAnimationAdapter adapter,
                                      @NonNull ViewGroup root) {
        // Retrieve annotations
        CoffeeScene setup = safeGetSetup(object);
        Scene[] scenes = setup.value();

        // Save scenes meta data
        int defaultScene = getValidDefaultScene(setup, scenes);

        // Create root node with all scenes
        if (adapter == null) {
            adapter = sDefaultSceneAnimationAdapter;
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        for (Scene scene : scenes) {
            View view = inflater.inflate(scene.layout(), root, false);
            showOrHideView(scene.scene() == defaultScene, adapter, view, false);
            root.addView(view);
        }

        // Save the scene's meta data
        ScenesMeta meta = new ScenesMeta(
                root,
                sDefaultSceneAnimationAdapter,
                scenes,
                defaultScene
        );
        sScenesMeta.put(object, meta);
        return root;
    }

    private static int getValidDefaultScene(CoffeeScene setup, Scene[] scenes) {
        int defaultScene = setup.defaultScene();
        for (Scene scene : scenes) {
            if (scene.scene() == defaultScene) {
                return defaultScene; // the default scene specified by the user is valid
            }
        }
        return scenes[0].scene(); // the default scene is not valid
    }

    private static void showOrHideView(boolean show,
                                       @NonNull SceneAnimationAdapter adapter,
                                       @NonNull View view,
                                       boolean animate) {
        if (show) {
            adapter.showView(view, animate);
        } else {
            adapter.hideView(view, animate);
        }
    }

    private static CoffeeScene safeGetSetup(@NonNull Object object) {
        Class<?> objClass = object.getClass();
        if (!objClass.isAnnotationPresent(CoffeeScene.class)) {
            throw new IllegalArgumentException("Annotation @CoffeeScene is missing");
        }
        return objClass.getAnnotation(CoffeeScene.class);
    }

    private static ScenesMeta safeGetMetaData(@NonNull Object object) {
        ScenesMeta meta = sScenesMeta.get(object);
        if (meta == null) {
            throw new IllegalArgumentException("Scene meta not found");
        }
        return meta;
    }

    private static void doChangeScene(@NonNull Object object, int scene) {
        ScenesMeta meta = safeGetMetaData(object);

        ViewGroup root = meta.getRoot();
        SceneAnimationAdapter adapter = meta.getSceneAnimationAdapter();
        Scene[] scenes = meta.getScenes();
        for (int index = 0; index < scenes.length; ++index) {
            showOrHideView(scenes[index].scene() == scene, adapter, root.getChildAt(index), true);
        }
    }
}