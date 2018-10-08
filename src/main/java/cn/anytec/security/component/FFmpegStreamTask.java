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

//            this.existValue = process.waitFor();
            //用一个读输出流类去读
            InputStream fis=process.getInputStream();

            InputStreamReader isr=new InputStreamReader(fis);

            BufferedReader br=new BufferedReader(isr);

//            String line=null;
            try {
                while(br.readLine()!=null) {
                    //有可能发生阻塞的问题
                }
            }catch (IOException e){
                this.existValue=2;
            }
            process.waitFor();
            this.existValue = process.exitValue();
            logger.info("exitVal:" + existValue);


        } catch (IOException | InterruptedException e) {
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
    //先判断线程是不是存在，再判断线程拉起的进程是不是存在
    public boolean isActive(){
        if(!super.isAlive()){
            logger.warn("thread for cmd 【 {} 】is not active ", Arrays.toString(cmds));
            return false;
        }else if(null == process || !process.isAlive()){
            logger.warn("process for cmd 【 {} 】 is not active ", Arrays.toString(cmds));
            return false;
        }else if(process.isAlive()) {
            logger.info("thread and process for 【 {} 】 is not active ", Arrays.toString(cmds));
            return true;
        }
        return false;
    }

    public Process getProcess() {
        return process;
    }

    public Integer getExistValue(){
        return this.existValue;
    }

    public String[] getCmds() {
        return cmds;
    }
}
