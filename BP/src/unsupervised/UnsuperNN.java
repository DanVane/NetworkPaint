/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unsupervised;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 *
 * @author wangdan
 */
public class UnsuperNN {

    private Images image;
    private BufferedImage sampleImage;
    private BufferedImage image0;
    private int imageSize;
    private int sampleSize;

    public static void main(String[] args) {
        UnsuperNN unn = new UnsuperNN();
        unn.InitImages();
        unn.update();

    }

    public void InitImages() {
        image = new Images();
        image0 = image.createWeight();
        sampleImage = image.setSamples();
        imageSize = image.getImageSize();
        sampleSize = image.getSampleSize();
        System.out.println("准备完毕");
    }

    //更新
    public void update() {
        System.out.println("开始：");
        int nearestX, nearestY;
        Point a = new Point();
        int rgb;
        for (int i = 0; i < sampleSize; i++) {
            for (int j = 0; j < sampleSize; j++) {
                rgb = sampleImage.getRGB(i, j);
                //寻找image0中的最近点
                a = findNearestPoint(rgb);
                nearestX = a.x;
                nearestY = a.y;
                //处理像素
                updateColor(i, j, nearestX, nearestY, rgb);
            }
        }
    }

    public Point findNearestPoint(int rgb) {
        System.out.println("开始查找最近点");
        Point a = new Point();
        int R, G, B, R1, G1, B1, absR, absG, absB, distance;
        int D = Integer.MAX_VALUE;
        R = (rgb & 0xff0000) >> 16;
        G = (rgb & 0xff00) >> 8;
        B = (rgb & 0xff);
        for (int i = 0; i < imageSize; i++) {
            for (int j = 0; j < imageSize; j++) {
                rgb = image0.getRGB(i, j);
                //颜色相似度计算找到最相似的点
                R1 = (rgb & 0xff0000) >> 16;
                G1 = (rgb & 0xff00) >> 8;
                B1 = (rgb & 0xff);
                absR = R - R1;
                absG = G - G1;
                absB = B - B1;
                distance = (int) Math.sqrt(absR * absR + absG * absG + absB * absB);
                if (distance < D) {
                    D = distance;
                    a.x = i;
                    a.y = j;
                }
            }
        }
        return a;
    }

    public void updateColor(int a, int b, int x, int y, int rgb) {
        System.out.println("开始更新像素点");
        File file = new File(UnsuperNN.class.getResource("/").getFile().toString() + "\\result\\Data" + a + b + ".jpg");
        int R, G, B, nearestX, nearestY, newR, newG, newB, R1, G1, B1;
        int rgb0;
        double rate;
        R = (rgb & 0xff0000) >> 16;
        G = (rgb & 0xff00) >> 8;
        B = (rgb & 0xff);
        nearestX = x;
        nearestY = y;
        for (int i = 0; i < imageSize; i++) {
            for (int j = 0; j < imageSize; j++) {
                //判断是否是邻居点
//                if (Math.abs(i - nearestX) + Math.abs(j - nearestY) > 80) {
//                    continue;
//                }
                if ((i - nearestX) * (i - nearestX) + (j - nearestY) * (j - nearestY) > 20000) {
                    continue;
                }
                rate = setRate(i, j, nearestX, nearestY);
                //找到邻居点ij，更新颜色
                rgb0 = image0.getRGB(i, j);
                R1 = (rgb0 & 0xff0000) >> 16;
                G1 = (rgb0 & 0xff00) >> 8;
                B1 = (rgb0 & 0xff);
                newR = (int) (R1 + (R - R1) * rate) % 255;
                newG = (int) (G1 + (G - G1) * rate) % 255;
                newB = (int) (B1 + (B - B1) * rate) % 255;
                rgb0 = ((newR * 256) + newG) * 256 + newB;
                image0.setRGB(i, j, rgb0);
            }
        }
        try {
            ImageIO.write(image0, "jpg", file);
            System.out.println("新图更新完毕");
        } catch (Exception e) {

        }

    }

    public double setRate(double x, double y, double u1, double u2) {
        double res = 0.0;
        res = 1 - (x + y) / (u1 + u2);
        return res;
    }

    class Images {

        private int red;
        private int blue;
        private int green;
        private int imageSize = 200;
        private int sampleSize = 10;
        BufferedImage image;
        private int rgb;
        Random a = new Random();

        //随机生成权重
        public BufferedImage createWeight() {
            Random a = new Random();
            File file = new File(UnsuperNN.class.getResource("/").getFile().toString() + "\\result\\image0.jpg");
            image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_3BYTE_BGR);
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    red = a.nextInt(255);
                    blue = a.nextInt(255);
                    green = a.nextInt(255);
                    rgb = ((red * 256) + green) * 256 + blue;
                    image.setRGB(i, j, rgb);
                }
            }
            try {
                ImageIO.write(image, "jpg", file);
                System.out.println("权重图片已生成");
            } catch (Exception e) {

            }
            return image;
        }

        //设置样本点
        public BufferedImage setSamples() {
            Color[] colors = new Color[10];
            colors[0] = Color.WHITE;
            colors[1] = Color.BLACK;
            colors[2] = Color.BLUE;
            colors[3] = Color.MAGENTA;
            colors[4] = Color.PINK;
            colors[5] = Color.ORANGE;
            colors[6] = Color.YELLOW;
            colors[7] = Color.PINK;
            colors[8] = Color.white;
            colors[9] = Color.RED;
            File file = new File(UnsuperNN.class.getResource("/").getFile().toString() + "\\result\\Data.jpg");
            image = new BufferedImage(sampleSize, sampleSize, BufferedImage.TYPE_3BYTE_BGR);
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    int randomc = a.nextInt(10);
                    red = (int) colors[randomc].getRed();
                    blue = (int) colors[randomc].getGreen();
                    green = (int) colors[randomc].getBlue();
                    rgb = ((red * 256) + green) * 256 + blue;
                    image.setRGB(i, j, rgb);
                }
            }
            try {
                ImageIO.write(image, "jpg", file);
                System.out.println("样本图片已生成");
            } catch (Exception e) {

            }
            return image;
        }

        public int getImageSize() {
            return imageSize;
        }

        public int getSampleSize() {
            return sampleSize;
        }
    }

}
