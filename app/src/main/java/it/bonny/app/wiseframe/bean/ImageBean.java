package it.bonny.app.wiseframe.bean;

public class ImageBean {
    private long id;
    private String imagePath;
    private String name;

    public ImageBean() {}

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

}
