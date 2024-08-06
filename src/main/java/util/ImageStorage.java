package util;

import javafx.scene.image.Image;

/**
 * The {@code ImageStorage} interface defines a contract for classes that store and retrieve images based on a key.
 *
 * @param <T> the type of the key used to retrieve images
 */
public interface ImageStorage<T> {

    /**
     * Retrieves the image associated with the specified key.
     *
     * @param key the key used to retrieve the image
     * @return the image associated with the specified key, or {@code null} if no image is found
     */
    Image get(T key);

}
