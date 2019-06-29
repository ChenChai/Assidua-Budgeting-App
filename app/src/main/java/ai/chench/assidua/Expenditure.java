package ai.chench.assidua;

import java.util.Date;

public class Expenditure {
    private float value;
    private String name;
    private Date date;

    public Expenditure(String name, float value, Date date) {
        this.name = name;
        this.value = value;
        this.date = date;
    }

    public float getValue() {
        return value;
    }
    public void setValue(float value) {
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