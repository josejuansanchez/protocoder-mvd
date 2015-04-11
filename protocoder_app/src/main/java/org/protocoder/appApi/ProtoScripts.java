/*
* Part of Protocoder http://www.protocoder.org
* A prototyping platform for Android devices
*
* Copyright (C) 2013 Victor Diaz Barrales victormdb@gmail.com
*
* Protocoder is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Protocoder is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with Protocoder. If not, see <http://www.gnu.org/licenses/>.
*/

package org.protocoder.appApi;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import org.protocoder.R;
import org.protocoder.projectlist.ProjectItem;
import org.protocoder.projectlist.ProjectListFragment;
import org.protocoder.projectlist.ProjectsPagerAdapter;
import org.protocoderrunner.apprunner.AppRunnerService;
import org.protocoderrunner.apprunner.api.PUtil;
import org.protocoderrunner.events.Events;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.utils.MLog;

import de.greenrobot.event.EventBus;

public class ProtoScripts {

    private static final String TAG = "ProtoScripts";
    private Protocoder mProtocoder;

    private int mProjectRequestCode = 1;
    private Intent currentProjectApplicationIntent;


    ProjectsPagerAdapter mProjectPagerAdapter;

    // fragments that hold the projects
    private ViewPager mViewPager;
    private TextView tProjects;
    private TextView tExamples;

    ProtoScripts(Protocoder protocoder) {
        this.mProtocoder = protocoder;
        init();
    }

    public void init() {


    }

    public java.util.ArrayList<Project> getProjectsInFolder(String folder) {
        return ProjectManager.getInstance().list(folder, true);
    }

    public void goTo(String folder) {
        int num = mProjectPagerAdapter.getFragmentNumByName(folder);
        mViewPager.setCurrentItem(num, true);
    }

    public void goTo(String folder, String appName) {
        goTo(folder);
        final ProjectListFragment plf = mProjectPagerAdapter.getFragmentByName(folder);
        final int id = plf.findAppPosByName(appName);

        PUtil util = new PUtil(mProtocoder.mContext);

        util.delay(200, new PUtil.delayCB() {
            @Override
            public void event() {
                plf.goTo(id);
            }
        });
    }

    public void highlight(String folder, String appName) {
        final ProjectListFragment plf = mProjectPagerAdapter.getFragmentByName(folder);
        ProjectItem view = (ProjectItem) getViewByName(folder, appName);
        int pos = plf.findAppPosByName(appName);

        plf.mProjectAdapter.mProjects.get(pos).selected = true;
        view.setHighlighted(true);
    }


    public void resetHighlighting(String folder) {
        final ProjectListFragment plf = mProjectPagerAdapter.getFragmentByName(folder);
        plf.resetHighlighting();
    }

    public View getViewByName(String folder, String appName) {
        final ProjectListFragment plf = mProjectPagerAdapter.getFragmentByName(folder);
        ProjectItem view = (ProjectItem) plf.getView(appName);

        int pos = plf.findAppPosByName(appName);
        //->
        plf.mProjectAdapter.mProjects.get(pos).selected = true;
        view.setHighlighted(true);

        return view;
    }


    public void goNext() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
    }

    public void goPrevious() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
    }


    public void addScriptList(int icon, String name, int color, boolean orderByName) {
        ProjectListFragment listFragmentBase = ProjectListFragment.newInstance(icon, name, color, orderByName);
        listFragmentBase.icon = icon;
        listFragmentBase.mProjectFolder = name;
        listFragmentBase.color = color;
        listFragmentBase.orderByName = orderByName;

        mProjectPagerAdapter.addFragment(name, listFragmentBase);
        mProjectPagerAdapter.notifyDataSetChanged();
    }

    public void refresh(String folder, String appName) {
        final ProjectListFragment plf = mProjectPagerAdapter.getFragmentByName(folder);
        plf.projectRefresh(appName);
    }

    public void rename(String folder, String appName) {

    }


    public void run(String folder, String appName) {
        //close app if open
        if (currentProjectApplicationIntent != null) {

            //cerramos servicio
            Events.ProjectEvent evt = new Events.ProjectEvent(null, "stop");
            EventBus.getDefault().post(evt);

            currentProjectApplicationIntent = null;
        }

        if (appName.toLowerCase().endsWith("service")) {
            currentProjectApplicationIntent = new Intent(mProtocoder.mContext, AppRunnerService.class);
            currentProjectApplicationIntent.putExtra(Project.FOLDER, folder);
            currentProjectApplicationIntent.putExtra(Project.NAME, appName);

            mProtocoder.mContext.startService(currentProjectApplicationIntent);
        } else {
            //open new activity
            try {

            } catch (Exception e) {
                MLog.d(TAG, "Error launching script");
            }
        }
    }

    //TODO
    public void createProject(String folder, String appName) {
        //create file
        Project newProject = ProjectManager.getInstance().addNewProject(mProtocoder.mContext, appName, folder, appName);

        //notify ui
        final ProjectListFragment plf = mProjectPagerAdapter.getFragmentByName(folder);
        plf.mProjects.add(newProject);
        plf.notifyAddedProject();
    }

    public void delete(String folder, String appName) {

    }

    public void createFileName(String folder, String appName, String fileName) {

    }

    public void deleteFileName(String folder, String appName, String fileName) {

    }

    public String exportProto(String folder, String fileName) {
        Project p = ProjectManager.getInstance().get(folder, fileName);
        String zipFilePath = ProjectManager.getInstance().createBackup(p);

        return zipFilePath;
    }



    public void listRefresh() {
        for (ProjectListFragment fragment : mProjectPagerAdapter.fragments) {
            fragment.refreshProjects();
        }
    }



    private String getFragmentTag(int position) {
        return "android:switcher:" + R.id.pager + ":" + position;
    }

    // public void reinitScriptList() {
    //  ProjectListFragment f0 = (ProjectListFragment) mProjectPagerAdapter.getItem(0);
    //  mProjectPagerAdapter.addFragment("", );

    // }
}
