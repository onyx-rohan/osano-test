package org.example.views.currencylookup;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import org.example.models.csv.CurrencyModel;
import org.example.models.lookupResults.LookupResults;
import org.example.services.DataService;
import org.example.views.MainLayout;
import org.json.JSONArray;
import org.json.JSONObject;

@PageTitle("Currency Lookup")
@Route(value = "currency-lookup", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class CurrencyLookupView extends VerticalLayout {

	private final DataService dataService;

	private ComboBox<CurrencyModel> currencySelector;
	private Grid<LookupResults> resultsGrid;
	private TextField potentialEarnings;

	private List<LookupResults> lookupResults;
	private List<String> cryptoCurrencies;
	private JSONObject exchangeRates;
	private JSONArray tradingPairs;

	private List<CurrencyModel> currencies;

	public CurrencyLookupView() {
		dataService = new DataService();
		lookupResults = new ArrayList<>();
		cryptoCurrencies = new ArrayList<>();

		resultsGrid = new Grid<>(LookupResults.class, false);
		resultsGrid.addColumn(LookupResults::getCryptoCurrency).setHeader("Cryptocurrency Code");
		resultsGrid.addColumn(LookupResults::getExchangeRate).setHeader("Current Value in Selected FIAT");
		resultsGrid.addColumn(LookupResults::getPercentageChange).setHeader("Percentage Changed % vs BTC");
		resultsGrid.setItems(lookupResults);

		resultsGrid.setEnabled(false);
		resultsGrid.setVisible(false);

		potentialEarnings = new TextField();
		potentialEarnings.setEnabled(false);
		potentialEarnings.setVisible(false);

		currencySelector = new ComboBox<>("Currency");
		try {
			currencies = dataService.getCurrencies();
			getTradingPairs();

			currencySelector.setItems(currencies);
			currencySelector.setItemLabelGenerator(CurrencyModel::getCurrencyCode);
			currencySelector.setTooltipText("Select an ISO-4217 Currency to Lookup");
			currencySelector.setHelperText("ISO-4217 Currency Dropdown");
			currencySelector.addValueChangeListener(event -> {
				if(currencySelector.getValue() != null) {
					getExchangeRates(currencySelector.getValue());
					identifyBTCTradingPairs();
					updateResultsGrid();
					calculatePotentialEarnings();
				} else {
					resultsGrid.setEnabled(false);
					resultsGrid.setVisible(false);
					potentialEarnings.setEnabled(false);
					potentialEarnings.setVisible(false);
				}
			});

			this.setMargin(true);

			HorizontalLayout horizontalLayout = new HorizontalLayout();
			horizontalLayout.setWidthFull();
			horizontalLayout.setAlignItems(Alignment.END);
			horizontalLayout.setDefaultVerticalComponentAlignment(Alignment.STRETCH);
			horizontalLayout.add(currencySelector, potentialEarnings);

			this.add(horizontalLayout, resultsGrid);
		} catch (Exception e) {
			this.add(new Paragraph(e.getMessage()));
		}
	}

	private void getTradingPairs() {
		Logger.getGlobal().info("Fetching the Trading Pairs...");
		try {
			tradingPairs = dataService.getTradingPairs();
		} catch (Exception e) {
			this.add(new Paragraph(e.getMessage()));
		}
	}

	private void getExchangeRates(CurrencyModel currency) {
		Logger.getGlobal().info("Fetching the Exchange Rates...");
		try {
			exchangeRates = dataService.getExchangeRates(currency.getCurrencyCode());
			identifyCryptoCurrencies();
		} catch (Exception e) {
			this.add(new Paragraph(e.getMessage()));
		}
	}

	private void identifyBTCTradingPairs() {
		Logger.getGlobal().info("Identifying BTC Trading Pairs Intersected with Our Cryptocurrencies...");
		JSONArray newTradingPair = new JSONArray();

		for(int i=0; i < tradingPairs.length(); i++) {
			JSONObject tradingPairObj = tradingPairs.getJSONObject(i);
			if(tradingPairObj.has("symbol")) {
				if(tradingPairObj.getString("symbol").toUpperCase(Locale.ROOT).contains("BTC")) {
					String symbol = tradingPairObj.getString("symbol").toUpperCase(Locale.ROOT);
					String pair = symbol.replaceFirst("BTC", "");
					if(cryptoCurrencies.contains(pair)) {
						newTradingPair.put(tradingPairObj);
					}
				}
			}
		}

		tradingPairs = newTradingPair;
	}

	private void identifyCryptoCurrencies() {
		Logger.getGlobal().info("Identifying Cryptocurrencies from Exchange Rates...");
		JSONObject rates = exchangeRates.getJSONObject("data").getJSONObject("rates");
		List<String> currencyCodes = currencies.stream()
			.map(CurrencyModel::getCurrencyCode)
			.map(String::toUpperCase)
			.toList();
		cryptoCurrencies = rates.keySet().stream()
			.filter(key -> !currencyCodes.contains(key))
			.toList();
	}

	private void updateResultsGrid() {
		Logger.getGlobal().info("Updating and Showing the Results Grid...");
		lookupResults = new ArrayList<>();
		JSONObject rates = exchangeRates.getJSONObject("data").getJSONObject("rates");
		for(int i=0; i < tradingPairs.length(); i++) {
			JSONObject tradingPairObj = tradingPairs.getJSONObject(i);
			String symbol = tradingPairObj.getString("symbol").toUpperCase(Locale.ROOT);
			String cryptoCurrency = symbol.replaceFirst("BTC", "");
			BigDecimal currentExRateValue = new BigDecimal(rates.getString(cryptoCurrency));
			Float percentageChange = Float.parseFloat(tradingPairObj.getString("priceChangePercent"));

			LookupResults newResult = new LookupResults();
			newResult.setCryptoCurrency(cryptoCurrency);
			newResult.setExchangeRate(currentExRateValue);
			newResult.setPercentageChange(percentageChange);

			lookupResults.add(newResult);
		}

		resultsGrid.setItems(lookupResults);
		resultsGrid.setEnabled(true);
		resultsGrid.setVisible(true);
	}

	private void calculatePotentialEarnings() {
		Logger.getGlobal().info("Calculating the Potential Earnings...");
		CurrencyModel currency = currencySelector.getValue();
		JSONObject BTCUSDCPair = new JSONObject();
		try {
			BTCUSDCPair = dataService.getTradingPair("BTCUSDC");
		} catch (Exception e) {
			this.add(new Paragraph(e.getLocalizedMessage()));
		}

		BigDecimal BTCUSDCPrevValue = BTCUSDCPair.getBigDecimal("prevClosePrice");
		BigDecimal currExRateToUSD = exchangeRates.getJSONObject("data")
			.getJSONObject("rates")
			.getBigDecimal("USD");

		potentialEarnings.setLabel("Potential Money <" + currency.getCurrencyCode() + ">");
		potentialEarnings.setTooltipText("Potential Money you would have right now if you had converted one Bitcoin 24 hours"
			+ " ago into " + currency.getCurrencyCode());
		potentialEarnings.setValue(BTCUSDCPrevValue.multiply(currExRateToUSD).toString());
		potentialEarnings.setReadOnly(true);
		potentialEarnings.setEnabled(true);
		potentialEarnings.setVisible(true);
	}
}
