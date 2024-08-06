package util;

import javafx.scene.image.Image;
import org.tinylog.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code EnumImageStorage} class provides a way to store and retrieve images associated with enum constants.
 * It implements the {@code ImageStorage} interface.
 *
 * @param <T> the type of enum constants used as keys for image storage
 */
public class EnumImageStorage<T extends Enum<T>> implements ImageStorage<T> {

    private final Map<T, Image> map = new HashMap<>();

    /**
     * Constructs an {@code EnumImageStorage} for the specified enum class.
     * It loads images for each enum constant from a specified path.
     *
     * @param enumClass the class of the enum for which images are to be loaded
     */
    public EnumImageStorage(Class<T> enumClass) {
        var path = "boardgame.game";
        for (var constant : enumClass.getEnumConstants()) {
            var url = String.format("/%s/%s.png", path, constant.name().toLowerCase());
            try {
                Image image = new Image(getClass().getResourceAsStream(url));
                if (image.isError()) {
                    Logger.error("Image loading error for " + constant + " from URL: " + url);
                } else {
                    map.put(constant, image);
                }
            } catch (Exception e) {
                Logger.error("Failed to load image for " + constant + " from URL: " + url, e);
            }
        }
    }

    /**
     * Retrieves the image associated with the specified enum constant.
     *
     * @param constant the enum constant for which the image is to be retrieved
     * @return the image associated with the specified enum constant, or {@code null} if no image is found
     */
    @Override
    public Image get(T constant) {
        return map.get(constant);
    }
}
