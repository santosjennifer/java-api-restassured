package com.spotify.api.music.core;

import org.junit.BeforeClass;

import io.restassured.RestAssured;

public abstract class BaseAPI {

	@BeforeClass
	public static void url() {
		RestAssured.baseURI = "https://api.spotify.com";
		RestAssured.basePath = "/v1";
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
	}

}
