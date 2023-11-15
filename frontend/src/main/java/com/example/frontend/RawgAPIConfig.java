package com.example.frontend;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RawgAPIConfig {
    public static ArrayList<Game> getGames() throws Exception {
        String apiKey = System.getenv("RAWG_API_KEY");
        ArrayList<Game> games = new ArrayList<>();

        if (apiKey == null) {
            throw new Exception("API key not found in environment variables");
        }

        String apiUrl = "https://api.rawg.io/api/games?key=" + apiKey + "&page_size=20&ordering=-metacritic";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        if (response != null && response.statusCode() == 200) {
            String responseBody = response.body();
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

            // Check if the "results" array exists
            if (jsonObject.has("results")) {
                JsonArray resultsArray = jsonObject.getAsJsonArray("results");

                // Iterate through the elements in the "results" array
                for (int i = 0; i < resultsArray.size(); i++) {
                    Game game = new Game();
                    JsonObject resultObject = resultsArray.get(i).getAsJsonObject();

                    // Check if the "name" property exists for each element
                    if (resultObject.has("name")) {
                        String name = resultObject.get("name").getAsString();
                        game.setName(name);
                    }

                    // Check if the "platforms" property exists for each element
                    if (resultObject.has("platforms")) {
                        JsonArray platformsArray = resultObject.getAsJsonArray("platforms");
                        ArrayList<String> platformNames = new ArrayList<>();
                        for (int j = 0; j < platformsArray.size(); j++) {
                            JsonObject platformObject = platformsArray.get(j).getAsJsonObject();
                            if (platformObject.has("platform")) {
                                JsonObject platform = platformObject.getAsJsonObject("platform");
                                if (platform.has("name")) {
                                    String platformName = platform.get("name").getAsString();
                                    platformNames.add(platformName);
                                }
                            }
                        }
                        game.setPlatforms(platformNames);
                    }

                    // Check if the "released" property exists for each element
                    if(resultObject.has("released")){
                        String released = resultObject.get("released").getAsString();
                        game.setReleased(released);
                    }

                    // Check if the "rating" property exists for each element
                    if(resultObject.has("rating")){
                        String rating = resultObject.get("rating").getAsString();
                        game.setRating(rating);
                    }

                    // Check if the "id" property exists for each element
                    if(resultObject.has("id")){
                        int id = resultObject.get("id").getAsInt();
                        game.setId(id);
                    }

                    // Check if the "esrb_rating" property exists for each element
                    if (resultObject.has("esrb_rating") && !resultObject.get("esrb_rating").isJsonNull()) {
                        JsonObject esrbObject = resultObject.getAsJsonObject("esrb_rating");

                        if (esrbObject.has("name")) {
                            String esrb = esrbObject.get("name").getAsString();
                            game.setEsrb(esrb);
                        }
                    }

                    // Check if the "background_image" property exists for each element
                    if (resultObject.has("background_image") && !resultObject.get("background_image").isJsonNull()) {
                        String background_image = resultObject.get("background_image").getAsString();
                        game.setBackground_image(background_image);
                    }
                    games.add(game);
                }
            } else {
                System.out.println("The 'results' array is missing in the JSON response.");
                return new ArrayList<Game>();
            }
        }
        return games;
    }
}
