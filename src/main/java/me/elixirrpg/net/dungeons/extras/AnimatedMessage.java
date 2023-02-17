// 
// Decompiled by Procyon v0.5.36
// 

package me.elixirrpg.net.dungeons.extras;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnimatedMessage {
    private ImageMessage[] images;

    private int index = 0;

    public AnimatedMessage(ImageMessage... VarArgs) {
        this.images = VarArgs;
    }

    public AnimatedMessage(File file, int Int, char Char) {
        List<BufferedImage> localList = getFrames(file);
        this.images = new ImageMessage[localList.size()];
        for (int i = 0; i < localList.size(); i++)
            this.images[i] = new ImageMessage(localList.get(i), Int, Char);
    }

    public List<BufferedImage> getFrames(File file) {
        ArrayList<BufferedImage> localArrayList = new ArrayList();
        try {
            ImageReader localImageReader = ImageIO.getImageReadersBySuffix("GIF").next();
            ImageInputStream localImageInputStream = ImageIO.createImageInputStream(file);
            localImageReader.setInput(localImageInputStream);
            int i = localImageReader.getNumImages(true);
            for (int j = 0; j < i; j++) {
                BufferedImage localBufferedImage = localImageReader.read(j);
                localArrayList.add(localBufferedImage);
            }
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
        return localArrayList;
    }

    public ImageMessage current() {
        return this.images[this.index];
    }

    public ImageMessage next() {
        this.index++;
        if (this.index >= this.images.length) {
            this.index = 0;
            return this.images[this.index];
        }
        return this.images[this.index];
    }

    public ImageMessage previous() {
        this.index--;
        if (this.index <= 0) {
            this.index = this.images.length - 1;
            return this.images[this.index];
        }
        return this.images[this.index];
    }

    public ImageMessage getIndex(int Int) {
        return this.images[Int];
    }
}
