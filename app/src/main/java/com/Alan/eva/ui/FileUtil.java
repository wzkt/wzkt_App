//package com.Alan.eva.ui;
//
//import android.os.Environment;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStream;
//
//public class FileUtil {
//    public static File updateDir=null;
//    public static File updateFile=null;
//    /******保存升级apk的目录******/
//    public static final String wzkt="wzkt";
//    public static boolean isCreateFileSuccess;
//    public static void createFile(String app_name)
//    {
//        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
//            isCreateFileSuccess=true;
//
//            updateDir=new File(Environment.getExternalStorageDirectory()+"/"+wzkt+"/");
//            updateFile=new File(updateDir+"/"+app_name+".apk");
//
//            if(!updateDir.exists())
//            {
//                updateDir.mkdirs();
//            }
//            if(!updateFile.exists())
//            {
//                try{
//                    updateFile.createNewFile();
//                }catch (IOException e)
//                {
//                    isCreateFileSuccess=false;
//                    e.printStackTrace();
//                }
//            }
//        }else
//        {
//            isCreateFileSuccess=false;
//        }
//    }
//
//    String cachePath = (
//            getExternalFilesDir("upgrade_apk") +
//                    File.separator +
//                    getPackageName() +
//                    ".apk");
//
//
//    /**
//     * 保存文件
//     *
//     * @param in       文件输入流
//     * @param filePath 文件保存路径
//     */
//    public static File saveFile(InputStream in, String filePath) {
//        File file = new File(filePath);
//        byte[] buffer = new byte[4096];
//        int len = 0;
//        FileOutputStream fos = null;
//        try {
//            file.createNewFile();
//            fos = new FileOutputStream(file);
//            while ((len = in.read(buffer)) != -1) {
//                fos.write(buffer, 0, len);
//            }
//            fos.flush();
//        } catch (IOException e) {
//        } finally {
//            try {
//                if (in != null) in.close();
//                if (fos != null) fos.close();
//            } catch (IOException e) {
//            }
//        }
//        return file;
//    }
//
//    public static String  saveFile(String fileName) {
//        // 创建String对象保存文件名路径
//        try {
//            updateDir=new File(Environment.getExternalStorageDirectory()+"/"+wzkt+"/");
//            if(!updateDir.exists())
//            {
//                updateDir.mkdirs();
//            }
//            // 创建指定路径的文件
//            File file = new File(updateDir, fileName);
//            // 如果文件不存在
//            if (!file.exists()){
//                file.createNewFile();
//            }
//            return file.getAbsolutePath().toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
//
//    /**
//     * 追加文件：使用FileWriter
//     *
//     * @param fileName
//     * @param content
//     */
//    public static void method2(String fileName, String content) {
//        try {
//            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
//            FileWriter writer = new FileWriter(fileName, true);
//            writer.write(content);
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//}
