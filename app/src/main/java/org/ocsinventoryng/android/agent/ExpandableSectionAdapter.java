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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.ocsinventoryng.android.sections.OCSSection;

import java.util.HashMap;
import java.util.List;

public class ExpandableSectionAdapter extends BaseExpandableListAdapter {
    // Context
    private Context myContext;
    // <Name, Content of Sections>
    private HashMap<String, List<OCSSection>> myDatas;

    public ExpandableSectionAdapter(Context myContext, HashMap<String, List<OCSSection>> myDatas) {
        this.myContext = myContext;
        this.myDatas = myDatas;
    }

    /**
     * Number of Sections
     *
     * @return int
     */
    @Override
    public int getGroupCount() {
        return myDatas.size();
    }

    /**
     * Number of details on a specific section
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return myDatas.get(getGroup(groupPosition)).size();
    }

    /**
     * Get section name
     * @return String
     */
    @Override
    public Object getGroup(int groupPosition) {
        return myDatas.keySet().toArray()[groupPosition];
    }

    /**
     * Get details of a Section
     * @return Section
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return myDatas.get(getGroup(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Display section title
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View maView = convertView;

        // Get this group title
        String headerTitle = (String) getGroup(groupPosition);

        if (maView == null) {
            LayoutInflater monLayoutInflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            maView = monLayoutInflater.inflate(R.layout.list_header, parent, false);
        }

        // Display it :-)
        TextView lblListHeader = (TextView) maView.findViewById(R.id.list_header_name);
        lblListHeader.setText(headerTitle);

        return maView;
    }

    /**
     * Display section details
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View maView = convertView;

        // Get part of details of this section
        final OCSSection maSection = (OCSSection) getChild(groupPosition, childPosition);

        if (maView == null) {
            LayoutInflater monLayoutInflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            maView = monLayoutInflater.inflate(R.layout.list_item, parent, false);
        }

        // Display it :-)
        TextView itemName = (TextView) maView.findViewById(R.id.list_item_name);
        TextView itemDetail = (TextView) maView.findViewById(R.id.list_item_detail);

        itemName.setText(maSection.getTitle());
        itemDetail.setText(maSection.toString());
        return maView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
