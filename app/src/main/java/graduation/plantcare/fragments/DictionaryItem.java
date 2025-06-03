package graduation.plantcare.fragments;
public class DictionaryItem {
    private final int iconResId;
    private final String name;
    private final String description;

    public DictionaryItem(int iconResId, String name, String description) {
        this.iconResId = iconResId;
        this.name = name;
        this.description = description;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
