package tech.berjis.mwakazyadmin;

public class Categories {
    String name, image, categoryId;

    public Categories(String name, String image, String categoryId) {
        this.name = name;
        this.image = image;
        this.categoryId = categoryId;
    }

    public Categories(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
