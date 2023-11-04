package org.example.models.lookupResults;

import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LookupResults {
	private String cryptoCurrency;
	private BigDecimal exchangeRate;
	private Float percentageChange;
}
