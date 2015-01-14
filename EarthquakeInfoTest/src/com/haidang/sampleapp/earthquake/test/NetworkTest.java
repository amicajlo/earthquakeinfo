package com.haidang.sampleapp.earthquake.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.test.UiThreadTest;

import com.haidang.sampleapp.earthquake.Earthquakes;
import com.haidang.sampleapp.earthquake.exception.NetworkNotAvailableException;
import com.haidang.sampleapp.earthquake.network.NetworkManager;
import com.haidang.sampleapp.earthquake.network.NetworkRequest;
import com.haidang.sampleapp.earthquake.network.ServiceResponse;

public class NetworkTest extends MyTestCase {
	CountDownLatch signal;
	
	@UiThreadTest
	public void testDownloadData() throws InterruptedException {
		signal = new CountDownLatch(1);
		try {
			NetworkManager.getInstance().getEarthquakesData(new NetworkRequest.NetworkCallback<ServiceResponse<Earthquakes>>() {
				@Override
				public void update(ServiceResponse<Earthquakes> response) {
					assertNotNull(response);
					assertNotNull(response.result);
					assertNull(response.error);
					assertNotNull(response.result.earthquakes);
					assertTrue(!response.result.earthquakes.isEmpty());
					signal.countDown();
				}
			});
			
		} catch (NetworkNotAvailableException e) {
			e.printStackTrace();
			fail("No network connection");
		}
		signal.await(10, TimeUnit.SECONDS);

	}

}
