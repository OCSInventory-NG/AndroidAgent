package org.ocsinventoryng.android.sections;

import java.util.ArrayList;

public interface OCSSectionInterface {
    String getSectionTag();

    ArrayList<OCSSection> getSections();

    String toString();

    String toXML();
}
