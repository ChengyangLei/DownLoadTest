package com.mr.smart;

import com.google.zxing.WriterException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

@WebServlet(name = "ZipDownLoadServlet", value = "/zipDownLoadServlet")
public class ZipDownLoadServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String realPath = request.getSession().getServletContext().getRealPath("/");
        //1 生成二维码
        String projectName = "农银公杂费";
        String path = realPath + projectName;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        List<String> list = Arrays.asList("code11.jpg", "code22.jpg", "code33.jpg");//已经存好的图片
        try {
            for (String x : list) {
                String source = "G://" + x;
                String qrCode = path + "/公杂费_" + x;
                String url = "https://www.baidu.com/?name=" + x;
                QRCodeUtil.generateQRImage(url, qrCode, source);//生成二维码的方法
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
        //2 生成zip文件
        ZipHelper.zipCompress(path, path + ".zip");
        //3 下载
        String zipFileName = path + ".zip";
        String filename = projectName + ".zip";
        //设置文件MIME类型
        response.setContentType(getServletContext().getMimeType(filename));

        response.setCharacterEncoding("UTF-8");
        //设置Content-Disposition
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "utf-8"));
        InputStream in = new FileInputStream(zipFileName);
        OutputStream out = response.getOutputStream();
        //写文件
        int b;
        while ((b = in.read()) != -1) {
            out.write(b);
        }
        out.flush();
        //4 删除多余文件
        deleteDir(new File(path));
        in.close();//先关闭输入流才能删除
        deleteDir(new File(zipFileName));
        out.close();

    }

    private boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String s : children) {
                boolean success = deleteDir(new File(dir, s));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
}
