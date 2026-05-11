package shopeasy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Task 1 – Specification-Based Testing (Chapter 2)
 *
 * <p>Target class: {@link PriceCalculator}
 *
 * <p>Your goal is to test {@code PriceCalculator.calculate(basePrice, discountRate, taxRate)}
 * using the domain testing technique from Chapter 2:
 * <ol>
 *   <li>Identify equivalence partitions for each input dimension.</li>
 *   <li>Identify boundary values between partitions (on-point / off-point).</li>
 *   <li>Write at least 10 meaningful test cases that cover both partitions and boundaries.</li>
 *   <li>Use {@code @ParameterizedTest} with {@code @CsvSource} for tests that share structure.</li>
 *   <li>Add a comment above each test method explaining which partition or boundary it covers.</li>
 * </ol>
 *
 * <h3>Input dimensions to consider</h3>
 * <ul>
 *   <li><b>basePrice</b>  – zero, positive, very large</li>
 *   <li><b>discountRate</b> – 0 (no discount), (0,100) typical, 100 (full discount)</li>
 *   <li><b>taxRate</b>    – 0 (no tax), (0,100) typical, 100 (100% tax)</li>
 * </ul>
 */
class PriceCalculatorSpecTest {

    private PriceCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new PriceCalculator();
    }

    // -----------------------------------------------------------------------
    // TODO: Write your tests below.
    // -----------------------------------------------------------------------
    
    @Test
    void zeroPriceAlwaysReturnsZero() {
        assertThat(calculator.calculate(0, 20, 10)).isEqualTo(0.0);
    }
    /** Partition: no discount always equal to base price —  result must always be 0 */
    @Test
    void noDiscountAlwaysReturnsBasePrice() {
        assertThat(calculator.calculate(100, 0, 0)).isEqualTo(100.0);
    }
    /** Partition: no discount and a valid tax rate —  result must always be tax applied to base price */
    @Test
    void noDiscountAndValidTaxRate() {
        assertThat(calculator.calculate(100, 0, 10)).isEqualTo(110.0);
    }

    /** Invalid Boundary: negative base price should throw an exception */
    @Test
    void invalidBasePriceShouldThrow() {
        assertThatThrownBy(() -> calculator.calculate(-10, 20, 10))
                .isInstanceOf(AssertionError.class);
    }

    /** Invalid Boundary: negative and overflow discount rates should throw an exception */
    @Test
    void invalidDiscountRateShouldThrow() {
        assertThatThrownBy(() -> calculator.calculate(100, -10, 10))
                    .isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> calculator.calculate(100, 110, 10))
                .isInstanceOf(AssertionError.class);
    }

    /** Invalid Boundary: negative and overflow tax rates should throw an exception */
    @Test
    void invalidTaxRateShouldThrow() {
        assertThatThrownBy(() -> calculator.calculate(100, 20, -5))
                .isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> calculator.calculate(100, 20, 150))
                .isInstanceOf(AssertionError.class);
    }

    /** Boundary: discountRate at upper bound (100%) — full discount wipes price to 0 */
    @Test
    void discountRateHundredMeansFullDiscount() {
        double result = calculator.calculate(100, 100, 0);
        assertThat(result).isEqualTo(0.0);
    }

    /** Boundary: taxRate at lower bound (0%) — no tax applied */
    @Test
    void zeroTaxMeansNoTaxApplied() {
        double result = calculator.calculate(100, 20, 0);
        assertThat(result).isEqualTo(80.0);
    }

    /** Boundary: taxRate at upper bound (100%) — full tax applied */
    @Test
    void hundredTaxMeansFullTaxApplied() {
        double result = calculator.calculate(100, 20, 100);
        assertThat(result).isEqualTo(160.0);
    }

    /** Partition: typical values — check formula correctness */
    @ParameterizedTest(name = "base={0}, disc={1}%, tax={2}% => {3}")
    @CsvSource({
        "100.0, 10.0, 20.0, 108.0",
        "200.0,  0.0, 10.0, 220.0",
        "50.0, 50.0, 50.0, 37.5",
    })
    void typicalValues(double base, double disc, double tax, double expected) {
        assertThat(calculator.calculate(base, disc, tax)).isCloseTo(expected, within(0.001));
    }
}
