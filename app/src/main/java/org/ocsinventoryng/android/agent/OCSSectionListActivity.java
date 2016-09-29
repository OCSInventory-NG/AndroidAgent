/*
 * Copyright 2013-2016 OCSInventory-NG/AndroidAgent contributors : mortheres, cdpointpoint,
 * Cédric Cabessa, Nicolas Ricquemaque, Anael Mobilia
 *
 * This file is part of OCSInventory-NG/AndroidAgent.
 *
 * OCSInventory-NG/AndroidAgent is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * OCSInventory-NG/AndroidAgent is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OCSInventory-NG/AndroidAgent. if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.ocsinventoryng.android.agent;

import android.app.ListActivity;
import android.os.Bundle;

import org.ocsinventoryng.android.actions.Inventory;
import org.ocsinventoryng.android.actions.OCSLog;
import org.ocsinventoryng.android.sections.OCSSection;

import java.util.ArrayList;

public class OCSSectionListActivity extends ListActivity {

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        OCSLog ocslog = OCSLog.getInstance();

        Bundle b = getIntent().getExtras();
        if (b == null) {
            ocslog.debug("OCSSectionListActivity bundle null");
            return;
        }
        CharSequence section = b.getCharSequence("ocsinventory.section").toString();

        this.setTitle(section);

        // recuperation de la section
        ArrayList<OCSSection> asl = (ArrayList<OCSSection>) Inventory.getInstance(this).
                getSections(section.toString());
        if (asl == null) {
            return;
        }

        // Creation de l'adapteur avec la liste des sections
        SectionAdapter adapter = new SectionAdapter(this, asl);
        setListAdapter(adapter);
    }
} 