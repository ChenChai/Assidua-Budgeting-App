package ai.chench.assidua;

import java.math.BigDecimal;
import java.util.Date;

public class Expenditure {
    private BigDecimal value;
    private String name;
    private Date date;

    public Expenditure(String name, BigDecimal value, Date date) {
        this.name = name;
        this.value = value;
        this.date = date;
    }

    public BigDecimal getValue() {
        return value;
    }
    public void setValue(BigDecimal value) {
        this.value = value;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
}