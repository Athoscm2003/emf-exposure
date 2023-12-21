// Arquivo main

package com.carisio.apps.exposurebasestationradiation;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.carisio.apps.exposurebasestationradiation.util.AppProperties;
import com.carisio.apps.exposurebasestationradiation.util.MapDrawing;
import com.carisio.apps.exposurebasestationradiation.util.ProjectDatabase;
import com.carisio.apps.exposurebasestationradiation.util.UtilView;
import com.carisio.apps.exposurebasestationradiation.util.mapstates.AddBaseStationState;
import com.carisio.apps.exposurebasestationradiation.util.mapstates.AddBoxState;
import com.carisio.apps.exposurebasestationradiation.util.mapstates.AddProbeState;
import com.carisio.apps.exposurebasestationradiation.util.mapstates.DeleteBaseStationState;
import com.carisio.apps.exposurebasestationradiation.util.mapstates.EditBaseStationState;
import com.carisio.apps.exposurebasestationradiation.util.mapstates.MapState;
import com.carisio.apps.exposurebasestationradiation.util.mapstates.WaitingUserState;

public class MainActivity extends FragmentActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ExpandableListView drawerList;
    
    private Menu savedMenu;
    private boolean addBSChecked;
    private boolean editBSChecked;
    private boolean deleteBSChecked;
    private boolean addProbeChecked;
    private boolean addBoxChecked;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppProperties.loadProperties(this);

		setContentView(R.layout.main_activity);
		
		prepareNavigationDrawer();
		
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
            this,                  /* host Activity */
            drawerLayout,         /* DrawerLayout object */
            R.string.open_drawer_description,  /* "open drawer" description for accessibility */
            R.string.close_drawer_description /* "close drawer" description for accessibility */
            ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);
        
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment == null) {
        	showMapFragment();
        } else {
        	showFragment(fragment);
        }

        showDisclaimer();
	}

	private void showDisclaimer() {
		if (AppProperties.isDontShowThisMessageAgain())
			return;

		View chkShowAgainView = View.inflate(this, R.layout.disclaimer_startup, null);
		final CheckBox chk = (CheckBox)chkShowAgainView.findViewById(R.id.dont_show_again);

		AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
				.setTitle(getString(R.string.disclaimer))
				.setMessage(getString(R.string.disclaimer_text) + "\n\n" + getString(R.string.disclaimer_overstimate))
				.setView(chkShowAgainView)
				.setCancelable(false)
				.setPositiveButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						AppProperties.setDontShowThisMessageAgain(chk.isChecked());
						AppProperties.saveProperties(getApplicationContext());
					}
				})
				.setIcon(android.R.drawable.ic_dialog_alert)
				.show();
		UtilView.applyAlertDialogStyle(dialog);
	}

	public void showMapFragment() {
		showFragment(new ShowMapsFragment());
	}
	public void showFragment(Fragment f) {
		Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
		if (fragment != null && fragment.getClass().equals(f.getClass()))
			return;
			
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.content_frame, f);
		transaction.addToBackStack(null);
		transaction.commit();
		
        drawerLayout.closeDrawer(drawerList);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu newMenu) {
		this.savedMenu = newMenu;
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.action_bar_menu, newMenu);

	    setActionButtonIcons();
    	return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
		// if this happens, just return true (this behavior is consumed by
		// mDrawerToggle::onOptionsItemSelected)
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
       
		switch (item.getItemId()) {
			case R.id.action_button_add_base_station:
				addBSEvent();
				break;
			case R.id.action_button_add_probe:
				addProbeEvent();
				break;
			case R.id.action_button_add_box:
				addBoxEvent();
				break;
       }
       // Default
       return super.onOptionsItemSelected(item);
	}
	
	private void prepareNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        
        drawerList = (ExpandableListView) findViewById(R.id.left_drawer);
        drawerList.setGroupIndicator(null);
        
        drawerList.setAdapter(new NavigationDrawerAdapter());
        drawerList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
            	int groupCount = drawerList.getExpandableListAdapter().getGroupCount();
                for (int i = 0; i < groupCount; i++) {
                	if (groupPosition != i)
                		drawerList.collapseGroup(i);
                }
            }
        });
        drawerList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				NavigationDrawerAdapter adapter = (NavigationDrawerAdapter) drawerList.getExpandableListAdapter();
				String groupName = adapter.getGroupName(groupPosition);
				if (groupName.equals(getString(R.string.settings))) {
					openSettingsEvent();
				} else if (groupName.equals(getString(R.string.about))) {
					openAboutEvent();
				}
				return false;
			}
		});
        
        drawerList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
        	@Override
        	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        		NavigationDrawerAdapter adapter = (NavigationDrawerAdapter) drawerList.getExpandableListAdapter();
        		String childName = adapter.getChildName(groupPosition, childPosition);
        		String groupName = adapter.getGroupName(groupPosition);
        		
        		if (groupName.equals(getString(R.string.base_stations)) && childName.equals(getString(R.string.add))) {
        			addBSEvent();
        		} else if (groupName.equals(getString(R.string.base_stations)) && childName.equals(getString(R.string.edit))) {
        			editBSEvent();
        		} else if (groupName.equals(getString(R.string.base_stations)) && childName.equals(getString(R.string.delete))) {
        			deleteBSEvent();
    			} else if (groupName.equals(getString(R.string.base_stations)) && childName.equals(getString(R.string.delete_all))) {
    				deleteAllBSEvent();
    			} else if (groupName.equals(getString(R.string.probe)) && childName.equals(getString(R.string.set))) {
    				addProbeEvent();
    			}  else if (groupName.equals(getString(R.string.probe)) && childName.equals(getString(R.string.delete))) {
    				deleteProbeEvent();
    			} else if (groupName.equals(getString(R.string.box)) && childName.equals(getString(R.string.set))) {
    				addBoxEvent();
    			} else if (groupName.equals(getString(R.string.box)) && childName.equals(getString(R.string.delete))) {
    				deleteBoxEvent();
    			}
        		
        		drawerLayout.closeDrawer(GravityCompat.START);
        		return true;
        	}}
        );
	}
	
	private void deleteProbeEvent() {
		MapState.setCurrentState(new WaitingUserState(MainActivity.this));
		MapDrawing.removeProbe();
		showMapFragment();
	}
	private void deleteBoxEvent() {
		MapState.setCurrentState(new WaitingUserState(MainActivity.this));
		ProjectDatabase.deleteBox(MainActivity.this);
		MapDrawing.removeBox();
	}
	private void deleteAllBSEvent() {
		AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
	    .setTitle(getString(R.string.delete))
	    .setMessage(getString(R.string.are_you_sure_you_want_to_delete_all_bs))
	    .setCancelable(false)
	    .setPositiveButton(getString(R.string.yes_button), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
    			ProjectDatabase.deleteAllBaseStation(MainActivity.this);
    			MapDrawing.clearDrawing();
    			showMapFragment();
			}
		})
	    .setNegativeButton(getString(R.string.no_button), null)
	    .setIcon(android.R.drawable.ic_dialog_alert)
	    .show();
		UtilView.applyAlertDialogStyle(dialog);
		
		MapState.setCurrentState(new WaitingUserState(MainActivity.this));
	}
	private void openSettingsEvent() {
		MapState.setCurrentState(new WaitingUserState(MainActivity.this));
		showFragment(new PropertiesFragment());
	}
	private void openAboutEvent() {
		MapState.setCurrentState(new WaitingUserState(MainActivity.this));
		showFragment(new AboutFragment());
	}
	private void editBSEvent() {
		switchState(new EditBaseStationState(this), new WaitingUserState(this), editBSChecked);
	}
	private void deleteBSEvent() {
		switchState(new DeleteBaseStationState(this), new WaitingUserState(this), deleteBSChecked);
	}
	private void addBSEvent() {
		switchState(new AddBaseStationState(this), new WaitingUserState(this), addBSChecked);
	}
	private void addProbeEvent() {
		switchState(new AddProbeState(this), new WaitingUserState(this), addProbeChecked);
	}
	private void addBoxEvent() {
		switchState(new AddBoxState(this), new WaitingUserState(this), addBoxChecked);
	}
	private void switchState(MapState stateIfUnchecked, MapState stateIfChecked, boolean checked) {
		if (!checked) {
			MapState.setCurrentState(stateIfUnchecked);
		} else {
			MapState.setCurrentState(stateIfChecked);
		}
		drawerLayout.closeDrawer(GravityCompat.START);
	}
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
    	super.onPostCreate(savedInstanceState);
    	// Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void setTitle(CharSequence title) {
        getActionBar().setTitle(title);
    }

    public void highlightNothing() {
    	addBSChecked = false;
    	editBSChecked = false;
    	deleteBSChecked = false;
    	addProbeChecked = false;
    	addBoxChecked = false;
    	
    	setActionButtonIcons();
    	
    	((NavigationDrawerAdapter)drawerList.getExpandableListAdapter()).notifyDataSetChanged();
    }
    private void setActionButtonIcons() {
    	setActionButtonIcon(R.id.action_button_add_base_station, R.drawable.bs_selected, R.drawable.bs_unselected, addBSChecked);
    	setActionButtonIcon(R.id.action_button_add_probe, R.drawable.probe_selected, R.drawable.probe_unselected, addProbeChecked);
    	setActionButtonIcon(R.id.action_button_add_box, R.drawable.box_selected, R.drawable.box_unselected, addBoxChecked);    	
    }
    public void highlightEditBSInfo() {
    	editBSChecked = true;
    }
    public void highlightDeleteBSInfo() {
    	deleteBSChecked = true;
    }
    public void highlightAddBSInfo() {
    	addBSChecked = true;
    	setActionButtonIcon(R.id.action_button_add_base_station, R.drawable.bs_selected, R.drawable.bs_unselected, addBSChecked);
    }
    public void highlightAddProbeInfo() {
    	addProbeChecked = true;
    	setActionButtonIcon(R.id.action_button_add_probe, R.drawable.probe_selected, R.drawable.probe_unselected, addProbeChecked);
    }
    public void highlightAddBoxInfo() {
    	addBoxChecked = true;
    	setActionButtonIcon(R.id.action_button_add_box, R.drawable.box_selected, R.drawable.box_unselected, addBoxChecked);
    }
    private void setActionButtonIcon(int menuItemID, int selectedIconID, int unselectedIconID, boolean selected) {
    	MenuItem menuItem = savedMenu.findItem(menuItemID);
    	Drawable icon = ContextCompat.getDrawable(this, selected ? selectedIconID : unselectedIconID);
    	menuItem.setIcon(icon);
    }    
    class NavigationDrawerAdapter extends BaseExpandableListAdapter {
    	private String[] navDrawerOptions;
    	private String[] navDrawerBaseStationSection;
    	private String[] navDrawerProbeSection;
    	private String[] navDrawerBoxSection;
    	
    	private Map<Integer, String[]> groupPositionOptions;
    	
    	public NavigationDrawerAdapter() {
    		navDrawerOptions = getResources().getStringArray(R.array.nav_drawer_options);
    		navDrawerBaseStationSection = getResources().getStringArray(R.array.nav_drawer_base_station_options);
    		navDrawerProbeSection = getResources().getStringArray(R.array.nav_drawer_probe_options);
    		navDrawerBoxSection = getResources().getStringArray(R.array.nav_drawer_box_options);
        	
        	groupPositionOptions = new HashMap<Integer, String[]>();
        	
    		for (int i = 0; i < navDrawerOptions.length; i++) {
    			String s = navDrawerOptions[i];
    			
    			if (s.equals(getResources().getString(R.string.base_stations)))
    				groupPositionOptions.put(i, navDrawerBaseStationSection);
    			if (s.equals(getResources().getString(R.string.probe)))
    				groupPositionOptions.put(i, navDrawerProbeSection);
    			else if (s.equals(getResources().getString(R.string.box)))
    				groupPositionOptions.put(i, navDrawerBoxSection);
    		}
		}
    	
		@Override
		public int getGroupCount() {
			return navDrawerOptions.length;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			String[] options = groupPositionOptions.get(groupPosition);
			if (options == null)
				return 0;
			else
				return options.length;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return navDrawerOptions[groupPosition];
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			String[] options = groupPositionOptions.get(groupPosition);
			if (options == null)
				return null;
			else
				return options[childPosition];
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

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView text = (TextView) getLayoutInflater().inflate(R.layout.nav_drawer_sections, null);
			text.setText(navDrawerOptions[groupPosition]);
			return text;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			String groupName = navDrawerOptions[groupPosition];
			String childName = getChild(groupPosition, childPosition).toString();
			
			TextView view = (TextView) getLayoutInflater().inflate(R.layout.nav_drawer_final_options, null);
			view.setText(childName);
			
			// Only highlight ADD BS, EDIT BS, DELETE BS, ADD PROBE and ADD BOX
			boolean highlightItem = checkIfChildIsDesiredAndIsSelected(groupName, getString(R.string.base_stations), childName, getString(R.string.add), MainActivity.this.addBSChecked) ||
					checkIfChildIsDesiredAndIsSelected(groupName, getString(R.string.base_stations), childName, getString(R.string.edit), MainActivity.this.editBSChecked) ||
					checkIfChildIsDesiredAndIsSelected(groupName, getString(R.string.base_stations), childName, getString(R.string.delete), MainActivity.this.deleteBSChecked) ||
					checkIfChildIsDesiredAndIsSelected(groupName, getString(R.string.probe), childName, getString(R.string.set), MainActivity.this.addProbeChecked) ||
					checkIfChildIsDesiredAndIsSelected(groupName, getString(R.string.box), childName, getString(R.string.set), MainActivity.this.addBoxChecked);
			
			if (highlightItem) {
				view.setTypeface(null, Typeface.BOLD_ITALIC);
			}
			
			return view;
		}
		
		private boolean checkIfChildIsDesiredAndIsSelected(String groupName, String desiredGroupName, String childName, String desiredChildName, boolean itemSelected) {
			return groupName.equals(desiredGroupName) && childName.equals(desiredChildName) && itemSelected;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
		
		public String getGroupName(int groupPosition) {
			return navDrawerOptions[groupPosition];
		}
		
		public String getChildName(int groupPosition, int childPosition) {
			return groupPositionOptions.get(groupPosition)[childPosition];
		}
    }
}
