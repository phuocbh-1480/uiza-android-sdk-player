package testlibuiza.sample.v3.customskin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;

import testlibuiza.R;
import testlibuiza.app.LSApplication;
import vn.loitp.chromecast.Casty;
import vn.loitp.core.base.BaseActivity;
import vn.loitp.core.utilities.LAnimationUtil;
import vn.loitp.core.utilities.LLog;
import vn.loitp.core.utilities.LScreenUtil;
import vn.loitp.core.utilities.LUIUtil;
import vn.loitp.restapi.uiza.model.v2.listallentity.Item;
import vn.loitp.restapi.uiza.model.v3.linkplay.getlinkplay.ResultGetLinkPlay;
import vn.loitp.restapi.uiza.model.v3.metadata.getdetailofmetadata.Data;
import vn.loitp.uizavideo.view.rl.video.UizaIMAVideo;
import vn.loitp.uizavideov3.util.UizaDataV3;
import vn.loitp.uizavideov3.util.UizaUtil;
import vn.loitp.uizavideov3.view.rl.video.UizaCallback;
import vn.loitp.uizavideov3.view.rl.video.UizaIMAVideoV3;
import vn.loitp.views.LToast;

/**
 * Created by loitp on 7/16/2018.
 */

public class CustomSkinActivity extends BaseActivity implements UizaCallback {
    private UizaIMAVideoV3 uizaIMAVideoV3;

    @Override
    protected boolean setFullScreen() {
        return false;
    }

    @Override
    protected String setTag() {
        return "TAG" + getClass().getSimpleName();
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_uiza_custom_skin;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        UizaDataV3.getInstance().setCasty(Casty.create(this));
        UizaDataV3.getInstance().setCurrentPlayerId(R.layout.uiza_controller_skin_custom_main);
        super.onCreate(savedInstanceState);
        uizaIMAVideoV3 = (UizaIMAVideoV3) findViewById(R.id.uiza_video);
        uizaIMAVideoV3.setUizaCallback(this);

        final String entityId = LSApplication.entityIdDefaultVOD;
        //String entityId = "ae8e7a65-b2f8-4803-a62c-6480e282616a";
        UizaUtil.initEntity(activity, uizaIMAVideoV3, entityId);

        findViewById(R.id.bt_change_skin_custom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uizaIMAVideoV3 != null) {
                    uizaIMAVideoV3.changeSkin(R.layout.uiza_controller_skin_custom_main);
                }
            }
        });
        findViewById(R.id.bt_change_skin_0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uizaIMAVideoV3 != null) {
                    uizaIMAVideoV3.changeSkin(R.layout.player_skin_default);
                }
            }
        });
        findViewById(R.id.bt_change_skin_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uizaIMAVideoV3 != null) {
                    uizaIMAVideoV3.changeSkin(R.layout.player_skin_1);
                }
            }
        });
        findViewById(R.id.bt_change_skin_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uizaIMAVideoV3 != null) {
                    uizaIMAVideoV3.changeSkin(R.layout.player_skin_2);
                }
            }
        });
        findViewById(R.id.bt_change_skin_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uizaIMAVideoV3 != null) {
                    uizaIMAVideoV3.changeSkin(R.layout.player_skin_3);
                }
            }
        });

        handleClickSampeText();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uizaIMAVideoV3.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        uizaIMAVideoV3.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uizaIMAVideoV3.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        uizaIMAVideoV3.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        uizaIMAVideoV3.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UizaIMAVideo.CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            if (resultCode == Activity.RESULT_OK) {
                uizaIMAVideoV3.initializePiP();
            } else {
                LToast.show(activity, "Draw over other app permission not available");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void isInitResult(boolean isInitSuccess, boolean isGetDataSuccess, ResultGetLinkPlay resultGetLinkPlay, Data data) {
        if (isInitSuccess) {
            uizaIMAVideoV3.setEventBusMsgFromActivityIsInitSuccess();
        }
    }

    @Override
    public void onClickListEntityRelation(Item item, int position) {
    }

    @Override
    public void onClickBack() {
        onBackPressed();
    }

    @Override
    public void onClickPip(Intent intent) {
        LLog.d(TAG, "onClickPip");
    }

    @Override
    public void onClickPipVideoInitSuccess(boolean isInitSuccess) {
        LLog.d(TAG, "onClickPipVideoInitSuccess " + isInitSuccess);
        if (isInitSuccess) {
            onBackPressed();
        }
    }

    @Override
    public void onSkinChange() {
        handleClickSampeText();
    }

    @Override
    public void onError(Exception e) {
        LLog.d(TAG, "onError " + e.getMessage());
    }

    @Override
    public void onBackPressed() {
        if (LScreenUtil.isFullScreen(activity)) {
            uizaIMAVideoV3.toggleFullscreen();
        } else {
            super.onBackPressed();
        }
    }

    private void handleClickSampeText() {
        TextView tvSample = uizaIMAVideoV3.getPlayerView().findViewById(R.id.tv_sample);
        if (tvSample != null) {
            tvSample.setText("This is a view from custom skin.\nTry to tap me.");
            LUIUtil.setTextShadow(tvSample);
            tvSample.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LAnimationUtil.play(v, Techniques.Pulse);
                    LToast.show(activity, "Click!");
                }
            });
        }
    }
}