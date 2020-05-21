package it.unimib.quakeapp.models;

public class Country {

    private String code;
    private String name;
    private boolean selected;

    public Country() {
        setCode(null);
        setName(null);
        setSelected(false);
    }

    public Country(String code, String name, boolean selected) {
        setCode(code);
        setName(name);
        setSelected(selected);
    }

    private void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    private void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return this.selected;
    }
}
