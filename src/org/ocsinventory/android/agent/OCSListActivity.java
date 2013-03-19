package org.ocsinventory.android.agent;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class OCSListActivity extends ListActivity {
	String[] sections; 
	
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    sections = getResources().getStringArray(R.array.array_sections);
 
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        R.layout.simple_liste_view, sections);

    setListAdapter(adapter);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
		String item = (String) getListAdapter().getItem(position);
		// Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
		
		Bundle b = new Bundle();
		b.putString("ocsinventory.section", item);  
		Intent intent = new Intent(this, OCSSectionListActivity.class);
		intent.putExtras(b);
		startActivity(intent);
  }
} 