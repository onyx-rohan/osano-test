package org.example.services;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.flow.server.HttpStatusCode;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.NoArgsConstructor;
import org.example.models.csv.CurrencyModel;
import org.json.JSONArray;
import org.json.JSONObject;

@NoArgsConstructor
public class DataService {
	private static final String CURRENCIES_FILE = "currencies.csv";
	private static final String BINANCE_API_URL = "https://api.binance.us/api/v3/ticker/24hr";
	private static final String COINBASE_API_URL = "https://api.coinbase.com/v2/exchange-rates?currency=";

	public List<CurrencyModel> getCurrencies() throws IOException, CsvValidationException {
		File csvFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(CURRENCIES_FILE)).getFile());
		CSVReader reader = new CSVReader(new FileReader(csvFile));

		List<CurrencyModel> currencies = new ArrayList<>();

		String[] record;
		while((record = reader.readNext()) != null) {
			CurrencyModel currency = new CurrencyModel();
			currency.setCurrencyCode(record[0]);
			currency.setCurrencyName(record[1]);
			currencies.add(currency);
		}

		reader.close();
		return currencies;
	}

	public JSONObject getExchangeRates(String currencyCode) throws URISyntaxException, IOException, InterruptedException {
		return new JSONObject(callAPI(COINBASE_API_URL + currencyCode));
	}

	public JSONObject getTradingPair(String pair) throws URISyntaxException, IOException, InterruptedException {
		return new JSONObject(callAPI(BINANCE_API_URL + "?symbol=" + pair));
	}

	public JSONArray getTradingPairs() throws URISyntaxException, IOException, InterruptedException {
		return new JSONArray(callAPI(BINANCE_API_URL));
	}

	private String callAPI(String api) throws URISyntaxException, IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
			.uri(new URI(api))
			.GET()
			.build();

		HttpResponse<String> response = HttpClient.newHttpClient()
			.send(request, HttpResponse.BodyHandlers.ofString());

		if(response.statusCode() == HttpStatusCode.OK.getCode()) {
			return response.body();
		} else {
			return new JSONObject().append("error", response.body()).toString();
		}
	}
}
