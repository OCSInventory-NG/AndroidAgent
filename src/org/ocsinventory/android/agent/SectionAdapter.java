package org.ocsinventory.android.agent;

import java.util.List;

import org.ocsinventory.android.sections.OCSSection;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SectionAdapter extends BaseAdapter  {
	
    private List<OCSSection> mListe;
    private LayoutInflater mInflater;
    private Context mContext;
	
    public SectionAdapter(Context context, List<OCSSection> aListP){
        mContext = context;
        mListe = aListP;
        mInflater = LayoutInflater.from(mContext);
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

		if(convertView == null){
			// Initialisation avec le layout personnalisé
			layoutItem = (LinearLayout) mInflater.inflate(R.layout.section_item,
	                		parent, false);
		}
		else{
			layoutItem = (LinearLayout) convertView;
		}
	        
		TextView tvTitre = (TextView) layoutItem.findViewById(R.id.titre);
		TextView tvDetail = (TextView) layoutItem.findViewById(R.id.detail);

	    OCSSection s = (OCSSection) mListe.get(position);    
		tvTitre.setText(s.getTitle());
		tvDetail.setText(s.toString());
	        
        return layoutItem;
	}
}
