package com.fivespecial.ploking.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.fivespecial.ploking.R;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

import static android.nfc.NdefRecord.createMime;

public class TrashActivity extends AppCompatActivity{

    NfcAdapter mNfcAdapter;
    TextView textView;
    PendingIntent mPendingIntent;

    public static final int TYPE_TEXT = 1;
    public static final int TYPE_URI = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mPendingIntent = PendingIntent.getActivity(this,0,intent,0);
    }

    @Override

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent == null)
            return;

        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    }

    private NdefMessage getTextAsNdef() {
        byte[] textBytes = new byte[]{0, 1, 2, 3, 4};
        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                "text/plain".getBytes(), new byte[] {}, textBytes);

        return new NdefMessage(new NdefRecord[] {textRecord});
    }

    private NdefMessage getUriAsNdef() {
        byte[] textBytes = new byte[]{0, 1, 2, 3, 4};
        NdefRecord record1 = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                new String("U").getBytes(Charset.forName("US-ASCII")), new byte[0], textBytes);
        return null;
    }

    private void toast(String text) {
        Log.i("fureun","toast");
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
