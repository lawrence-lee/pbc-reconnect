/**
 * Copyright 2000-present Liferay, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.pbcreconnect;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;

import com.google.common.util.concurrent.AbstractScheduledService;

import java.net.URLConnection;

import java.util.concurrent.TimeUnit;

/**
 * @author Christopher Bryan Boyd
 */
public class PBCReconnect {

	public static void main(String[] args) {
		new Checker().startAsync();
	}

	public static void post() throws Exception {
		final WebRequest requestSettings = new WebRequest(
			new java.net.URL("https://1.1.1.1/login.html"), HttpMethod.POST);

		requestSettings.setAdditionalHeader(
			"Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		requestSettings.setAdditionalHeader("Content-Type", "application/x-www-form-urlencoded");
		requestSettings.setAdditionalHeader("Referer", "https://1.1.1.1/login.html");
		requestSettings.setAdditionalHeader("Accept-Language", "en-US");
		requestSettings.setAdditionalHeader("Accept-Encoding", "gzip,deflate");
		requestSettings.setAdditionalHeader("User-Agent", "Java");
		requestSettings.setAdditionalHeader("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.3");
		requestSettings.setAdditionalHeader("X-Requested-With", "XMLHttpRequest");
		requestSettings.setAdditionalHeader("Cache-Control", "no-cache");
		requestSettings.setAdditionalHeader("Pragma", "no-cache");
		requestSettings.setAdditionalHeader("Host", "1.1.1.1");
		requestSettings.setAdditionalHeader("Connection", "keep-alive");
		requestSettings.setAdditionalHeader("Upgrade-Insecure-Requests", "1");
		requestSettings.setAdditionalHeader("Cookie", "GUEST_LANGUAGE_ID=en_US; COOKIE_SUPPORT=true");
		requestSettings.setAdditionalHeader("Origin", "http://1.1.1.1/login.html");

		requestSettings.setRequestBody(
			"buttonClicked=4&err_flag=0&err_msg=&info_flag=0&info_msg=&redirect_url=&network_name=Guest+Network");

		final WebClient webClient = new WebClient();

		webClient.getOptions().setUseInsecureSSL(true);
		webClient.getPage(requestSettings);
		webClient.close();
	}

	public static boolean testInet(String site) {
		try {
			java.net.URL url = new java.net.URL(site);

			URLConnection connection = url.openConnection();

			connection.connect();
		}
		catch (Throwable t) {
			t.printStackTrace();

			return false;
		}

		return true;
	}

	private static class Checker extends AbstractScheduledService {

		private boolean showMessage = true;
		@Override
		protected void runOneIteration() throws Exception {
			if (!testInet("https://www.google.com/")) {
				System.out.println("Disconnected, reconnecting.");

				try {
					post();
					showMessage = true;
				}
				catch (Throwable t) {
					t.printStackTrace();
				}
			}
			else {
				if (showMessage)
				{
					System.out.println("Still connected");
					showMessage = false;
				}
			}
		}

		@Override
		protected Scheduler scheduler() {
			return Scheduler.newFixedDelaySchedule(1, 5, TimeUnit.SECONDS);
		}

	}

}