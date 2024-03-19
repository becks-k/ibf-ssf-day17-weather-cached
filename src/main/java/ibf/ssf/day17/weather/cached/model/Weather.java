package ibf.ssf.day17.weather.cached.model;

import jakarta.json.Json;

public class Weather {

    private String icon;
    private String main;
    private String description;

    
    public Weather() {
    }

    public Weather(String icon, String main, String description) {
        this.icon = icon;
        this.main = main;
        this.description = description;
    }


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String buildJsonString() {
        String weatherJson = Json.createObjectBuilder()
        .add("icon", getIcon())
        .add("main", getMain())
        .add("description", getDescription())
        .build().toString();

        return weatherJson;
    }

    @Override
    public String toString() {
        return buildJsonString();
    }

    
}