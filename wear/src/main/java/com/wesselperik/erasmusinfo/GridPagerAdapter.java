package com.wesselperik.erasmusinfo;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.view.Gravity;

import java.util.ArrayList;

/**
 * Created by Wessel on 7-3-2016.
 */
public class GridPagerAdapter extends FragmentGridPagerAdapter {

    private static Row row1;
    private final Context mContext;
    private ArrayList<Row> mPages;
    //Row row1;

    public GridPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        initPages();
    }

    private void initPages() {
        mPages = new ArrayList<Row>();

        row1 = new Row();
        row1.addPages(new Page("Laden...", "De data wordt opgehaald"));
        /*row1.addPages(new Page("Mededeling", "Canterbury/Luik\n" +
                "Indeling voor beide schooltrips\n" +
                "zie mededelingenbord hal.\n" +
                "Vanwege de over-inschrijving is een\n" +
                "aantal leerlingen op een wachtlijst\n" +
                "geplaatst.\n" +
                "(Hr.Schonewille)"));
        row1.addPages(new Page("Rooster Maandag", "Hr. Vos afwezig\n" +
                "3HD 3e uur Lok. 39\n" +
                "2VA Hr. Halman 5e uur Lok. 28\n" +
                "                i.p.v. 1e uur\n" +
                "Mevr. Dekkers 5e t/m 7e uur vervalt\n" +
                "3VB Hr. Vaanhold 1e uur Ec Lok. 24\n" +
                "2HC Hr. de Vries 2e uur Ec Lok. 24\n" +
                "3GV Hr. Vaanhold 4e uur Ec Lok. 24\n" +
                "Hr. Vaanhold 5e uur lok. 24\n" +
                "Hr. Bolk 5e uur lok. 16\n" +
                "4HE en 4HF 6e uur Zoldarium(S48)"));*/

        /*Row row2 = new Row();
        row2.addPages(new Page("Title3", "Text3", R.mipmap.ic_launcher));

        Row row3 = new Row();
        row3.addPages(new Page("Title4", "Text4", R.mipmap.ic_launcher));

        Row row4 = new Row();
        row4.addPages(new Page("Title5", "Text5", R.mipmap.ic_launcher));
        row4.addPages(new Page("Title6", "Text6", R.mipmap.ic_launcher));*/

        mPages.add(row1);
        /*mPages.add(row2);
        mPages.add(row3);
        mPages.add(row4);*/
    }

    public static void addPage(String title, String text){
        row1.addPages(new Page(title, text));
    }

    public static void removePages(){
        row1.removePages();
    }

    @Override
    public Fragment getFragment(int row, int col) {
        Page page = ((Row)mPages.get(row)).getPages(col);
        CardFragment fragment = CardFragment.create(page.mTitle, page.mText);
        return fragment;
    }

    @Override
    public int getRowCount() {
        return mPages.size();
    }

    @Override
    public int getColumnCount(int row) {
        return mPages.get(row).size();
    }
}
