package ui;

import java.util.List;

public class Group {
    /**
     * Represents a Group in the database.
     */
    private List<String> emails = null;
    private String name = null;

    public Group(String name, List<String> emails) {
        this.emails = emails;
        this.name = name;
    }

    public List<String> getEmails() {
        return emails;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Group)
            return name.equals(((Group)obj).name);
        return super.equals(obj);
    }
}
