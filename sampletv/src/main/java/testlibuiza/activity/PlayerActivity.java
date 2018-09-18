package testlibuiza.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import testlibuiza.app.R;
import vn.uiza.core.base.BaseActivity;
import vn.uiza.core.common.Constants;
import vn.uiza.restapi.uiza.model.v2.listallentity.Item;
import vn.uiza.restapi.uiza.model.v3.linkplay.getlinkplay.ResultGetLinkPlay;
import vn.uiza.restapi.uiza.model.v3.metadata.getdetailofmetadata.Data;
import vn.uiza.uzv3.util.UZUtil;
import vn.uiza.uzv3.view.rl.video.UZCallback;
import vn.uiza.uzv3.view.rl.video.UZVideo;
import vn.uiza.views.LToast;

public class PlayerActivity extends BaseActivity implements UZCallback {
    private UZVideo uzVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uzVideo = (UZVideo) findViewById(R.id.uiza_video);
        uzVideo.setUZCallback(this);
        String entityId = getIntent().getStringExtra(Constants.KEY_UIZA_ENTITY_ID);
        UZUtil.initEntity(activity, uzVideo, entityId);
        //UZUtil.initPlaylistFolder(activity, uzVideo, "put the playlist/folder id here");
    }

    @Override
    protected boolean setFullScreen() {
        return false;
    }

    @Override
    protected String setTag() {
        return getClass().getSimpleName();
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_player;
    }

    @Override
    public void isInitResult(boolean isInitSuccess, boolean isGetDataSuccess, ResultGetLinkPlay resultGetLinkPlay, Data data) {
        if (isInitSuccess) {
            uzVideo.setEventBusMsgFromActivityIsInitSuccess();
        }
    }

    @Override
    public void onClickListEntityRelation(Item item, int position) {
    }

    @Override
    public void onClickBack() {
    }

    @Override
    public void onClickPip(Intent intent) {
    }

    @Override
    public void onClickPipVideoInitSuccess(boolean isInitSuccess) {

    }

    @Override
    public void onSkinChange() {
    }

    @Override
    public void onError(Exception e) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uzVideo.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        uzVideo.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uzVideo.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        uzVideo.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        uzVideo.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            if (resultCode == Activity.RESULT_OK) {
                uzVideo.initializePiP();
            } else {
                LToast.show(activity, "Draw over other app permission not available");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
