package pl.com.turski.ah.service;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * User: Adam
 */
public interface FtpService {

    public void upload(List<BufferedImage> images);
}
