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
package org.ocsinventoryng.android.agent.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.ocsinventoryng.android.agent.R;

public class OCSListActivity extends ListActivity {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String[] sections = getResources().getStringArray(R.array.array_sections);
        Log.d("OCSListActivity", "onCreate ");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_liste_view, sections);

        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);

        Log.d("OCSListActivity", "item " + item);
        Bundle b = new Bundle();
        b.putString("ocsinventory.section", item);
        Intent intent = new Intent(this, OCSSectionListActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }
} 