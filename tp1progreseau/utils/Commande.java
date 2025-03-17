package tp1progreseau.utils;

import java.io.IOException;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Commande implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonProperty("action")
    private AgentAction action;
    
    public Commande() {
    	
    }

    public Commande(AgentAction action) {
        this.action = action;
    }

    public AgentAction getAction() {
        return this.action;
    }

    public String show() {
        switch (this.action) {
            case MOVE_RIGHT:
                return "Déplacer à droite";
            case MOVE_LEFT:
                return "Déplacer à gauche";
            case MOVE_UP:
                return "Déplacer en haut";
            case MOVE_DOWN:
                return "Déplacer en bas";
            default:
                return "Action inconnue";
        }
    }

    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        String json;
        json = "";
        try {
            json = objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        };
        return json;
    }

    public static Commande fromJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        Commande cmd = null;
        try {
            cmd = objectMapper.readValue(json, Commande.class);
        } catch (IOException e) {
        }
        return cmd;
    }
}
