package com.hes.easysales.easysales;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by sinopsys on 2/18/18.
 */

public class MainActivity extends AppCompatActivity {

    private FetchData process;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button click = findViewById(R.id.buttonDownload);
        process = new FetchData(this);

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                process.execute();
            }
        });
    }
}


// EOF
