package com.cari.web.server.util;

import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.function.Function;

public interface ImageValidator extends Function<BufferedImage, Optional<String>> {
}
