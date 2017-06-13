package pw.androidthanatos.ant.util;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import pw.androidthanatos.ant.controller.ResponseListener;
import pw.androidthanatos.ant.handler.AntHandler;

/**
 * Created by liuxiongfei on 2017/6/12.
 */

public class PaseIO {

    public static <T> ByteArrayOutputStream parseIo(InputStream inputStream, final Long dataLength, final ResponseListener<T> listener){
        int currentLength =0;
        int len = 0;
        byte[] bt = new byte[1024];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            while ((len= inputStream.read(bt))!= -1){
                bos.write(bt,0,len);
                currentLength += len;
                final int progress = (int) (currentLength*100/dataLength);
                AntHandler.INSTANCE.getAntHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (dataLength == -1){
                            listener.progress(-1);
                        }else {
                            listener.progress(progress);
                        }

                    }
                });
            }
            return bos;
        } catch (IOException e) {
            e.printStackTrace();
            return new ByteArrayOutputStream(1);
        }

    }
}
