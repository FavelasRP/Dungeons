// 
// Decompiled by Procyon v0.5.36
// 

package me.elixirrpg.net.dungeons.extras;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class ImageMessage
{
    private final Color[] colors;
    private String[] lines;
    
    public ImageMessage(final BufferedImage Image, final int Int, final char Char) {
        this.colors = new Color[] { new Color(0, 0, 0), new Color(0, 0, 170), new Color(0, 170, 0), new Color(0, 170, 170), new Color(170, 0, 0), new Color(170, 0, 170), new Color(255, 170, 0), new Color(170, 170, 170), new Color(85, 85, 85), new Color(85, 85, 255), new Color(85, 255, 85), new Color(85, 255, 255), new Color(255, 85, 85), new Color(255, 85, 255), new Color(255, 255, 85), new Color(255, 255, 255) };
        final ChatColor[][] arrayOfChatColor = this.toChatColorArray(Image, Int);
        this.lines = this.toImgMessage(arrayOfChatColor, Char);
    }
    
    public ImageMessage(final ChatColor[][] ArrayOfChatColor, final char Char) {
        this.colors = new Color[] { new Color(0, 0, 0), new Color(0, 0, 170), new Color(0, 170, 0), new Color(0, 170, 170), new Color(170, 0, 0), new Color(170, 0, 170), new Color(255, 170, 0), new Color(170, 170, 170), new Color(85, 85, 85), new Color(85, 85, 255), new Color(85, 255, 85), new Color(85, 255, 255), new Color(255, 85, 85), new Color(255, 85, 255), new Color(255, 255, 85), new Color(255, 255, 255) };
        this.lines = this.toImgMessage(ArrayOfChatColor, Char);
    }
    
    public ImageMessage(final String... VarArgs) {
        this.colors = new Color[] { new Color(0, 0, 0), new Color(0, 0, 170), new Color(0, 170, 0), new Color(0, 170, 170), new Color(170, 0, 0), new Color(170, 0, 170), new Color(255, 170, 0), new Color(170, 170, 170), new Color(85, 85, 85), new Color(85, 85, 255), new Color(85, 255, 85), new Color(85, 255, 255), new Color(255, 85, 85), new Color(255, 85, 255), new Color(255, 255, 85), new Color(255, 255, 255) };
        this.lines = VarArgs;
    }
    
    public ImageMessage appendText(final String... VarArgs) {
        for (int i = 0; i < this.lines.length; ++i) {
            if (VarArgs.length > i) {
                final String[] arrayOfString = this.lines;
                final int j = i;
                arrayOfString[j] = String.valueOf(String.valueOf(arrayOfString[j])) + " " + VarArgs[i];
            }
        }
        return this;
    }
    
    public ImageMessage appendCenteredText(final String... VarArgs) {
        for (int i = 0; i < this.lines.length; ++i) {
            if (VarArgs.length <= i) {
                return this;
            }
            final int j = 65 - this.lines[i].length();
            this.lines[i] = String.valueOf(String.valueOf(this.lines[i])) + this.center(VarArgs[i], j);
        }
        return this;
    }
    
    private ChatColor[][] toChatColorArray(final BufferedImage Image, final int Int) {
        final double d = Image.getHeight() / Image.getWidth();
        int i = (int)(Int / d);
        if (i > 10) {
            i = 10;
        }
        final BufferedImage localBufferedImage = this.resizeImage(Image, (int)(Int / d), Int);
        final ChatColor[][] arrayOfChatColor = new ChatColor[localBufferedImage.getWidth()][localBufferedImage.getHeight()];
        for (int j = 0; j < localBufferedImage.getWidth(); ++j) {
            for (int k = 0; k < localBufferedImage.getHeight(); ++k) {
                final int m = localBufferedImage.getRGB(j, k);
                final ChatColor localChatColor = arrayOfChatColor[j][k] = this.getClosestChatColor(new Color(m, true));
            }
        }
        return arrayOfChatColor;
    }
    
    private String[] toImgMessage(final ChatColor[][] ArrayOfChatColor, final char Char) {
        final String[] arrayOfString = new String[ArrayOfChatColor[0].length];
        for (int i = 0; i < ArrayOfChatColor[0].length; ++i) {
            String str = "";
            for (int j = 0; j < ArrayOfChatColor.length; ++j) {
                final ChatColor localChatColor;
                str = String.valueOf(String.valueOf(str)) + (((localChatColor = ArrayOfChatColor[j][i]) != null) ? (String.valueOf(ArrayOfChatColor[j][i].toString()) + Char) : Character.valueOf(' '));
            }
            arrayOfString[i] = String.valueOf(String.valueOf(str)) + ChatColor.RESET;
        }
        return arrayOfString;
    }
    
    private BufferedImage resizeImage(final BufferedImage Image, final int Int1, final int Int2) {
        final AffineTransform localAffineTransform = new AffineTransform();
        localAffineTransform.scale(Int1 / Image.getWidth(), Int2 / Image.getHeight());
        final AffineTransformOp localAffineTransformOp = new AffineTransformOp(localAffineTransform, 1);
        return localAffineTransformOp.filter(Image, null);
    }
    
    private double getDistance(final Color Color1, final Color Color2) {
        final double d1 = (Color1.getRed() + Color2.getRed()) / 2.0;
        final double d2 = Color1.getRed() - Color2.getRed();
        final double d3 = Color1.getGreen() - Color2.getGreen();
        final int i = Color1.getBlue() - Color2.getBlue();
        final double d4 = 2.0 + d1 / 256.0;
        final double d5 = 4.0;
        final double d6 = 2.0 + (255.0 - d1) / 256.0;
        return d4 * d2 * d2 + d5 * d3 * d3 + d6 * i * i;
    }
    
    private boolean areIdentical(final Color Color1, final Color Color2) {
        return Math.abs(Color1.getRed() - Color2.getRed()) <= 5 && Math.abs(Color1.getGreen() - Color2.getGreen()) <= 5 && Math.abs(Color1.getBlue() - Color2.getBlue()) <= 5;
    }
    
    private ChatColor getClosestChatColor(final Color Color) {
        if (Color.getAlpha() < 128) {
            return null;
        }
        int j = 0;
        double d1 = -1.0;
        for (int i = 0; i < this.colors.length; ++i) {
            if (this.areIdentical(this.colors[i], Color)) {
                return ChatColor.values()[i];
            }
        }
        for (int i = 0; i < this.colors.length; ++i) {
            final double d2 = this.getDistance(Color, this.colors[i]);
            if (d2 < d1 || d1 == -1.0) {
                d1 = d2;
                j = i;
            }
        }
        return ChatColor.values()[j];
    }
    
    private String center(final String String, final int Int) {
        if (String.length() > Int) {
            return String.substring(0, Int);
        }
        if (String.length() == Int) {
            return String;
        }
        final int i = (Int - String.length()) / 2;
        final StringBuilder localStringBuilder = new StringBuilder();
        for (int j = 0; j < i; ++j) {
            localStringBuilder.append(" ");
        }
        return String.valueOf(String.valueOf(localStringBuilder.toString())) + String;
    }
    
    public String[] getLines() {
        return this.lines;
    }
    
    public void sendToPlayer(final Player Player) {
        String[] arrayOfString;
        for (int j = (arrayOfString = this.lines).length, i = 0; i < j; ++i) {
            final String str = arrayOfString[i];
            Player.sendMessage(str);
        }
    }
}
