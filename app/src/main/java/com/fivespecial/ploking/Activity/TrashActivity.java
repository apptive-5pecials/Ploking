package com.fivespecial.ploking.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.fivespecial.ploking.R;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

public class TrashActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

    }

    @Override
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        Tag tag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if(tag != null) {
            Log.i("hey", Arrays.toString(tag.getTechList()));
            MifareClassic mfc = MifareClassic.get(tag) ;

            try {
                mfc.connect();
                boolean authB = mfc.authenticateSectorWithKeyB(2, MifareClassic.KEY_DEFAULT) ;
                Log.i("hey", "authB : " + authB) ;

                if (authB) {
                    byte[] bWrite = new byte[16];
                    for(int i=0; i!=16; ++i) bWrite[i] = (byte)i;
                    byte[] hello = "hello".getBytes(StandardCharsets.US_ASCII);
                    //System.arraycopy(hello, 0, bWrite, 0, hello.length);
                    mfc.writeBlock(11, bWrite);
                    Log.i("hey", "write : " + Arrays.toString(bWrite));

                    byte[] bRead = mfc.readBlock(11);
                    String str = new String(bRead, StandardCharsets.US_ASCII);
                    Log.i("hey", "read bytes : " + Arrays.toString(bRead));
                    Log.i("hey", "read string : " + str);
                    Toast.makeText(this, "read : " + str, Toast.LENGTH_SHORT).show();
                    Log.i("hey", "expected : " + new String(bWrite, StandardCharsets.US_ASCII));
                }

                mfc.close();

            } catch (IOException e) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                Log.i("hey", "Error") ;
            }
        }
    }

}
