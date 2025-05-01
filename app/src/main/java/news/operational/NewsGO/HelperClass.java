package news.operational.NewsGO;

public class HelperClass {

    String name, email, username, phone, authenticationType,imageUrl,fcmToken;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAuthenticationType() {
        return authenticationType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public HelperClass(String name, String email, String username, String phone, String authenticationType, String imageUrl) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.authenticationType = authenticationType;
        this.imageUrl = imageUrl;
    }

    public HelperClass() {
    }
}
