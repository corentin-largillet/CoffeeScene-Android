package com.geronimostudios.coffeescene;

import android.app.Activity;
import android.app.Fragment;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import com.geronimostudios.coffeescene.animations.AnimationAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>This {@link SceneCreator} allows to create scenes from an existing layout.</p>
 *
 * <p>Example:
 * SceneManager.create(
 *      SceneCreator.with(this)
 *          .add(Scene.MAIN, R.id.activity_no_annotations_sample_main_content)
 *          .add(Scene.SPINNER, R.id.activity_no_annotations_sample_loader)
 *          .add(Scene.PLACEHOLDER, R.id.activity_no_annotations_sample_placeholder)
 *          .main(Scene.MAIN)
 *  );</p>
 */
public final class SceneCreator {
    private final Object mReference;
    private Listener mListener;
    private final ViewGroup mRootView;
    private @Nullable
    AnimationAdapter mAdapter;
    private int mFirstSceneId;
    private List<Pair<Integer, View>> mScenes;

    private SceneCreator(@NonNull Object reference, @NonNull ViewGroup rootView) {
        mReference = reference;
        mRootView = rootView;
        mScenes = new ArrayList<>();
        mFirstSceneId = -1;
    }

    /**
     * Setup by using an Object. The view classes or view ids added by
     * {@link SceneCreator#add(int, View)} and {@link SceneCreator#add(int, int)}
     * will be searched into the parameter rootView.
     *
     * @param reference the reference that will be used to change scene.
     * @param rootView the view group that holds the scenes anchors.
     *
     * @return a {@link SceneCreator} for more configurations.
     */
    public static SceneCreator with(@NonNull Object reference, @NonNull ViewGroup rootView) {
        return new SceneCreator(
                reference,
                rootView
        );
    }

    /**
     * Setup by using an activity. The view classes or view ids added by
     * {@link SceneCreator#add(int, View)} and {@link SceneCreator#add(int, int)}
     * will be searched into the main layout of the activity.
     *
     * @param activity the reference activity which contains the children.
     * @return a {@link SceneCreator} for more configurations.
     */
    public static SceneCreator with(@NonNull Activity activity) {
        return new SceneCreator(
                activity,
                (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content))
                        .getChildAt(0)
        );
    }

    /**
     * Setup by using a {@link Fragment}. The view classes or view ids added by
     * {@link SceneCreator#add(int, View)} and {@link SceneCreator#add(int, int)}
     * will be searched into the main layout of the fragment.
     *
     * @param fragment the fragment which contains the children.
     * @return a {@link SceneCreator} for more configurations.
     */
    public static SceneCreator with(@NonNull Fragment fragment) {
        if (fragment.getView() != null) {
            return new SceneCreator(
                    fragment,
                    (ViewGroup) fragment.getView()
            );
        } else {
            throw new NullPointerException("fragment.getView() == null");
        }
    }

    /**
     * Setup by using a {@link android.support.v4.app.Fragment}.
     * The view classes or view ids added by {@link SceneCreator#add(int, View)}
     * and {@link SceneCreator#add(int, int)} will be searched into
     * the main layout of the fragment.
     *
     * @param fragment the fragment which contains the children.
     * @return a {@link SceneCreator} for more configurations.
     */
    public static SceneCreator with(@NonNull android.support.v4.app.Fragment fragment) {
        if (fragment.getView() != null) {
            return new SceneCreator(
                    fragment,
                    (ViewGroup) fragment.getView()
            );
        } else {
            throw new NullPointerException("fragment.getView() == null");
        }
    }

    /**
     * Setup by using a {@link ViewGroup}.
     * The view classes or view ids added by {@link SceneCreator#add(int, View)}
     * and {@link SceneCreator#add(int, int)} will be searched into
     * the provided view group.
     *
     * @param viewGroup the fragment which contains the children.
     * @return a {@link SceneCreator} for more configurations.
     */
    public static SceneCreator with(@NonNull ViewGroup viewGroup) {
        return new SceneCreator(viewGroup, viewGroup);
    }

    /**
     * Change the animation adapter.
     *
     * @param adapter the new animation adapter
     * @return a {@link SceneCreator} for more configurations.
     */
    public SceneCreator animation(@Nullable AnimationAdapter adapter) {
        mAdapter = adapter;
        return this;
    }

    /**
     * Add a listener. See {@link Listener} and {@link CoffeeSceneListenerAdapter}.
     *
     * @param listener the new listener
     * @return a {@link SceneCreator} for more configurations.
     */
    public SceneCreator listener(@Nullable Listener listener) {
        mListener = listener;
        return this;
    }

    /**
     * Change the default view.
     * {@link #main(int)} is now deprecated, {@link #first(int)} should be used instead.
     *
     * @param defaultSceneId the default sceneId.
     * @return a {@link SceneCreator} for more configurations.
     */
    @Deprecated
    public SceneCreator main(int defaultSceneId) {
        return first(defaultSceneId);
    }

    /**
     * Change the default view.
     *
     * @param defaultSceneId the default sceneId.
     * @return a {@link SceneCreator} for more configurations.
     */
    public SceneCreator first(int defaultSceneId) {
        mFirstSceneId = defaultSceneId;
        return this;
    }

    /**
     * Create a new scene by providing a view and sceneId.
     *
     * @param sceneId the new sceneId
     * @param view the view associated the the sceneId.
     * @return a {@link SceneCreator} for more configurations.
     */
    public SceneCreator add(int sceneId, View view) {
        if (view == null) {
            throw new NullPointerException("Invalid view scene. (view == null");
        }
        mScenes.add(new Pair<>(sceneId, view));
        return this;
    }

    /**
     * Create a new scene by providing a view id and sceneId.
     *
     * @param sceneId the new sceneId
     * @param idRes the view id associated the the sceneId.
     * @return a {@link SceneCreator} for more configurations.
     */
    public SceneCreator add(int sceneId, @IdRes int idRes) {
        return add(sceneId, mRootView.findViewById(idRes));
    }

    @NonNull
    Object getReference() {
        return mReference;
    }

    @Nullable
    AnimationAdapter getAdapter() {
        return mAdapter;
    }

    int getFirstSceneId() {
        return mFirstSceneId;
    }

    @NonNull
    List<Pair<Integer, View>> getScenes() {
        return mScenes;
    }

    @Nullable
    Listener getListener() {
        return mListener;
    }
}
