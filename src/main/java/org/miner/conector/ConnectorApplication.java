package org.miner.conector;

import java.io.FileInputStream;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConnectorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConnectorApplication.class, args);
		try {
			initFcm();
		} catch (IOException e) {
			log(e.toString());
		}
	}

	private static void initFcm() throws IOException {
		FileInputStream serviceAccount = new FileInputStream("path/to/serviceAccountKey.json");

//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                .setDatabaseUrl("https://<DATABASE_NAME>.firebaseio.com/")
//                .build();

		log("initializing FCM...");
		GoogleCredentials googleCredentials = GoogleCredentials.getApplicationDefault();
		log("google credentials: " + googleCredentials);
		FirebaseOptions options = new FirebaseOptions.Builder()
//                .setCredentials(googleCredentials)
//                .setDatabaseUrl("https://<DATABASE_NAME>.firebaseio.com/")
				.setProjectId("mining-connector")
				.build();

		FirebaseApp.initializeApp(options);
		log("FCM initialised");
	}

	private static void log(String message) {
		System.out.println(message);
	}
}
