package util;

import javafx.application.Platform;
import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

class EnumImageStorageTest extends ApplicationTest {

    @BeforeAll
    static void initToolkit() throws Exception {
        Platform.startup(() -> {});
    }

    @Test
    void testImageLoading() {
        EnumImageStorage<TestEnum> imageStorage = new EnumImageStorage<>(TestEnum.class);

        for (TestEnum constant : TestEnum.values()) {
            Image image = imageStorage.get(constant);
            if (image != null) {
                assertFalse(image.isError(), "Image should not have loading errors for constant: " + constant);
            }
        }
    }

    @Test
    void testImageLoadingFailure() {
        EnumImageStorage<TestEnum> imageStorage = new EnumImageStorage<>(TestEnum.class);

        for (TestEnum constant : TestEnum.values()) {
            Image image = imageStorage.get(constant);
            if (image == null) {
                assertNull(image, "Image should be null if path is incorrect for constant: " + constant);
            }
        }
    }
}
