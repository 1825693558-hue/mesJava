package com.demo.mes.vo;

public class DashboardOverviewVO {
    private int plannedQty;
    private int completedQty;
    private int scrapQty;
    private double completionRate;
    private double scrapRate;
    private int activeOrderCount;
    private int runningEquipmentCount;
    private int totalEquipmentCount;

    public int getPlannedQty() {
        return plannedQty;
    }

    public void setPlannedQty(int plannedQty) {
        this.plannedQty = plannedQty;
    }

    public int getCompletedQty() {
        return completedQty;
    }

    public void setCompletedQty(int completedQty) {
        this.completedQty = completedQty;
    }

    public int getScrapQty() {
        return scrapQty;
    }

    public void setScrapQty(int scrapQty) {
        this.scrapQty = scrapQty;
    }

    public double getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(double completionRate) {
        this.completionRate = completionRate;
    }

    public double getScrapRate() {
        return scrapRate;
    }

    public void setScrapRate(double scrapRate) {
        this.scrapRate = scrapRate;
    }

    public int getActiveOrderCount() {
        return activeOrderCount;
    }

    public void setActiveOrderCount(int activeOrderCount) {
        this.activeOrderCount = activeOrderCount;
    }

    public int getRunningEquipmentCount() {
        return runningEquipmentCount;
    }

    public void setRunningEquipmentCount(int runningEquipmentCount) {
        this.runningEquipmentCount = runningEquipmentCount;
    }

    public int getTotalEquipmentCount() {
        return totalEquipmentCount;
    }

    public void setTotalEquipmentCount(int totalEquipmentCount) {
        this.totalEquipmentCount = totalEquipmentCount;
    }
}
