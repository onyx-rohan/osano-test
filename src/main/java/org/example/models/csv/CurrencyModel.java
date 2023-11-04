package org.example.models.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class CurrencyModel {
	@CsvBindByName(column = "CurrencyCode")
	private String currencyCode;

	@CsvBindByName(column = "CurrencyName")
	private String currencyName;
}
