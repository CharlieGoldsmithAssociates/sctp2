package org.cga.sctp.mis.targeting;

/**
 * Targeting formula
 */
public class Criteria {
    private String name;
    private boolean active;
    private String formula;

    public Criteria() {
    }

    public Criteria(String name, String formula, boolean active) {
        this.name = name;
        this.formula = formula;
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }
}
