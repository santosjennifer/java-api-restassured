package com.spotify.api.music.test;

import org.junit.BeforeClass;
import org.junit.Test;

import com.spotify.api.music.utils.EncodeLoginToken;
import com.spotify.api.music.core.BaseAPI;

import static io.restassured.RestAssured.given;
import io.restassured.response.Response;
import static org.hamcrest.CoreMatchers.equalTo;

public class SpotifyAPITest extends BaseAPI {

	static String accessToken;
	static String clientId = "318c6661bf8747c1a2d65d17b47ed614";
	static String clientSecret = "0415a0ab212744d98be552770e56faec";
	String idArtist = "6KImCVD70vtIoJWnq6nGn3";
	String idPlaylist = "3cEYpjA9oz9GiPac4AsH4n";
	String idAlbum = "7mzrIsaAjnXihW3InKjlC3";
	
	@BeforeClass
	public static void authenticationSpotify() {
		Response responseLogin = 
				given()
					.header("Authorization", "Basic " + EncodeLoginToken.getAuthToken(clientId, clientSecret))
					.formParam("grant_type", "client_credentials")
					.contentType("application/x-www-form-urlencoded")
					.when()
						.post("https://accounts.spotify.com/api/token");

		accessToken = responseLogin.jsonPath().get("access_token");

		/*
		 * Using EncodeLoginToken to convert clientId and clientSercret to base64
		 */
	}

	/*
	@BeforeClass
	public void authenticationSpotify() {
		Response responseLogin = given().header("Authorization",
				"Basic MzE4YzY2NjFiZjg3NDdjMWEyZDY1ZDE3YjQ3ZWQ2MTQ6MDQxNWEwYWIyMTI3NDRkOThiZTU1Mjc3MGU1NmZhZWM=")
				.formParam("grant_type", "client_credentials").contentType("application/x-www-form-urlencoded").when()
				.post("https://accounts.spotify.com/api/token");

		accessToken = responseLogin.jsonPath().get("access_token");

		*** Using authToken manually converted to base64

	} */

	@Test
	public void getNewReleases() {
		given()
			.header("Authorization", "Bearer " + accessToken)
			.when()
				.get("/browse/new-releases")
			.then()
				.statusCode(200);
	}
	
	@Test
	public void getArtist() {
		given()
			.header("Authorization", "Bearer " + accessToken)
			.when()
				.get("/artists/" + idArtist)
			.then().log().body()
				.statusCode(200)
				.body("name", equalTo("Harry Styles"));
	}
	
	@Test
	public void getArtistWithoutId() {
		given()
			.header("Authorization", "Bearer " + accessToken)
			.when()
				.get("/artists/")
			.then().log().body()
				.statusCode(400)
				.body("error.message", equalTo("invalid id"));
	}
	
	@Test
	public void getSearchArtist() {
		given()
			.header("Authorization", "Bearer " + accessToken)
			.when()
				.get("/search?q=Taylor Swift&type=artist")
			.then()
				.statusCode(200)
				.body("artists.items[0].name", equalTo("Taylor Swift"));
	}
	
	@Test
	public void getSearchArtistWithoutType() {
		given()
			.header("Authorization", "Bearer " + accessToken)
			.when()
				.get("/search?q=Taylor Swift")
			.then()
				.statusCode(400)
				.body("error.message", equalTo("Missing parameter type"));
	}
	
	
	@Test
	public void getPlaylist() {
		given()
			.header("Authorization", "Bearer " + accessToken)
			.when()
				.get("/playlists/" + idPlaylist)
			.then()
				.statusCode(200)
				.body("description", equalTo("A playlist for testing pourposes"));
	}
	
	@Test
	public void getPlaylistWithIncorrectId() {
		given()
			.header("Authorization", "Bearer " + accessToken)
			.when()
				.get("/playlists/7mzrIsaAjnXihW3InKjlC3")
			.then().log().body()
				.statusCode(404)
				.body("error.message", equalTo("Not found."));
	}
	
	@Test
	public void getAlbum() {
		given()
			.header("Authorization", "Bearer " + accessToken)
			.when()
				.get("/albums/" + idAlbum)
			.then()
				.statusCode(200)
				.body("artists[0].name", equalTo("Taylor Swift"))
				.body("album_type", equalTo("album"));
	}
	
	@Test
	public void getAlbumWithIncorrectId() {
		given()
			.header("Authorization", "Bearer " + accessToken)
			.when()
				.get("/albums/6KImCVD70vtIoJWnq6nGn3")
			.then()
				.statusCode(404)
				.body("error.message", equalTo("non existing id"));
	}

}
