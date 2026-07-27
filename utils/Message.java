package tp1progreseau.utils;

import java.io.IOException;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Message implements Serializable {
	
    private static final long serialVersionUID = 1L;

    @JsonProperty("message")
    private String message;

    @JsonProperty("motif")
    private String motif;

    // Constructeurs
    public Message() {
    }

    public Message(String message, String motif) {
        this.message = message;
        this.motif = motif;
    }

    // Getters et setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Message fromJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, Message.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return  motif + ':' + message;
    }
}
