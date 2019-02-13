package com.ruoyi.server.domain;

import com.ruoyi.system.domain.SysCollectionPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: wtao
 * @Date: 2019-01-07 19:55
 * @Version 1.0
 */
public class ResolveRecord {

    private int reader;

    private int readerIndex;

    private int readerAdr;

    private int startAdr;

    private List<SysCollectionPoint> points = new ArrayList<>();

    public ResolveRecord(SysCollectionPoint point) {
        this.points.add(point);
        startAdr = Integer.valueOf(point.getRegisterAdr()) % 10000 - 1;
        readerAdr = startAdr;
    }

    public int getReader() {
        return reader;
    }

    public void setReader(int reader) {
        this.reader = reader;
        if (reader >= points.size())
            this.reader = 0;
    }

    public List<SysCollectionPoint> getPoints() {
        return points;
    }

    public void setPoints(List<SysCollectionPoint> points) {
        this.points = points;
    }

    public int getReaderIndex() {
        return readerIndex;
    }

    public void setReaderIndex(int readerIndex) {
        this.readerIndex = readerIndex;
    }

    public int getReaderAdr() {
        return readerAdr;
    }

    public void setReaderAdr(int readerAdr) {
        this.readerAdr = readerAdr;
    }

    public void resetReaderAdr() {
        readerAdr = startAdr;
    }
}
