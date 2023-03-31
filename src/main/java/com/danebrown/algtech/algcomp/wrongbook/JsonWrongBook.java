package com.danebrown.algtech.algcomp.wrongbook;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileMode;
import cn.hutool.json.JSONUtil;
import com.danebrown.algtech.algcomp.WrongBook;
import com.google.common.base.Charsets;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by danebrown on 2021/8/19
 * mail: tain198127@163.com
 *
 * @author danebrown
 */
@Log4j2
public class JsonWrongBook<R> implements WrongBook {
    static int LENGTH = 0x8000000; // 128 Mb
    private static Object fileCreatorLock = new Object();
    private String EXTENSION=".wb";
    private String filePath;
    private Gson gson = new Gson();
    public JsonWrongBook(String filePath) {
        this.filePath = filePath;

    }

    public JsonWrongBook() {
        this("./");
    }

    private void checkAndCreate() {
        File file = new File(this.filePath);
        if (file.isDirectory()) {
            if (!file.exists()) {
                synchronized (this.filePath) {
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                }
            }
        } else {
            throw new RuntimeException(this.filePath + "不是目录");
        }

    }

    private File create(String wrongBookFileName) {
        String filename = this.filePath + wrongBookFileName+EXTENSION;
        File file = FileUtil.file(filename);
        if (!file.exists()) {
            synchronized (filename) {
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        log.error("创建文件:[{}]失败",filename,e);
                    }
                }
            }
        }
        return file;
    }

    @Override
    public void write(String testName, Object setupData) {
        if (setupData == null) {
            return;
        }
        checkAndCreate();

        try(BufferedWriter raFile =
                    new BufferedWriter(new OutputStreamWriter(new FileOutputStream(create(testName),
        true), Charsets.UTF_8))){
            String json = gson.toJson(setupData,
                    new com.google.gson.reflect.TypeToken<R>(){}.getType());
                try {

                    raFile.write(json+"\n");
                    raFile.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }


        } catch (IOException e) {
            log.error("testName错题集写入失败", e);
        }


    }

    @Override
    public  List load(String testName, Type type) {
        checkAndCreate();
        List list = new ArrayList<>();

        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(create(testName)))){

            String line;
            while ((line = bufferedReader.readLine())!=null){

                Object r = gson.fromJson(line,
                        type);
                list.add(r);
            }

        } catch (IOException e) {
            log.error("读取错题本出错",e);
        }
        return list;
    }


}
