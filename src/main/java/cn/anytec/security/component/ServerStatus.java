package cn.anytec.security.component;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class ServerStatus {
    private static final Logger logger = LoggerFactory.getLogger(ServerStatus.class);

    public String getMemoryStatus() {
        Sigar sigar = new Sigar();
        Mem mem = null;
        try {
            mem = sigar.getMem();
        } catch (SigarException e) {
            e.printStackTrace();
        }
        //计数单位 K
        Long totalMemory = mem.getTotal() / 1024L;
        Long usedMemory = mem.getUsed() / 1024L;
        Long freeMemory = mem.getFree() / 1024L;
        double percent = (double) usedMemory/totalMemory;
        DecimalFormat decimalFormat = new DecimalFormat("0.0%");
        String result = decimalFormat.format(percent);
        return result;
    }
}
