package com.cari.web.server.service;

import java.awt.image.BufferedImage;

public interface ImageService {

    boolean isImageSquare(BufferedImage image);

    boolean isImageMinimumSize(BufferedImage image, int minPixelsSide);

    BufferedImage resizeImage(BufferedImage image, int maxPixelsSide)
            throws IllegalArgumentException;

    BufferedImage cropToSquare(BufferedImage image);
}
