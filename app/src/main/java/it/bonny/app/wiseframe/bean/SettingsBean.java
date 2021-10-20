package it.bonny.app.wiseframe.bean;

import java.util.Set;

public class SettingsBean {
    //Slide show
    private Integer displayTime;
    private Integer displayEffect;
    private Integer transitionEffect;
    private Integer photoOrder;
    private boolean decoration;
    //Generic
    private boolean displayOn;
    private String startSchedules;
    private boolean launchBoot;

    //Firebase
    private boolean activeCrash;
    private boolean activeAnalytics;
    private boolean activeMessaging;

    //Day
    private Set<String> listDays;

    public SettingsBean() {}

    public Integer getDisplayTime() {
        return displayTime;
    }
    public void setDisplayTime(Integer displayTime) {
        this.displayTime = displayTime;
    }

    public Integer getTransitionEffect() {
        return transitionEffect;
    }
    public void setTransitionEffect(Integer transitionEffect) {
        this.transitionEffect = transitionEffect;
    }

    public Integer getDisplayEffect() {
        return displayEffect;
    }
    public void setDisplayEffect(Integer displayEffect) {
        this.displayEffect = displayEffect;
    }

    public Integer getPhotoOrder() {
        return photoOrder;
    }
    public void setPhotoOrder(Integer photoOrder) {
        this.photoOrder = photoOrder;
    }

    public boolean isDecoration() {
        return decoration;
    }
    public void setDecoration(boolean decoration) {
        this.decoration = decoration;
    }

    public String getStartSchedules() {
        return startSchedules;
    }
    public void setStartSchedules(String startSchedules) {
        this.startSchedules = startSchedules;
    }

    public boolean isDisplayOn() {
        return displayOn;
    }
    public void setDisplayOn(boolean displayOn) {
        this.displayOn = displayOn;
    }

    public boolean isLaunchBoot() {
        return launchBoot;
    }
    public void setLaunchBoot(boolean launchBoot) {
        this.launchBoot = launchBoot;
    }

    public boolean isActiveCrash() {
        return activeCrash;
    }
    public void setActiveCrash(boolean activeCrash) {
        this.activeCrash = activeCrash;
    }

    public boolean isActiveAnalytics() {
        return activeAnalytics;
    }
    public void setActiveAnalytics(boolean activeAnalytics) {
        this.activeAnalytics = activeAnalytics;
    }

    public boolean isActiveMessaging() {
        return activeMessaging;
    }
    public void setActiveMessaging(boolean activeMessaging) {
        this.activeMessaging = activeMessaging;
    }

    public Set<String> getListDays() {
        return listDays;
    }
    public void setListDays(Set<String> listDays) {
        this.listDays = listDays;
    }

    public Integer getDisplayTimeInMilliSeconds() {
        return displayTime * 1000;
    }

}
