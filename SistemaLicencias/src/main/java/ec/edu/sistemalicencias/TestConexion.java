package ec.edu.sistemalicencias;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TestConexion {

    public static void main(String[] args) throws Exception {

        String url = "https://vnnlolntgzdpyyghirth.supabase.co/rest/v1/administradores";
        String anonKey = "sb_publishable_e3igu5tI1FPfuQw-fWu0Nw_zFk3dkNL";

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apikey", anonKey)
                .header("Authorization", "Bearer " + anonKey)
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status: " + response.statusCode());
        System.out.println(response.body());
    }
}
