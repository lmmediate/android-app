package com.hes.easysales.easysales;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Created by sinopsys on 2/18/18.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new FetchData(this).execute();
    }
}


// EOF
