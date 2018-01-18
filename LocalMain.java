package cn.lw;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by sunhua on 2018/1/18.
 */
public class LocalMain {

    private static Robot robot;

    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        int t = 0;
        while(true) {
            t++;
            System.out.println(t+"------------------------------");
            captureScreen("C:\\Users\\sunhua\\Desktop\\books","11.png");
        }
//        System.out.println(sameColor(new int[]{100,149,105}, new int[]{201,201,201}));
    }

//    public static int[] rgb_shadow = {178,149,101};
//    public static int[] rgb_self = {55,59,100};
    public static int[] rgb_self = {54,60,102};
    public static int[] rgb_self2 = {88,80,128};
    public static int[] point_start = null;

    public static void captureScreen(String fileName, String folder) throws Exception {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenRectangle = new Rectangle(screenSize);
        Robot robot = new Robot();
        screenRectangle.setBounds(0, 0, 450, 800);
        BufferedImage image = robot.createScreenCapture(screenRectangle);
//        saveAndOpen(fileName, folder, image);

        int[] target = null;
        int[] tarP = null;
        for (int j = 200; j < image.getHeight(); j++) {
            for (int i = 10; i < image.getWidth(); i++) {
                int[] t = getRGB(image, i, j);
                int[] bg = getRGB(image, 10, 200);
                if(target == null && !sameColor(bg, t)
                    && !sameColor(t, rgb_self)
                    && !sameColor(t, rgb_self2)) {// 从上找到target最上方点
                    target = t;
                    tarP = new int[]{i, j};
                }
                if(rgb_self[0] == t[0]
                    && rgb_self[1] == t[1]
                    && rgb_self[2] == t[2]) { // 找到小人的位置
                    point_start = new int[]{i, j};
                }
                /*if(sameColor(t, rgb_self) && t[1]<80) {// 找到小人位置
                    point_start = new int[]{i, j};
                }*/
            }
        }
        long t = (long) (distance(point_start, tarP)*6.75/2.5);

        robot.mouseMove(point_start[0], point_start[1]);
        Thread.sleep(500);
        robot.mouseMove(tarP[0], tarP[1]);
        Thread.sleep(500);
        System.out.println(t);
        push(t);
        Thread.sleep(2000);
    }

    private static void outAry(int[] target) {
        for (int a:target) {
            System.out.print(a+",");
        }
        System.out.println("");
    }

    public static int[] getRGB(BufferedImage image, int x, int y){
        int pixel = image.getRGB(x, y); // 下面三行代码将一个数字转换为RGB数字
        int[] rgb = new int[3];
        rgb[0] = (pixel & 0xff0000) >> 16;
        rgb[1] = (pixel & 0xff00) >> 8;
        rgb[2] = (pixel & 0xff);
        return rgb;
    }

    public static boolean sameColor(int[] a, int[] b) {
        return sameColor(a, b, 20);
    }

    public static boolean sameColor(int[] a, int[] b, int range) {
        int absR=a[0]-b[0];
        int absG=a[1]-b[1];
        int absB=a[2]-b[2];
        if(Math.sqrt(absR*absR+absG*absG+absB*absB)<range)
            return true;
        return false;
    }

    public static double distance(int[] a, int[] b) {
        int absR=a[0]-b[0];
        int absG=a[1]-b[1];
        return Math.sqrt(absR*absR+absG*absG);
    }

    private static void saveAndOpen(String fileName, String folder, BufferedImage image) throws IOException {
        //保存路径
        File screenFile = new File(fileName);
        if (!screenFile.exists()) {
            screenFile.mkdir();
        }
        File f = new File(screenFile, folder);

        ImageIO.write(image, "png", f);
        //自动打开
        if (Desktop.isDesktopSupported()
                && Desktop.getDesktop().isSupported(Desktop.Action.OPEN))
            Desktop.getDesktop().open(f);
    }

    private static void push(long time) throws InterruptedException {
        robot.mousePress(InputEvent.BUTTON1_MASK);
        Thread.sleep(time);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }
}
