package framework;

import com.itextpdf.text.Image;

public class LogPortion {
    private String description;
    private Image image;

    public LogPortion(String description, Image image) {
        super();
        this.description = description;
        this.image = image;
    }

    public LogPortion(String description) {
        super();
        this.description = description;
        this.image = null;
    }

    public LogPortion(Image image) {
        super();
        this.description = "No operation description supplied";
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

}
