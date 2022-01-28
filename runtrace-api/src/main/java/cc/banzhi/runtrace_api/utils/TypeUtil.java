package cc.banzhi.runtrace_api.utils;

/**
 * @program: ZRunTrace
 * @description: 类型处理工具，用于字节码中基本数据类型转Object
 * @author: zoufengli01
 * @create: 2022/1/25 10:16 下午
 **/
public class TypeUtil {

    public static Object toObj(Object data) {
        return data;
    }

    public static Object toObj(String data) {
        return data;
    }

    public static Object toObj(byte data) {
        return data + "";
    }

    public static Object toObj(short data) {
        return data + "";
    }

    public static Object toObj(int data) {
        return data + "";
    }

    public static Object toObj(long data) {
        return data + "";
    }

    public static Object toObj(float data) {
        return data + "";
    }

    public static Object toObj(double data) {
        return data + "";
    }

    public static Object toObj(char data) {
        return data + "";
    }

    public static Object toObj(boolean data) {
        return data + "";
    }
}
