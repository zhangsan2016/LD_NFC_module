package com.ldgd.ld_nfc_module.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ldgd.ld_nfc_module.R;
import com.ldgd.ld_nfc_module.base.BaseActivity;
import com.ldgd.ld_nfc_module.util.DrawableUtil;
import com.ldgd.ld_nfc_module.util.LogUtil;
import com.ldgd.ld_nfc_module.util.TagDiscovery;
import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;
import com.st.st25sdk.TagHelper;
import com.st.st25sdk.type5.Type5Tag;
import com.st.st25sdk.type5.st25dv.ST25DVTag;

import java.util.Arrays;

import static com.st.st25sdk.MultiAreaInterface.AREA1;

public class NFCActivity extends BaseActivity implements TagDiscovery.onTagDiscoveryCompletedListener, View.OnClickListener {

    private static final String TAG = "NFCActivity";

    /// static private NFCTag mTag;
    static private ST25DVTag mTag;

    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private ContentViewAsync contentView;
    private LinearLayout ll;
    private EditText ed_search;
    private Button bt_read_nfc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏顶部的状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_nfc);


        ll = (LinearLayout) findViewById(R.id.ll_nfc);
        bt_read_nfc = (Button) this.findViewById(R.id.bt_read_nfc);

        // 初始化NFC-onResume处理
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);


        ed_search = (EditText) this.findViewById(R.id.ed_search);
        DrawableUtil drawableUtil = new DrawableUtil(ed_search, new DrawableUtil.OnDrawableListener() {
            @Override
            public void onLeft(View v, Drawable left) {
                Toast.makeText(getApplicationContext(), "left", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRight(View v, Drawable right) {
                Toast.makeText(getApplicationContext(), "right", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    }

    /**
     * 读取的开始位置和结束位置
     */
    private int mStartAddress;
    private int mNumberOfBytes;

    public void read(View view) {

     if(mTag != null){
         bt_read_nfc.setEnabled(false);
         //设置为读取所有
         mStartAddress = 0;
         mNumberOfBytes = 508;
         //   mNumberOfBytes = 508;
         ReadTheBytes(view, mStartAddress, mNumberOfBytes);
     }else{
         showToast("标签不在场区内");
     }



    }

    /**
     * 从 mNumberOfBytes
     *
     * @param v
     * @param mStartAddress
     * @param mNumberOfBytes
     */
    private void ReadTheBytes(View v, int mStartAddress, int mNumberOfBytes) {


        Snackbar snackbar = Snackbar.make(v, "", Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.reading_x_bytes_starting_y_address, mStartAddress, mNumberOfBytes), this);

        snackbar.setActionTextColor(getResources().getColor(R.color.white));
        snackbar.show();
        // by defaut - read first area
        if (mTag instanceof Type5Tag) {
            startType5ReadingAndDisplaying(mStartAddress, mNumberOfBytes);
        } else startType4ReadingAndDisplaying(mTag, AREA1);
    }

    private void startType4ReadingAndDisplaying(NFCTag tag, int area) {
        contentView = new ContentViewAsync(tag, area);
        contentView.execute();
    }

    private void startType5ReadingAndDisplaying(int startAddress, int numberOfBytes) {

        mStartAddress = startAddress;
        mNumberOfBytes = numberOfBytes;
        contentView = new ContentViewAsync(mTag);
        contentView.execute();
    }


    /**
     * 异步读取NFC数组
     */
    class ContentViewAsync extends AsyncTask<Void, Integer, Boolean> {
        byte mBuffer[] = null;
        NFCTag mTag;
        int mArea;

        public ContentViewAsync(NFCTag myTag) {
            mTag = myTag;
        }

        public ContentViewAsync(NFCTag myTag, int myArea) {
            mTag = myTag;
            mArea = myArea;
        }

        public ContentViewAsync(byte[] buffer) {
            mBuffer = buffer;
        }

        protected Boolean doInBackground(Void... arg0) {
            if (mBuffer == null) {
                try {
                    // Type 5
                    mBuffer = mTag.readBytes(mStartAddress, mNumberOfBytes);
                    // Warning: readBytes() may return less bytes than requested
                    int nbrOfBytesRead = 0;
                    if (mBuffer != null) {
                        nbrOfBytesRead = mBuffer.length;
                        // 保存nfc读取的数据
                          /*  CacheUtils.putString(ReadFragmentActivity.this,"nfcdata",Arrays.toString(mBuffer));
                            showToast(R.string.save_read_data);*/


                    }
                    if (nbrOfBytesRead != mNumberOfBytes) {
                        showToast(R.string.error_during_read_operation, nbrOfBytesRead);
                    }
                } catch (STException e) {
                    Log.e(TAG, e.getMessage());
                    showToast(R.string.Command_failed);
                    return false;
                }

            } else {
                // buffer already initialized by constructor - no need to read Tag.
                // Nothing to do
            }
            return true;
        }

        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (mBuffer != null) {
                /*mAdapter = new CustomListAdapter(mBuffer);
                lv = (ListView) findViewById(R.id.readBlocksListView);
                lv.setAdapter(mAdapter);*/
                LogUtil.e("xxx onPostExecute mBuffer = " + Arrays.toString(mBuffer));

            }
            bt_read_nfc.setEnabled(true);
        }

        private void snackBarUiThread() {
            runOnUiThread(new Thread(new Runnable() {
                public void run() {
                    // inform user that a read will be performed
                    Snackbar snackbar = Snackbar.make(ll, "", Snackbar.LENGTH_LONG);
                    snackbar.setText(getString(R.string.reading_x_bytes_starting_y_address, mStartAddress, mNumberOfBytes));
                    snackbar.setActionTextColor(getResources().getColor(R.color.white));
                    snackbar.show();
                }
            }));
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        Log.d(TAG, "onNewIntent " + intent);
        setIntent(intent);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mNfcAdapter != null) {
            Log.v(TAG, "disableForegroundDispatch");
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    public void onResume() {
        Intent intent = getIntent();
        Log.d(TAG, "Resume mainActivity intent: " + intent);
        super.onResume();

        processIntent(intent);

        if (mNfcAdapter != null) {
            Log.v(TAG, "enableForegroundDispatch");
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null /*nfcFiltersArray*/, null /*nfcTechLists*/);

            if (mNfcAdapter.isEnabled()) {
                // NFC enabled
            } else {
                // NFC disabled
            }

        } else {
            // NFC not available on this phone!!!
            showToast(getString(R.string.nfc_not_available));
        }

    }

    private static NfcIntentHook mNfcIntentHook;

    @Override
    public void onClick(View v) {

    }

    public interface NfcIntentHook {
        void newNfcIntent(Intent intent);
    }

    void processIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        Log.e(TAG, "processIntent " + intent);

        if (mNfcIntentHook != null) {
            // NFC Intent hook used only for test purpose!
            mNfcIntentHook.newNfcIntent(intent);
            return;
        }

        Tag androidTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (androidTag != null) {
            // A tag has been taped

            // Perform tag discovery in an asynchronous task
            // onTagDiscoveryCompleted() will be called when the discovery is completed.
            new TagDiscovery(this).execute(androidTag);

            // This intent has been processed. Reset it to be sure that we don't process it again
            // if the MainActivity is resumed
            setIntent(null);
        }
    }


    @Override
    public void onTagDiscoveryCompleted(NFCTag nfcTag, TagHelper.ProductID productId, STException e) {
        if (e != null) {
            Log.i(TAG, e.toString());
            Toast.makeText(getApplication(), R.string.error_while_reading_the_tag, Toast.LENGTH_LONG).show();
            return;
        }

        mTag = (ST25DVTag) nfcTag;
        Log.e("xxx productId =", "productId = " + productId);
        switch (productId) {
            case PRODUCT_ST_ST25DV64K_I:
            case PRODUCT_ST_ST25DV64K_J:
            case PRODUCT_ST_ST25DV16K_I:
            case PRODUCT_ST_ST25DV16K_J:
            case PRODUCT_ST_ST25DV04K_I:
            case PRODUCT_ST_ST25DV04K_J:
             /*   checkMailboxActivation();
                startTagActivity(ST25DVActivity.class, R.string.st25dv_menus);*/

                showToast("st25dv_menus");
                break;


            default:
                Toast.makeText(getApplication(), getResources().getString(R.string.unknown_tag), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Product not recognized");
                break;
        }
    }
}
