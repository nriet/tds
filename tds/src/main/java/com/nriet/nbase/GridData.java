//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.nriet.nbase;

import java.io.Serializable;
import java.util.Date;

public class GridData implements Serializable {
    private static final long serialVersionUID = -6699394137326914065L;
    private Date time;
    private int period;
    private int layer;
    private float startx;
    private float endx;
    private float starty;
    private float endy;
    private float dx;
    private float dy;
    private int nx;
    private int ny;
    private float dline;
    private float startLine;
    private float endLine;
    private float coef;
    private float boldLine;
    private int proj;
    private float[][] vals;
    private float invalidVal;

    public GridData() {
    }

    public Date getTime() {
        return this.time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getPeriod() {
        return this.period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getLayer() {
        return this.layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public float getStartx() {
        return this.startx;
    }

    public void setStartx(float startx) {
        this.startx = startx;
    }

    public float getEndx() {
        return this.endx;
    }

    public void setEndx(float endx) {
        this.endx = endx;
    }

    public float getStarty() {
        return this.starty;
    }

    public void setStarty(float starty) {
        this.starty = starty;
    }

    public float getEndy() {
        return this.endy;
    }

    public void setEndy(float endy) {
        this.endy = endy;
    }

    public float getDx() {
        return this.dx;
    }

    public void setDx(float dx) {
        this.dx = dx;
    }

    public float getDy() {
        return this.dy;
    }

    public void setDy(float dy) {
        this.dy = dy;
    }

    public int getNx() {
        return this.nx;
    }

    public void setNx(int nx) {
        this.nx = nx;
    }

    public int getNy() {
        return this.ny;
    }

    public void setNy(int ny) {
        this.ny = ny;
    }

    public float getDline() {
        return this.dline;
    }

    public void setDline(float dline) {
        this.dline = dline;
    }

    public float getStartLine() {
        return this.startLine;
    }

    public void setStartLine(float startLine) {
        this.startLine = startLine;
    }

    public float getEndLine() {
        return this.endLine;
    }

    public void setEndLine(float endLine) {
        this.endLine = endLine;
    }

    public float getCoef() {
        return this.coef;
    }

    public void setCoef(float coef) {
        this.coef = coef;
    }

    public float getBoldLine() {
        return this.boldLine;
    }

    public void setBoldLine(float boldLine) {
        this.boldLine = boldLine;
    }

    public int getProj() {
        return this.proj;
    }

    public void setProj(int proj) {
        this.proj = proj;
    }

    public float[][] getVals() {
        return this.vals;
    }

    public void setVals(float[][] vals) {
        this.vals = vals;
    }

    public float getInvalidVal() {
        return this.invalidVal;
    }

    public void setInvalidVal(float invalidVal) {
        this.invalidVal = invalidVal;
    }
}
