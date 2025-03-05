package tp1progreseau.utils;

import java.io.IOException;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tp1progreseau.gameElement.Item;
import tp1progreseau.gameElement.Snake;
import tp1progreseau.gameElement.fabrique.TypeSnake;
import tp1progreseau.model.InputMap;
import tp1progreseau.model.SnakeGame;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PanelBuilder {
	
    private ArrayList<FeaturesSnake> featuresSnakes;
    private ArrayList<FeaturesItem> featuresItems;
    
    // Constructeur par défaut nécessaire pour Jackson
    public PanelBuilder() {
    }
    
    public PanelBuilder(ArrayList<FeaturesSnake> featuresSnakes, ArrayList<FeaturesItem> featuresItems) {
        this.featuresSnakes = featuresSnakes;
        this.featuresItems = featuresItems;
    }
    
    public ArrayList<FeaturesSnake> getFeaturesSnakes() {
        return this.featuresSnakes;
    }
    
    public ArrayList<FeaturesItem> getFeaturesItems() {
        return this.featuresItems;
    }

    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }
    
    public static PanelBuilder fromJson(String json) throws IOException {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON content is null or empty");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, PanelBuilder.class);
    }

}
