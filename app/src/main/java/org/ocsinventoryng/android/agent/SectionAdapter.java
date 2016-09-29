/*
 * Copyright 2013-2016, OCSInventory-NG/AndroidAgent contributors
 *
 * This file is part of OCSInventory-NG/AndroidAgent.
 *
 * OCSInventory-NG/AndroidAgent is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OCSInventory-NG/AndroidAgent is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OCSInventory-NG/AndroidAgent. If not, see <http://www.gnu.org/licenses/>
 */
package org.ocsinventoryng.android.agent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ocsinventoryng.android.sections.OCSSection;

import java.util.List;

public class SectionAdapter extends BaseAdapter {

    private List<OCSSection> mListe;
    private LayoutInflater mInflater;

    public SectionAdapter(Context context, List<OCSSection> aListP) {
        mListe = aListP;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mListe.size();
    }

    @Override
    public Object getItem(int position) {
        return mListe.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layoutItem;

        if (convertView == null) {
            // Initialisation avec le layout personnalisé
            layoutItem = (LinearLayout) mInflater.inflate(R.layout.section_item, parent, false);
        } else {
            layoutItem = (LinearLayout) convertView;
        }

        TextView tvTitre = (TextView) layoutItem.findViewById(R.id.titre);
        TextView tvDetail = (TextView) layoutItem.findViewById(R.id.detail);

        OCSSection s = mListe.get(position);
        tvTitre.setText(s.getTitle());
        tvDetail.setText(s.toString());

        return layoutItem;
    }
}
