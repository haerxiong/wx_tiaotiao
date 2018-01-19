package cn.lw;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
//        System.out.println(sameColor(new int[]{170,180,234}, new int[]{206,209,250}, 40));
    }

//    public static int[] rgb_shadow = {178,149,101};
//    public static int[] rgb_self = {55,59,100};
    public static int[] rgb_self = {54,60,102};
    public static int[] rgb_self2 = {88,80,128};
    public static int[] point_start = null;

    public static Map<String, int[]> bs = new HashMap();

    public static void getBot(BufferedImage image, int[] top, int[] rgb) throws Exception {
        int[] t = getRGB(image, top[0], top[1]);
        if(!bs.containsKey(top[0]+","+top[1]) && !sameColor(t, rgb)) {
            bs.put(top[0]+","+top[1], top);
            getBot(image, new int[]{top[0], top[1]+1}, rgb);
            getBot(image, new int[]{top[0]-1, top[1]+1}, rgb);
            getBot(image, new int[]{top[0]+1, top[1]+1}, rgb);
        }
    }

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
                if(rgb_self[0] == t[0]
                        && rgb_self[1] == t[1]
                        && rgb_self[2] == t[2]) { // 找到小人的位置
                    point_start = new int[]{i, j};
                    break;
                }
            }
        }
        for (int j = 200; j < image.getHeight(); j++) {
            for (int i = 10; i < image.getWidth(); i++) {
                int[] t = getRGB(image, i, j);
                int[] bg = getBgRGB(image);
                if(target == null && !sameColor(bg, t)
                    && !sameColor(t, rgb_self)
                    && !sameColor(t, rgb_self2)
                    && Math.abs(point_start[0] - i) > 25
                    ) {// 从上找到target最上方点
                    target = getRGB(image, i+2, j+2);
                    tarP = new int[]{i + 2, j + 2};
                    outAry(tarP);
                    break;
                }
            }
        }
        int[] tarPL = null;
        int[] tarPR = null;
        for (int i = 10; i < image.getWidth(); i++) {
            for (int j = 200; j < image.getHeight(); j++) {
                int[] t = getRGB(image, i, j);
                if(tarPL == null && target[0] == t[0]
                        && target[1] == t[1]
                        && target[2] == t[2]
                        && j < point_start[1]
                        && distance(tarP, new int[]{i, j}) < 100
                        && Math.abs(point_start[0] - i) > 25
                        ) {
                    tarPL = new int[]{i, j};
                }
                if(target[0] == t[0]
                        && target[1] == t[1]
                        && target[2] == t[2]
                        && j < point_start[1]
                        && distance(tarP, new int[]{i, j}) < 100
                        && Math.abs(point_start[0] - i) > 25
                        ) {
                    tarPR = new int[]{i, j};
                    if(tarPR[0]-tarPL[0]<60) {
                        tarPR[1] = tarPL[1];
                    }
                }
            }
        }

        int[] tar = new int[]{tarPL[0]/2+tarPR[0]/2, tarPL[1]/2+tarPR[1]/2};
//        long t = (long) (distance(point_start, tarP)*6.75/2.5);
        long t = (long) (distance(point_start, tar)*3);

//        robot.mouseMove(point_start[0], point_start[1]);
        robot.mouseMove(tarPL[0], tarPL[1]);
        Thread.sleep(500);
//        robot.mouseMove(tar[0], tar[1]);
        robot.mouseMove(tarPR[0], tarPR[1]);
        Thread.sleep(500);
        System.out.println(t);
        push(t);
        Thread.sleep(2000);
    }

    private static int[] getBgRGB(BufferedImage image) {
        return getRGB(image, 10, 200);
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
