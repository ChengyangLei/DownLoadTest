package com.mr.smart;

import java.io.*;
import java.util.zip.*;

/**
 * ѹ��������
 *
 * @author Administrator
 */
public class ZipHelper {
    private static final int BUFFER_SIZE = 1024;
    private static final String PATH_SEP = "/";

    /**
     * @param sourceFileName
     * @param destFileName
     * @throws IOException
     */
    public static void zipCompress(String sourceFileName, String destFileName) throws IOException {
        File sourceFile = new File(sourceFileName);
        File destFile = new File(destFileName);
        zipCompress(sourceFile, destFile);
    }

    /**
     * @param sourceFile
     * @param destFile
     * @throws IOException
     */
    public static void zipCompress(File sourceFile, File destFile) throws IOException {
        FileOutputStream fos = null;
        CheckedOutputStream cos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(destFile);
            cos = new CheckedOutputStream(fos, new CRC32());
            zos = new ZipOutputStream(cos);
            zipCompress(sourceFile, zos, "");
            zos.flush();
            zos.finish();
        } finally {
            //�����ڲ������ cos, fos �� close ����
            if (zos != null)
                zos.close();
            if (cos != null)
                cos.close();
            if (fos != null)
                fos.close();
        }
    }

    /**
     * @param sourceFile
     * @param zos
     * @param basePath
     * @throws IOException
     */
    private static void zipCompress(File sourceFile, ZipOutputStream zos, String basePath) throws IOException {
        if (sourceFile.isDirectory())
            zipCompressDir(sourceFile, zos, basePath);
        else
            zipCompressFile(sourceFile, zos, basePath);
    }

    /**
     * ���ļ��н���ѹ�����������
     *
     * @param dir      �ļ���
     * @param zos      �����
     * @param basePath ѹ���ļ��е����·��
     * @throws IOException
     */
    private static void zipCompressDir(File dir, ZipOutputStream zos, String basePath) throws IOException {
        File[] files = dir.listFiles();
        String newBasePath = basePath + dir.getName() + PATH_SEP;
        //������Ŀ¼
        if (files.length <= 0) {
            ZipEntry entry = new ZipEntry(newBasePath);
            zos.putNextEntry(entry);
            zos.closeEntry();
        }
        //�ݹ�ѹ��
        for (File file : files) {
            zipCompress(file, zos, newBasePath);
        }
    }

    /**
     * ���ļ�����ѹ�����������
     *
     * @param file     ��ѹ���ļ�
     * @param zos      �����
     * @param basePath ѹ���ļ��е����·��
     * @throws IOException
     */
    private static void zipCompressFile(File file, ZipOutputStream zos, String basePath) throws IOException {
        String entryName = basePath + file.getName();
        FileInputStream fis = new FileInputStream(file);
        zipCompress(fis, zos, entryName);
    }

    public static byte[] zipCompress(InputStream is, String entryName) throws IOException {
        ByteArrayOutputStream baos = null;
        CheckedOutputStream cos = null;
        ZipOutputStream zos = null;
        try {
            baos = new ByteArrayOutputStream();
            cos = new CheckedOutputStream(baos, new CRC32());
            zos = new ZipOutputStream(cos);

            zos.setMethod(ZipOutputStream.DEFLATED);
            zipCompress(is, zos, entryName);
            zos.flush();
            zos.finish();
            byte[] result = baos.toByteArray();
            return result;
        } finally {
            if (zos != null)
                zos.close();
            if (cos != null)
                cos.close();
            if (baos != null)
                baos.close();
        }
    }

    /**
     * �������������ѹ�����������
     *
     * @param is        ��ѹ������
     * @param zos       ѹ����������
     * @param entryName ԭʼ�ļ���
     * @throws IOException
     */
    private static void zipCompress(InputStream is, ZipOutputStream zos, String entryName) throws IOException {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(is);
            ZipEntry entry = new ZipEntry(entryName);
            zos.putNextEntry(entry);
            int count;
            byte[] buffer = new byte[BUFFER_SIZE];
            while (true) {
                count = bis.read(buffer, 0, BUFFER_SIZE);
                if (count < 0)
                    break;
                zos.write(buffer, 0, count);
            }
            zos.closeEntry();
        } finally {
            if (bis != null)
                bis.close();
        }
    }

    /**
     * ��ѹ���ļ���ѹ����Ŀ���ļ���
     *
     * @param sourceFileName
     * @param destFileName
     * @throws IOException
     */
    public static void zipDecompress(String sourceFileName, String destDir) throws IOException {
        zipDecompress(new File(sourceFileName), new File(destDir));
    }

    /**
     * ��ѹ���ļ���ѹ����Ŀ���ļ���
     *
     * @param sourceFile
     * @param destFile
     * @throws IOException
     */
    public static void zipDecompress(File sourceFile, File destDir) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(sourceFile);
            zipDecompress(fis, destDir);
        } finally {
            if (fis != null)
                fis.close();
        }
    }

    /**
     * ������е����ݽ�ѹ����Ŀ���ļ���
     *
     * @param sourceBytes
     * @param destDir
     * @throws IOException
     */
    public static void zipDecompress(byte[] sourceBytes, File destDir) throws IOException {
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(sourceBytes);
            zipDecompress(bais, destDir);
        } finally {
            if (bais != null)
                bais.close();
        }
    }

    /**
     * �����е����ݽ�ѹ����Ŀ���ļ���
     *
     * @param is
     * @param destFile
     * @throws IOException
     */
    public static void zipDecompress(InputStream is, File destFile) throws IOException {
        CheckedInputStream cis = null;
        ZipInputStream zis = null;
        try {
            cis = new CheckedInputStream(is, new CRC32());
            zis = new ZipInputStream(cis);
            zipDecompress(zis, destFile);
        } finally {
            if (zis != null)
                zis.close();
            if (cis != null)
                cis.close();
        }
    }

    /**
     * ʹ�� ZipInputStream ���н�ѹ����������ѹ����Ŀ���ļ���
     *
     * @param zis
     * @param destFile
     * @throws IOException
     */
    private static void zipDecompress(ZipInputStream zis, File destFile) throws IOException {
        while (true) {
            ZipEntry entry = zis.getNextEntry();
            if (entry == null)
                break;
            String dir = destFile.getPath() + File.separator + entry.getName();
            File dirFile = new File(dir);
            IOHelper.ensureFolderExists(dirFile);

            //���ǰ Entry ��Ŀ¼�����ļ����򴴽�Ŀ¼��
            if (entry.isDirectory()) {
                dirFile.mkdir();
            }
            //���ǰ Entry ���ļ����򽫽�ѹ����������ļ���
            else {
                zipDecompressFile(zis, dirFile);
            }
            zis.closeEntry();
        }
    }

    /**
     * ��ѹ�����е�ǰ Entry �����ݽ�ѹ����ָ���ļ���
     * ������Ҫȷ����ǰ�� Entry ���ļ�����Ŀ¼��
     *
     * @param zis
     * @param destFile
     * @throws IOException
     */
    private static void zipDecompressFile(ZipInputStream zis, File destFile) throws IOException {
        BufferedOutputStream bos = null;
        int count;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(destFile));
            byte[] buffer = new byte[BUFFER_SIZE];
            while (true) {
                count = zis.read(buffer, 0, BUFFER_SIZE);
                if (count < 0)
                    break;
                bos.write(buffer, 0, count);
            }
        } finally {
            if (bos != null)
                bos.close();
        }
    }

    /**
     * ��ѹ���ļ���ѹ��Ϊ����ָ���ļ�����ļ�����ʹ���ļ���ԭʼ�ļ���
     * ���ַ�ʽѹ���ļ��б������ֻ�ܰ� 1 ���ļ���
     *
     * @param sourceFileName
     * @param destFileName
     * @throws IOException
     */
    public static void zipDecompressAsSingleFile(String sourceFileName, String destFileName) throws IOException {
        CheckedInputStream cis = null;
        ZipInputStream zis = null;
        try {
            cis = new CheckedInputStream(new FileInputStream(new File(sourceFileName)), new CRC32());
            zis = new ZipInputStream(cis);
            //�����п��ܻ�û�� Entry?
            zis.getNextEntry();
            zipDecompressFile(zis, new File(destFileName));
        } finally {
            if (zis != null)
                zis.close();
            if (cis != null)
                cis.close();
        }
    }
}











