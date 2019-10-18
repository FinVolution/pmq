package com.ppdai.infrastructure.mq.biz.common.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

@RunWith(JUnit4.class)
public class HttpClientTest {

	@Test
	public void HttpClient1Test() {
		HttpClient httpClient = new HttpClient();
		HttpClient httpClient1 = new HttpClient(10000, 10000);
	}

	@Test
	public void checkTest() throws IOException {
		ClientAndServer mockClient = new ClientAndServer(5000);
		try {
			mockClient.when(HttpRequest.request().withMethod("GET"))
					.respond(HttpResponse.response().withStatusCode(200));
			HttpClient httpClient = new HttpClient();
			assertEquals(true, httpClient.check("http://localhost:5000/hs"));
		} finally {
			mockClient.stop(true);
		}
	}

	@Test
	public void checkFalseTest() throws IOException {
		// ClientAndServer mockClient = new ClientAndServer (5000);
		// mockClient.when(HttpRequest.request().withMethod("GET")).respond(HttpResponse.response().withStatusCode(200));
		HttpClient httpClient = new HttpClient(10, 10);
		assertEquals(false, httpClient.check("http://localhost:5000/hs"));
	}

	@Test
	public void getTest() throws IOException {
		ClientAndServer mockClient = new ClientAndServer(5000);
		try {
			mockClient.when(HttpRequest.request().withMethod("GET"))
					.respond(HttpResponse.response().withStatusCode(200).withBody("1"));
			HttpClient httpClient = new HttpClient();
			assertEquals("1", httpClient.get("http://localhost:5000/hs"));
		} finally {
			mockClient.stop(true);
		}
	}

	@Test
	public void getErrorTest() throws IOException {
		ClientAndServer mockClient = new ClientAndServer(5000);
		try {
			mockClient.when(HttpRequest.request().withMethod("GET"))
					.respond(HttpResponse.response().withStatusCode(700).withBody("1"));
			HttpClient httpClient = new HttpClient();
			boolean rs = false;
			try {
				assertEquals("1", httpClient.get("http://localhost:5000/hs"));
			} catch (Exception e) {
				rs = true;
			}
			assertEquals(true, rs);
		} finally {
			mockClient.stop(true);
		}
	}

	@Test
	public void getError1Test() throws IOException {
		ClientAndServer mockClient = new ClientAndServer(5000);
		try {
			mockClient.when(HttpRequest.request().withMethod("GET"))
					.respond(HttpResponse.response().withStatusCode(700).withBody("1"));
			HttpClient httpClient = new HttpClient();
			boolean rs = false;
			try {
				assertEquals("1", httpClient.get("http://localhost:50001/hs"));
			} catch (Exception e) {
				rs = true;
			}
			assertEquals(true, rs);
		} finally {
			mockClient.stop(true);
		}
	}

	@Test
	public void postTest() throws IOException, BrokerException {
		ClientAndServer mockClient = new ClientAndServer(5000);
		try {
			mockClient.when(HttpRequest.request().withMethod("POST"))
					.respond(HttpResponse.response().withStatusCode(200).withBody("1"));
			HttpClient httpClient = new HttpClient();
			assertEquals("1", httpClient.post("http://localhost:5000/hs", "1"));
		} finally {
			mockClient.stop(true);
		}
	}

	@Test
	public void postErrorTest() throws IOException {
		ClientAndServer mockClient = new ClientAndServer(5000); 
		try {
			mockClient.when(HttpRequest.request().withMethod("POST"))
					.respond(HttpResponse.response().withStatusCode(700).withBody("1"));
			HttpClient httpClient = new HttpClient();
			boolean rs = false;
			try {
				assertEquals("1", httpClient.post("http://localhost:5000/hs", "1"));
			} catch (Exception e) {
				rs = true;
			}
			assertEquals(true, rs);
		} finally {
			mockClient.stop(true);
		}
	}

	@Test
	public void postError1Test() throws IOException {
		ClientAndServer mockClient = new ClientAndServer(5000);
		try {
			mockClient.when(HttpRequest.request().withMethod("POST"))
					.respond(HttpResponse.response().withStatusCode(200).withBody("1"));
			HttpClient httpClient = new HttpClient();
			boolean rs = false;
			try {
				assertEquals("1", httpClient.post("http://localhost:5001/hs", "1"));
			} catch (Exception e) {
				rs = true;
			}
			assertEquals(true, rs);
		} finally {
			mockClient.stop(true);
		}
	}

	@Test
	public void post1Test() throws IOException, BrokerException {
		ClientAndServer mockClient = new ClientAndServer(5000);
		try {
			mockClient.when(HttpRequest.request().withMethod("POST"))
					.respond(HttpResponse.response().withStatusCode(200).withBody("{\"t\":1}"));
			HttpClient httpClient = new HttpClient();
			assertEquals(1, httpClient.post("http://localhost:5000/hs", "1", TestDemo.class).getT());
		} finally {
			mockClient.stop(true);
		}
	}

	@Test
	public void postError2Test() throws IOException {
		ClientAndServer mockClient = new ClientAndServer(5000);
		try {
			mockClient.when(HttpRequest.request().withMethod("POST"))
					.respond(HttpResponse.response().withStatusCode(700).withBody("1"));
			HttpClient httpClient = new HttpClient();
			boolean rs = false;
			try {
				assertEquals(1, httpClient.post("http://localhost:5000/hs", "1", TestDemo.class).getT());
			} catch (Exception e) {
				rs = true;
			}
			assertEquals(true, rs);
		} finally {
			mockClient.stop(true);
		}
	}

	@Test
	public void postError3Test() throws IOException {
		ClientAndServer mockClient = new ClientAndServer(5000);
		try {
			mockClient.when(HttpRequest.request().withMethod("POST"))
					.respond(HttpResponse.response().withStatusCode(200).withBody("1"));
			HttpClient httpClient = new HttpClient();
			boolean rs = false;
			try {
				assertEquals(1, httpClient.post("http://localhost:5001/hs", "1", TestDemo.class).getT());
			} catch (Exception e) {
				rs = true;
			}
			assertEquals(true, rs);
		} finally {
			mockClient.stop(true);
		}
	}

	public static class TestDemo {
		private int t;

		public int getT() {
			return t;
		}

		public void setT(int t) {
			this.t = t;
		}
	}
}
