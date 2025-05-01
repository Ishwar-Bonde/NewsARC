package news.operational.NewsGO;

public class ScreenItem {

    int Title,Description;
    int ScreenImg;

    public ScreenItem(int title, int description, int screenImg) {
        Title = title;
        Description = description;
        ScreenImg = screenImg;
    }

    public void setTitle(int title) {
        Title = title;
    }

    public void setDescription(int description) {
        Description = description;
    }

    public void setScreenImg(int screenImg) {
        ScreenImg = screenImg;
    }

    public int getTitle() {
        return Title;
    }

    public int getDescription() {
        return Description;
    }

    public int getScreenImg() {
        return ScreenImg;
    }
}