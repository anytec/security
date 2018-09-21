package cn.anytec.security.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.Date;

public class FFmpegStreamTask extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(FFmpegStreamTask.class);

    private Process process;

    private Integer existValue;


    private String[] cmds;

    public FFmpegStreamTask(String[] cmds){
        this.cmds=cmds;
    }

    @Override
    public void run() {
        try {

            ProcessBuilder builder = new ProcessBuilder(cmds);
            builder.redirectErrorStream(true);
            logger.info("【ffmpeg command】 {}", Arrays.toString(cmds));
            process=builder.start();

            this.existValue = process.waitFor();
            //用一个读输出流类去读
            InputStream fis=process.getInputStream();

            InputStreamReader isr=new InputStreamReader(fis);

            BufferedReader br=new BufferedReader(isr);

            String line=null;
            try {
                while((line = br.readLine())!=null) {
                    //有可能发生阻塞的问题
                }
            }catch (IOException e){
            }

            this.existValue = process.waitFor();
            logger.info("exitVal:" + existValue);


        } catch (IOException e) {
            e.printStackTrace();
        }catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

        }
    }
    public boolean destory(){

        while (process.isAlive()) {
            process.destroy();
        }
        return true;
    }

    public Process getProcess() {
        return process;
    }

    public Integer getExistValue(){
        return this.existValue;
    }

}
