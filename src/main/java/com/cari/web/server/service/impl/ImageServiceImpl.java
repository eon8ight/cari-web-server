package com.cari.web.server.service.impl;

import java.awt.image.BufferedImage;
import com.cari.web.server.service.ImageService;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;

@Service
public class ImageServiceImpl implements ImageService {

    @Override
    public boolean isImageSquare(BufferedImage image) {
        return image.getWidth() == image.getHeight();
    }

    @Override
    public boolean isImageMinimumSize(BufferedImage image, int minPixelsSide) {
        return Math.min(image.getWidth(), image.getHeight()) >= minPixelsSide;
    }

    @Override
    public BufferedImage resizeImage(BufferedImage image, int maxPixelsSide)
            throws IllegalArgumentException {
        int maxSide = Math.max(image.getWidth(), image.getHeight());

        if (maxSide < maxPixelsSide) {
            throw new IllegalArgumentException("Image ");
        } else if (maxSide == maxPixelsSide) {
            return image;
        }

        return Scalr.resize(image, maxPixelsSide);
    }

    @Override
    public BufferedImage cropToSquare(BufferedImage image) {
        int minSide = Math.min(image.getWidth(), image.getHeight());
        return image.getSubimage(0, 0, minSide, minSide);
    }
}
