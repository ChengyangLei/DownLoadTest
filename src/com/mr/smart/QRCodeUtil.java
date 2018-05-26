/*
 * @(#)EncodeTest.java      1.0   2018年3月13日
 *
 * Copyright (c) 2009 fingard System Engineering Co., Ltd.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * fingard System Engineering Co., Ltd. ("Confidential Information").  
 * You shall not disclose such Confidential Information and shall use it 
 * only in accordance with the terms of the license agreement you entered 
 * into with fingard.
 */

package com.mr.smart;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

/**
 * @author raozj
 * @version 1.0 2018年3月13日
 */
public class QRCodeUtil {


    private static final int BLACK = 0xFF000000;// 用于设置图案的颜色

    private static final int WHITE = 0xFFFFFFFF; // 用于背景色

    public static void main(String[] args) throws Exception {
        String content = "http://10.60.45.76:8080/admin/html/payformtemplate/view/userForm.jsp?templateId=8abcadcc63953f0a016395495889005c&organizationId=8abcac416361357a0163615851310068";
        String outPutFile = "G://测试.png";
        String logoFile = "G://logo.jpg";
        boolean result = generateQRImage(content, outPutFile, logoFile);
        if (result) {
            System.out.println("生成二维码成功！");
        }
    }

    /**
     * 生成二维码
     *
     * @param content    二维码内容
     * @param outPutFile 二维码输出文件
     * @param logoFile   logo文件
     * @return
     * @throws IOException
     * @throws WriterException
     * @author raozj  v1.0   2018年3月13日
     */
    public static boolean generateQRImage(String content, String outPutFile, String logoFile) throws IOException, WriterException {
        int width = 300; // 二维码图片宽度 300
        int height = 300; // 二维码图片高度300

        String format = "png";// 二维码的图片格式 png

        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();

        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);// 指定纠错等级,纠错级别（L 7%、M 15%、Q 25%、H 30%）
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");// 内容所使用字符集编码
        hints.put(EncodeHintType.MARGIN, 1);// 设置二维码边的空度，非负数

        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

        String path = outPutFile.substring(0, outPutFile.lastIndexOf("/"));
        File pathFile = new File(path);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }

        // 生成二维码
        File outputFile = new File(outPutFile);// 指定输出路径
        BufferedImage image = toBufferedImage(bitMatrix);

        if (logoFile != null) {
            logoMatrix(image, logoFile);//设置logo
        }

        if (!ImageIO.write(image, format, outputFile)) {
            return false;
        } else {
            return true;
        }
    }

    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x >= width / 5 * 2 && x <= width / 5 * 3) {
                    if (y >= height / 5 * 2 && y <= height / 5 * 3) {
                        image.setRGB(x, y, WHITE);
                        continue;
                    }
                }
                image.setRGB(x, y, (matrix.get(x, y) ? BLACK : WHITE));
                // image.setRGB(x, y, (matrix.get(x, y) ? Color.YELLOW.getRGB()
                // : Color.CYAN.getRGB()));
            }
        }
        return image;
    }

    /**
     * 设置 logo
     *
     * @param matrixImage 源二维码图片
     * @return 返回带有logo的二维码图片
     * @throws IOException
     * @author Administrator sangwenhao
     */
    public static void logoMatrix(BufferedImage matrixImage, String logoFile) throws IOException {
        /**
         * 读取二维码图片，并构建绘图对象
         */
        Graphics2D g2 = matrixImage.createGraphics();

        int matrixWidth = matrixImage.getWidth();
        int matrixHeigh = matrixImage.getHeight();

        /**
         * 读取Logo图片
         */
        BufferedImage logo = ImageIO.read(new File(logoFile));

        // 开始绘制图片
        g2.drawImage(logo, matrixWidth / 5 * 2, matrixHeigh / 5 * 2, matrixWidth / 5, matrixHeigh / 5, null);// 绘制
        BasicStroke stroke = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke);// 设置笔画对象
        // 指定弧度的圆角矩形
        RoundRectangle2D.Float round = new RoundRectangle2D.Float(matrixWidth / 5 * 2, matrixHeigh / 5 * 2,
                matrixWidth / 5, matrixHeigh / 5, 20, 20);
        g2.setColor(Color.white);
        g2.draw(round);// 绘制圆弧矩形

        // 设置logo 有一道灰色边框
        BasicStroke stroke2 = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke2);// 设置笔画对象
        RoundRectangle2D.Float round2 = new RoundRectangle2D.Float(matrixWidth / 5 * 2 + 2, matrixHeigh / 5 * 2 + 2,
                matrixWidth / 5 - 4, matrixHeigh / 5 - 4, 20, 20);
        g2.setColor(new Color(128, 128, 128));
        g2.draw(round2);// 绘制圆弧矩形

        g2.dispose();
        matrixImage.flush();
    }

}
