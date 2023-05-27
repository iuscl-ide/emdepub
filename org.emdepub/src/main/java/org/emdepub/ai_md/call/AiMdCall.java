package org.emdepub.ai_md.call;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.eclipse.core.runtime.Platform;
import org.emdepub.activator.Activator;
import org.emdepub.ai_md.call.model.AiMdCallRequest_body;
import org.emdepub.ai_md.call.model.AiMdCallRequest_message;
import org.emdepub.ai_md.call.model.AiMdCallResponse_body;
import org.emdepub.common.utils.CU;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AiMdCall {

	static String url = "https://api.openai.com/v1/chat/completions";
	
	static String header_Authorization = findAuthorization();
	
	static String aiTextAction = "Rephrase the text (and keep the Markdown tags)";
	
	static String aiModel = "gpt-3.5-turbo-0301";
	
	static String aiRole = "user";

	@SneakyThrows(IOException.class)
	private static String findAuthorization() {

		return CU.loadInputStreamInString(Platform.getBundle(Activator.PLUGIN_ID).getResource("config/ChatGPT_Authorization.txt").openStream()); 
	}
	
	@SneakyThrows({URISyntaxException.class, InterruptedException.class, IOException.class})
	public static String callRephrase(String text) {
		
		AiMdCallRequest_message request_message = new AiMdCallRequest_message(aiRole, aiTextAction + ": " + text);
		
		AiMdCallRequest_body request_body = new AiMdCallRequest_body(aiModel);
		request_body.getMessages().add(request_message);
		
		
		String requestBody = CU.jsonSerialize(request_body);
		System.out.println(requestBody);
		
		HttpClient httpClient = HttpClient.newHttpClient();
		
		URI postURI = new URI(url);
		
		HttpRequest httpRequestPost = HttpRequest.newBuilder()
                .uri(postURI)
                .header("Content-Type", "application/json")
                .header("Authorization", header_Authorization)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
		
		HttpResponse<String> postResponse = httpClient.send(httpRequestPost, HttpResponse.BodyHandlers.ofString());
		System.out.println(postResponse.body());
		
		try {
			AiMdCallResponse_body response_body = CU.jsonDeserialize(postResponse.body(), AiMdCallResponse_body.class);
			
			String resultText = response_body.getChoices().get(0).getMessage().getContent();
			
			System.out.println(resultText);
			
			return resultText;
			
		} catch (Exception e) {
			return "result text error";
		}
	}
}
