package com.fivespecial.ploking.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.widget.Toast;

import com.fivespecial.ploking.R;
import com.google.common.base.Charsets;
import com.google.common.primitives.Bytes;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;


public class TrashActivity extends AppCompatActivity{

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;

    private String TAG = "mifdebug";



    public static final int TYPE_TEXT = 1;
    public static final int TYPE_URI = 2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);


        // NFC 관련 객체 생성
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
    }

    /***************************************************************
     * 여기서부턴 NFC 관련 메소드
     * NFC 단말에 태그가 인식되면, Intent를 통해서 Activity로 전달됩니다.
     * Activity가 이 Intent를 받기 위해서는 NfcAdapter 클래스의
     * enableForegroundDispatch(..) 를 이용합니다.
     **************************************************************/
    @Override
    protected void onPause() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    /*********************************************
     *  NFC 태그 스캔시 자동 호출되는 메소드
     *  ******************************************/
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null) {
            String s = "plokk0123456789012345plokingPLOKINGmk";

            // 감지된 태그를 가리키는 객체
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NdefMessage message = createTagMessage(s, TYPE_TEXT);
            writeTag(message, detectedTag);
        }
    }

//
//    public void writeMTag(Tag tag){
//        MifareClassic classic = MifareClassic.get(tag);
//        boolean auth;
//
//        try{
//            classic.connect();
////            byte[] data_a = new byte[]{(byte)0xFB, (byte)0xEA, (byte)0x8B, (byte)0x60, (byte)0x06,
////                    (byte)0xD8, (byte)0xAE, (byte)0xBF, (byte)0x06, (byte)0xD8, (byte)0xEA, (byte)0xFB};
////            classic.writeBlock(6, data_a);
//
//            auth = classic.authenticateSectorWithKeyA(1, MifareClassic.KEY_DEFAULT);
//
//            if(auth){
//                Log.d(TAG, "done?");
//                classic.writeBlock(6, "abcd".getBytes(Charset.forName("US-ASCII")));
//            }else{
//                Toast.makeText(getApplicationContext(), "auth is failed", Toast.LENGTH_SHORT).show();
//            }
//        } catch(Exception e){
//            Log.d(TAG, "IOException!!");
//            e.printStackTrace();
//        }
//        finally {
//            try{
//                classic.close();
//            }catch (IOException e){
//                e.printStackTrace();
//            }
//        }
//    }

    // 감지된 태그에 NdefMessage를 쓰는 메소드
    public boolean writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    return false;
                }

                if (ndef.getMaxSize() < size) {
                    return false;
                }

                ndef.writeNdefMessage(message);
                Toast.makeText(getApplicationContext(), "쓰기 성공!", Toast.LENGTH_SHORT).show();
                finish();
            } else {

                NdefFormatable formatable = NdefFormatable.get(tag);
                if (formatable != null) {
                    try {
                        formatable.connect();
                        formatable.format(message);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();

            return false;
        }
        return true;
    }

    /***********************
     * NdefMessage를 생성
     *
     * @param msg
     * @param type
     * @return
     ***********************/
    private NdefMessage createTagMessage(String msg, int type) {
        NdefRecord[] records = new NdefRecord[1];

        if (type == TYPE_TEXT) {
            records[0] = createTextRecord(msg, Locale.KOREAN, true);
        } else if (type == TYPE_URI) {
            records[0] = createUriRecord(msg.getBytes());
        }

        NdefMessage mMessage = new NdefMessage(records);

        return mMessage;
    }

    private NdefRecord createTextRecord(String text, Locale locale,
                                        boolean encodeInUtf8) {
        final byte[] langBytes = locale.getLanguage().getBytes(Charsets.US_ASCII);
        final Charset utfEncoding = encodeInUtf8 ? Charsets.UTF_8 : Charset
                .forName("UTF-16");
        final byte[] textBytes = text.getBytes(utfEncoding);
        final int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        final char status = (char) (utfBit + langBytes.length);
        final byte[] data = Bytes.concat(new byte[] { (byte) status }, langBytes,
                textBytes);
        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT,
                new byte[0], data);
    }

    private NdefRecord createUriRecord(byte[] data) {
        return new NdefRecord(NdefRecord.TNF_ABSOLUTE_URI, NdefRecord.RTD_URI,
                new byte[0], data);
    }


}

