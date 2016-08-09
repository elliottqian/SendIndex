package netease.beauty.pub;



import sun.misc.IOUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by hzqianwei on 2016/7/26.
 *
 */
public class SeDemo {
    public static void toFile(String filename, Object o) {
        ObjectOutputStream objectOutputStreams = null;
        try{
            objectOutputStreams = new ObjectOutputStream(new FileOutputStream(filename));
            objectOutputStreams.writeObject(o);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                objectOutputStreams.close();
            }catch (Exception e){e.printStackTrace();}
        }
    }
}
