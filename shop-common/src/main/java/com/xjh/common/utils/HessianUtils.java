package com.xjh.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

public class HessianUtils {
    public static byte[] serialize(Serializable obj) {
        HessianOutput hessianOutput = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
            hessianOutput = new HessianOutput(outputStream);
            hessianOutput.writeObject(obj);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(hessianOutput);
        }
        return null;
    }

    public static <T> T deserialize(byte[] bytes) {
        HessianInput hessianInput = null;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);) {
            hessianInput = new HessianInput(inputStream);
            return (T) hessianInput.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(hessianInput);
        }
        return null;
    }

    private static void close(HessianOutput output) {
        try {
            output.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void close(HessianInput input) {
        try {
            input.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
