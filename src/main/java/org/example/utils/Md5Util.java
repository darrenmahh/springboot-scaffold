package org.example.utils; // 定义包路径

import java.security.MessageDigest; // 导入消息摘要类，用于加密
import java.security.NoSuchAlgorithmException; // 导入算法不存在异常类

public class Md5Util { // MD5工具类
    // 将密码转换成16进制表示的字符
    protected static char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'}; // 16进制字符数组

    protected static MessageDigest messageDigest = null; // 声明消息摘要对象

    static { // 静态代码块，类加载时执行
        try {
            messageDigest = MessageDigest.getInstance("MD5"); // 获取MD5算法的MessageDigest实例
        } catch (NoSuchAlgorithmException e) { // 捕获算法不存在异常
            System.err.println(Md5Util.class.getName() + "初始化失败，MessageDigest不支持MD5Util。"); // 打印错误信息
            throw new RuntimeException(e); // 抛出运行时异常
        }
    }

    public static boolean checkPassword(String password, String md5PwdStr) { // 检查密码方法
        String s = getMD5String(password); // 获取输入密码的MD5值
        return s.equals(md5PwdStr); // 比较计算出的MD5值与存储的MD5值是否相等
    }

    public static String getMD5String(String s) { // 获取字符串的MD5值
        return getMD5String(s.getBytes()); // 将字符串转换为字节数组并获取MD5
    }

    private static String getMD5String(byte[] bytes) { // 获取字节数组的MD5值
        messageDigest.update(bytes); // 更新摘要内容
        return bufferToHex(messageDigest.digest()); // 计算摘要并转换为16进制字符串
    }

    private static String bufferToHex(byte[] bytes) { // 字节数组转16进制字符串
        return bufferToHex(bytes, 0, bytes.length); // 调用重载方法转换整个数组
    }

    private static String bufferToHex(byte[] bytes, int m, int n) { // 字节数组指定部分转16进制字符串
        StringBuffer stringBuffer = new StringBuffer(2 * n); // 创建字符缓冲区，每个字节需要2个16进制字符
        int k = m + n; // 计算结束位置
        for (int l = m ; l < k ; l++) { // 遍历指定范围的字节
            appendHexPair(bytes[l], stringBuffer); // 将每个字节转换为16进制并添加到缓冲区
        }
        return stringBuffer.toString(); // 返回16进制字符串
    }

    private static void appendHexPair(byte bt, StringBuffer stringBuffer) { // 添加16进制对
        char c0 = hexDigits[(bt & 0xf0) >> 4]; // 取字节高4位作为第一个16进制字符索引
        char c1 = hexDigits[bt & 0xf]; // 取字节低4位作为第二个16进制字符索引
        stringBuffer.append(c0); // 添加高4位对应的16进制字符
        stringBuffer.append(c1); // 添加低4位对应的16进制字符
    }

}