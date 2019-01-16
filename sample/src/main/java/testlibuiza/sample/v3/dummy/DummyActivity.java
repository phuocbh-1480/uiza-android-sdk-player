package testlibuiza.sample.v3.dummy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import testlibuiza.R;
import vn.uiza.core.utilities.LLog;
import vn.uiza.utils.util.Encryptor;

public class DummyActivity extends AppCompatActivity {
    private final String TAG = "TAG" + getClass().getSimpleName();
    private TextView tvIn;
    private TextView tvOut;
    private Button bt;
    private String strIn = "7329ed563fdc22ebd712cdd1ceb5e2bd52122dd2f435f9e87b26eb86326297e44b05d0a1462f8feb683ac5ccb6d9df63197fac097219ece9dea9b4a38a1b193925b4d3f4f22f11194207f1ba12efc7d70aaa72ab3437469383022ad93bcad3594b093511b59e38e5c49fc7f13f609425a56076b1396f107db8cdd4bab2490dd7f7765a7c896c1ef3b53709ae1a0e09fa86f25564d637be37b545c4f7bab7c4eb2cdb9e788cb2cf53bfc6004f52ad7a23e73b8a1c56e64d97cf4182d5b107639e6e8150264baf143a1ef0e3ae5a0cf051c55cd14701f18448aa5febbafad647b2a3f1ece7517ea1d4c682eb2420207ed2db2c1b2581b8d4462b6ea6018406b6e3af5d79d2ead68d6b171e03135380e79a03d1bf9d8e633194a1188f0c75454c7dd04a85b8e78d3f543a9effd08159d15a93fdec67f6a77d633a5fd527016d1f51e6fd7d5c0f8958a84ca1dab5e1dfcdb90d77";
    private final String key = "W7gHszMhK9HKry29Qky5SaKP7qxBQ2bJ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);
        tvIn = (TextView) findViewById(R.id.tv_in);
        tvOut = (TextView) findViewById(R.id.tv_out);
        bt = (Button) findViewById(R.id.bt);
        tvIn.setText(strIn);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    test(strIn);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void test(String input) throws Exception {
        if (input == null || input.isEmpty() || input.length() <= 16) {
            return;
        }
        input = input.trim();
        String hexIv = strIn.substring(0, 16);
        LLog.d(TAG, "hexIv: " + hexIv);
        String hexText = strIn.substring(16, input.length());
        LLog.d(TAG, "hexText: " + hexText);

        byte[] decodedHex = org.apache.commons.codec.binary.Hex.decodeHex(hexText.toCharArray());
        String base64 = android.util.Base64.encodeToString(decodedHex, android.util.Base64.NO_WRAP);
        LLog.d(TAG, "base64 " + base64);

        String decrypt = Encryptor.decrypt(key, hexIv, base64);
        LLog.d(TAG, "decrypt " + decrypt);
        tvOut.setText(decrypt);

        if (decrypt.equals("{\"licenseAcquisitionUrl\":\"https://wv.service.expressplay.com/hms/wv/rights/?ExpressPlayToken=BAAzsz04KdYAAACQC0WYhaELSFCcPbc4O6vJXP3nPB7qnN9sL9boHHnvCDLzb6J4p_jh-x5FLWbHuk86vjnHAyY4LqTFZta0OTVB0tHcC87HAsxQWNPfVS0qV8pDxiDHa5ZYx6iz1vvoWlvRiMedIr8b5Q-aIaPehlE-MeiXtJHN6_wqZFrzR6Pw_PapWK4ENINrq1E-6iu6pEnGIozZrO9vbUS30B6grmQs_yciCXM\"}")) {
            LLog.d(TAG, "okkkkkkkkkkkkk");
        }
    }
}
