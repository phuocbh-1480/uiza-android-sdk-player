package vn.loitp.uizavideov3.view.rl.video;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.MediaRouteButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.rubensousa.previewseekbar.base.PreviewView;
import com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBar;
import com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBarLayout;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaTrack;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import loitp.core.R;
import vn.loitp.chromecast.Casty;
import vn.loitp.core.base.BaseActivity;
import vn.loitp.core.common.Constants;
import vn.loitp.core.utilities.LActivityUtil;
import vn.loitp.core.utilities.LConnectivityUtil;
import vn.loitp.core.utilities.LDateUtils;
import vn.loitp.core.utilities.LDeviceUtil;
import vn.loitp.core.utilities.LDialogUtil;
import vn.loitp.core.utilities.LImageUtil;
import vn.loitp.core.utilities.LLog;
import vn.loitp.core.utilities.LPref;
import vn.loitp.core.utilities.LScreenUtil;
import vn.loitp.core.utilities.LSocialUtil;
import vn.loitp.core.utilities.LUIUtil;
import vn.loitp.data.EventBusData;
import vn.loitp.restapi.restclient.RestClientTracking;
import vn.loitp.restapi.restclient.RestClientV3;
import vn.loitp.restapi.restclient.RestClientV3GetLinkPlay;
import vn.loitp.restapi.uiza.UizaServiceV2;
import vn.loitp.restapi.uiza.UizaServiceV3;
import vn.loitp.restapi.uiza.model.tracking.UizaTracking;
import vn.loitp.restapi.uiza.model.v2.listallentity.Item;
import vn.loitp.restapi.uiza.model.v2.listallentity.Subtitle;
import vn.loitp.restapi.uiza.model.v3.linkplay.getlinkplay.ResultGetLinkPlay;
import vn.loitp.restapi.uiza.model.v3.linkplay.getlinkplay.Url;
import vn.loitp.restapi.uiza.model.v3.linkplay.gettokenstreaming.ResultGetTokenStreaming;
import vn.loitp.restapi.uiza.model.v3.linkplay.gettokenstreaming.SendGetTokenStreaming;
import vn.loitp.restapi.uiza.model.v3.livestreaming.gettimestartlive.ResultTimeStartLive;
import vn.loitp.restapi.uiza.model.v3.livestreaming.getviewalivefeed.ResultGetViewALiveFeed;
import vn.loitp.restapi.uiza.model.v3.metadata.getdetailofmetadata.Data;
import vn.loitp.restapi.uiza.util.UizaV3Util;
import vn.loitp.rxandroid.ApiSubscriber;
import vn.loitp.uizavideo.listerner.ProgressCallback;
import vn.loitp.uizavideo.view.ComunicateMng;
import vn.loitp.uizavideo.view.dlg.info.UizaDialogInfo;
import vn.loitp.uizavideo.view.rl.video.UizaPlayerView;
import vn.loitp.uizavideo.view.util.UizaUtil;
import vn.loitp.uizavideov3.view.dlg.listentityrelation.PlayListCallbackV3;
import vn.loitp.uizavideov3.view.dlg.listentityrelation.UizaDialogListEntityRelationV3;
import vn.loitp.uizavideov3.view.floatview.FloatingUizaVideoServiceV3;
import vn.loitp.uizavideov3.view.manager.UizaPlayerManagerV3;
import vn.loitp.uizavideov3.view.util.UizaDataV3;
import vn.loitp.uizavideov3.view.util.UizaInputV3;
import vn.loitp.uizavideov3.view.util.UizaTrackingUtil;
import vn.loitp.views.LToast;
import vn.loitp.views.autosize.imagebuttonwithsize.ImageButtonWithSize;
import vn.loitp.views.seekbar.verticalseekbar.VerticalSeekBar;

/**
 * Created by www.muathu@gmail.com on 7/26/2017.
 */

public class UizaIMAVideoV3 extends RelativeLayout implements PreviewView.OnPreviewChangeListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private final String TAG = "TAG" + getClass().getSimpleName();
    private int DEFAULT_VALUE_BACKWARD_FORWARD = 10000;//10000 mls
    private BaseActivity activity;
    private boolean isLivestream;
    private boolean isTablet;
    //TODO remove
    private Gson gson = new Gson();
    private RelativeLayout rootView;
    private UizaPlayerManagerV3 uizaPlayerManagerV3;
    private ProgressBar progressBar;

    private LinearLayout llTop;
    //play controller
    private RelativeLayout llMid;
    private View llMidSub;

    private PreviewTimeBarLayout previewTimeBarLayout;
    private PreviewTimeBar previewTimeBar;
    private ImageView ivThumbnail;

    private RelativeLayout rlTimeBar;
    private RelativeLayout rlMsg;
    private TextView tvMsg;

    private ImageView ivVideoCover;
    private ImageButtonWithSize exoFullscreenIcon;
    private TextView tvTitle;
    private ImageButtonWithSize exoPause;
    private ImageButtonWithSize exoPlay;
    private ImageButtonWithSize exoRew;
    private ImageButtonWithSize exoFfwd;
    private ImageButtonWithSize exoBackScreen;
    private ImageButtonWithSize exoVolume;
    private ImageButtonWithSize exoSetting;
    private ImageButtonWithSize exoCc;
    private ImageButtonWithSize exoPlaylist;
    private ImageButtonWithSize exoHearing;
    private ImageButtonWithSize exoPictureInPicture;
    private ImageButtonWithSize exoShare;
    private VerticalSeekBar seekbarVolume;
    private VerticalSeekBar seekbarBirghtness;
    private ImageView exoIvPreview;

    private RelativeLayout rlLiveInfo;
    private TextView tvLiveView;
    private TextView tvLiveTime;

    private LinearLayout debugLayout;
    private LinearLayout debugRootView;
    private TextView debugTextView;

    private int firstBrightness = Constants.NOT_FOUND;

    private ResultGetLinkPlay mResultGetLinkPlay;
    private Data mData;//infortion of video (VOD or LIVE)
    private boolean isResultGetLinkPlayDone;

    private final int DELAY_FIRST_TO_GET_LIVE_INFORMATION = 100;
    private final int DELAY_TO_GET_LIVE_INFORMATION = 15000;

    //chromecast https://github.com/DroidsOnRoids/Casty
    private MediaRouteButton mediaRouteButton;
    private RelativeLayout rlChromeCast;
    private ImageButtonWithSize ibsCast;

    public PlayerView getPlayerView() {
        return playerView;
    }

    public TextView getDebugTextView() {
        return debugTextView;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public PreviewTimeBarLayout getPreviewTimeBarLayout() {
        return previewTimeBarLayout;
    }

    public ImageView getIvThumbnail() {
        return ivThumbnail;
    }

    private boolean isHasError;

    private void handleError(Exception e) {
        if (isHasError) {
            LLog.e(TAG, "handleError isHasError=true -> return");
            return;
        }
        LLog.e(TAG, "handleError " + e.toString());
        isHasError = true;
        if (callback != null) {
            callback.onError(e);
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void init(@NonNull String entityId, final String urlIMAAd, final String urlThumnailsPreviewSeekbar, final boolean isTryToPlayPreviousUizaInputIfPlayCurrentUizaInputFailed) {
        LLog.d(TAG, "======================NEW SESSION======================");
        LLog.d(TAG, "entityId " + entityId);
        UizaDataV3.getInstance().setSettingPlayer(true);
        isHasError = false;
        UizaV3Util.getDetailEntity((BaseActivity) activity, entityId, new UizaV3Util.Callback() {
            @Override
            public void onSuccess(Data d) {
                mData = d;
                LLog.d(TAG, "init getDetailEntity onSuccess: " + gson.toJson(mData));

                //save current data
                LPref.setData(activity, mData, gson);

                UizaInputV3 uizaInputV3 = new UizaInputV3();
                uizaInputV3.setData(mData);

                //uizaInputV3.setUrlIMAAd(activity.getString(loitp.core.R.string.ad_tag_url));
                uizaInputV3.setUrlIMAAd(urlIMAAd);

                //uizaInputV3.setUrlThumnailsPreviewSeekbar(activity.getString(loitp.core.R.string.url_thumbnails));
                uizaInputV3.setUrlThumnailsPreviewSeekbar(urlThumnailsPreviewSeekbar);

                UizaDataV3.getInstance().setUizaInput(uizaInputV3);

                checkData();
            }

            @Override
            public void onError(Throwable e) {
                LLog.e(TAG, "init onError " + e.toString());
                LToast.show(activity, "init onError: " + e.getMessage());
            }
        });
    }

    public void init(@NonNull String entityId, final String urlIMAAd, final String urlThumnailsPreviewSeekbar) {
        init(entityId, urlIMAAd, urlThumnailsPreviewSeekbar, false);
    }

    public void init(@NonNull String entityId) {
        init(entityId, null, null, false);
    }

    private void checkData() {
        LLog.d(TAG, "checkData");
        UizaDataV3.getInstance().setSettingPlayer(true);
        isHasError = false;
        if (UizaDataV3.getInstance().getEntityId() == null || UizaDataV3.getInstance().getEntityId().isEmpty()) {
            LLog.d(TAG, "checkData getEntityId null or empty -> return");
            LDialogUtil.showDialog1Immersive(activity, activity.getString(R.string.entity_cannot_be_null_or_empty), new LDialogUtil.Callback1() {
                @Override
                public void onClick1() {
                    handleError(new Exception(activity.getString(R.string.entity_cannot_be_null_or_empty)));
                }

                @Override
                public void onCancel() {
                    handleError(new Exception(activity.getString(R.string.entity_cannot_be_null_or_empty)));
                }
            });
            return;
        }

        isLivestream = UizaDataV3.getInstance().isLivestream();
        LLog.d(TAG, "isLivestream " + isLivestream);

        if (LPref.getClickedPip(activity)) {
            LLog.d(TAG, "-> trackUiza getClickedPip true -> dont clearAllValues");
        } else {
            UizaTrackingUtil.clearAllValues(activity);
            LLog.d(TAG, "-> trackUiza getClickedPip false -> clearAllValues");
        }

        if (uizaPlayerManagerV3 != null) {
            //LLog.d(TAG, "init uizaPlayerManager != null");
            uizaPlayerManagerV3.release();
            mResultGetLinkPlay = null;
            mData = null;
            isResultGetLinkPlayDone = false;
            resetCountTryLinkPlayError();
        }
        updateUI();
        setTitle();
        setVideoCover();
        getTokenStreaming();
        LUIUtil.showProgressBar(progressBar);

        //track event eventype display
        if (UizaTrackingUtil.isTrackedEventTypeDisplay(activity)) {
            //da track roi ko can track nua
        } else {
            trackUiza(UizaDataV3.getInstance().createTrackingInputV3(activity, Constants.EVENT_TYPE_DISPLAY), new UizaTrackingUtil.UizaTrackingCallback() {
                @Override
                public void onTrackingSuccess() {
                    UizaTrackingUtil.setTrackingDoneWithEventTypeDisplay(activity, true);
                }
            });
        }

        //track event plays_requested
        if (UizaTrackingUtil.isTrackedEventTypePlaysRequested(activity)) {
            //da track roi ko can track nua
        } else {
            trackUiza(UizaDataV3.getInstance().createTrackingInputV3(activity, Constants.EVENT_TYPE_PLAYS_REQUESTED), new UizaTrackingUtil.UizaTrackingCallback() {
                @Override
                public void onTrackingSuccess() {
                    UizaTrackingUtil.setTrackingDoneWithEventTypePlaysRequested(activity, true);
                }
            });
        }
    }

    private int countTryLinkPlayError = 0;

    public void tryNextLinkPlay() {
        if (isLivestream) {
            //try to play 3 times
            if (countTryLinkPlayError >= 3) {
                showTvMsg(activity.getString(R.string.err_live_is_stopped));
            }

            //if entity is livestreaming, dont try to next link play
            //LLog.d(TAG, "tryNextLinkPlay isLivestream true -> try to replay = count " + countTryLinkPlayError);
            if (uizaPlayerManagerV3 != null) {
                uizaPlayerManagerV3.initWithoutReset();
                uizaPlayerManagerV3.setRunnable();
            }
            countTryLinkPlayError++;
            return;
        }
        countTryLinkPlayError++;
        if (Constants.IS_DEBUG) {
            LToast.show(activity, activity.getString(R.string.cannot_play_will_try) + "\n" + countTryLinkPlayError);
        }
        //LLog.d(TAG, "tryNextLinkPlay countTryLinkPlayError " + countTryLinkPlayError);
        uizaPlayerManagerV3.release();
        checkToSetUp();
    }

    //khi call api getLinkPlay nhung json tra ve ko co data
    //se co gang choi video da play gan nhat
    //neu co thi se play
    //khong co thi bao loi
    private void handleErrorNoData() {
        //LLog.e(TAG, "handleErrorNoData");
        removeVideoCover(true);
        LDialogUtil.showDialog1Immersive(activity, activity.getString(R.string.has_no_linkplay), new LDialogUtil.Callback1() {
            @Override
            public void onClick1() {
                LLog.e(TAG, "handleErrorNoData onClick1");
                if (callback != null) {
                    UizaDataV3.getInstance().setSettingPlayer(false);
                    callback.isInitResult(false, null, null);

                    //callback.onError(new Exception(activity.getString(R.string.has_no_linkplay)));
                }
            }

            @Override
            public void onCancel() {
                LLog.e(TAG, "handleErrorNoData onCancel");
                if (callback != null) {
                    UizaDataV3.getInstance().setSettingPlayer(false);
                    callback.isInitResult(false, null, null);
                    //callback.onError(new Exception(activity.getString(R.string.has_no_linkplay)));
                }
            }
        });
    }

    private void checkToSetUp() {
        //LLog.d(TAG, "checkToSetUp");
        if (isResultGetLinkPlayDone) {
            if (mResultGetLinkPlay != null && mData != null) {
                //LLog.d(TAG, "checkToSetUp if");
                List<String> listLinkPlay = new ArrayList<>();
                List<Url> urlList = mResultGetLinkPlay.getData().getUrls();

                for (Url url : urlList) {
                    if (url.getSupport().toLowerCase().equals("mpd")) {
                        listLinkPlay.add(url.getUrl());
                    }
                }
                for (Url url : urlList) {
                    if (url.getSupport().toLowerCase().equals("m3u8")) {
                        listLinkPlay.add(url.getUrl());
                    }
                }

                //listLinkPlay.add("https://toanvk-live.uizacdn.net/893db5e8bb3943bfb12894fec56c8875-live/hi-uaqsv9as/manifest.mpd");
                //listLinkPlay.add("http://112.78.4.162/drm/test/hevc/playlist.mpd");
                //listLinkPlay.add("http://112.78.4.162/6yEB8Lgd/package/playlist.mpd");
                //listLinkPlay.add("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd");

                //LLog.d(TAG, "listLinkPlay toJson: " + gson.toJson(listLinkPlay));
                if (listLinkPlay == null || listLinkPlay.isEmpty()) {
                    LLog.e(TAG, "checkToSetUp listLinkPlay == null || listLinkPlay.isEmpty()");
                    handleErrorNoData();
                    return;
                }
                if (countTryLinkPlayError >= listLinkPlay.size()) {
                    if (LConnectivityUtil.isConnected(activity)) {
                        LDialogUtil.showDialog1Immersive(activity, activity.getString(R.string.try_all_link_play_but_no_luck), new LDialogUtil.Callback1() {
                            @Override
                            public void onClick1() {
                                handleError(new Exception(activity.getString(R.string.try_all_link_play_but_no_luck)));
                            }

                            @Override
                            public void onCancel() {
                                handleError(new Exception(activity.getString(R.string.try_all_link_play_but_no_luck)));
                            }
                        });
                    } else {
                        //LLog.d(TAG, "checkToSetUp else err_no_internet");
                        showTvMsg(activity.getString(R.string.err_no_internet));
                    }
                    return;
                }
                String linkPlay = listLinkPlay.get(countTryLinkPlayError);

                List<Subtitle> subtitleList = null;
                //TODO iplm v3 chua co subtitle
                //List<Subtitle> subtitleList = mResultRetrieveAnEntity.getData().get(0).getSubtitle();
                //LLog.d(TAG, "subtitleList toJson: " + gson.toJson(subtitleList));

                initDataSource(linkPlay, UizaDataV3.getInstance().getUrlIMAAd(), UizaDataV3.getInstance().getUrlThumnailsPreviewSeekbar(), subtitleList);
                initUizaPlayerManagerV3();
            } else {
                LDialogUtil.showDialog1Immersive(activity, activity.getString(R.string.err_setup), new LDialogUtil.Callback1() {
                    @Override
                    public void onClick1() {
                        handleError(new Exception(activity.getString(R.string.err_setup)));
                    }

                    @Override
                    public void onCancel() {
                        handleError(new Exception(activity.getString(R.string.err_setup)));
                    }
                });
            }
        }
    }

    public void resetCountTryLinkPlayError() {
        countTryLinkPlayError = 0;
    }

    private void setVideoCover() {
        if (ivVideoCover.getVisibility() != VISIBLE) {
            resetCountTryLinkPlayError();

            ivVideoCover.setVisibility(VISIBLE);
            ivVideoCover.invalidate();
            LImageUtil.load(activity, UizaDataV3.getInstance().getThumbnail() == null ? Constants.URL_IMG_THUMBNAIL : UizaDataV3.getInstance().getThumbnail(), ivVideoCover, R.drawable.uiza);
        }
    }

    public void removeVideoCover(boolean isFromHandleError) {
        if (ivVideoCover.getVisibility() != GONE) {
            //LLog.d(TAG, "--------removeVideoCover isFromHandleError: " + isFromHandleError);
            ivVideoCover.setVisibility(GONE);
            if (isLivestream) {
                tvLiveTime.setText("-");
                tvLiveView.setText("-");
                updateLiveInfoCurrentView(DELAY_FIRST_TO_GET_LIVE_INFORMATION);
                updateLiveInfoTimeStartLive(DELAY_FIRST_TO_GET_LIVE_INFORMATION);
            }
            if (!isFromHandleError) {
                onStateReadyFirst();
            }
        }
    }

    public UizaIMAVideoV3(Context context) {
        super(context);
        onCreate();
    }

    public UizaIMAVideoV3(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreate();
    }

    public UizaIMAVideoV3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public UizaIMAVideoV3(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        onCreate();
    }

    private void onCreate() {
        activity = ((BaseActivity) getContext());
        LPref.setClassNameOfPlayer(activity, activity.getLocalClassName());
        inflate(getContext(), R.layout.v3_uiza_ima_video_core_rl, this);

        rootView = (RelativeLayout) findViewById(R.id.root_view);
        isTablet = LDeviceUtil.isTablet(activity);
        addPlayerView();
        findViews();
        UizaUtil.resizeLayout(rootView, llMid, ivVideoCover);
        updateUIEachSkin();
        setMarginPreviewTimeBarLayout();
        setMarginRlLiveInfo();

        //setup chromecast
        //mediaRouteButton = (MediaRouteButton) playerView.findViewById(R.id.media_route_button);
        mediaRouteButton = new MediaRouteButton(activity);
        llTop.addView(mediaRouteButton);
        setUpMediaRouteButton();
    }

    private UizaPlayerView playerView;

    private void addPlayerView() {
        playerView = null;
        switch (UizaDataV3.getInstance().getCurrentPlayerId()) {
            case Constants.PLAYER_ID_SKIN_1:
                playerView = (UizaPlayerView) activity.getLayoutInflater().inflate(R.layout.player_skin_1, null);
                break;
            case Constants.PLAYER_ID_SKIN_2:
                playerView = (UizaPlayerView) activity.getLayoutInflater().inflate(R.layout.player_skin_2, null);
                break;
            case Constants.PLAYER_ID_SKIN_3:
                playerView = (UizaPlayerView) activity.getLayoutInflater().inflate(R.layout.player_skin_3, null);
                break;
            case Constants.PLAYER_ID_SKIN_0:
                playerView = (UizaPlayerView) activity.getLayoutInflater().inflate(R.layout.player_skin_default, null);
                break;
            default:
                playerView = (UizaPlayerView) activity.getLayoutInflater().inflate(R.layout.player_skin_default, null);
                break;
        }
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        playerView.setLayoutParams(lp);
        rootView.addView(playerView);
    }

    private void updateUIEachSkin() {
        //LLog.d(TAG, "updateUIEachSkin");
        switch (UizaDataV3.getInstance().getCurrentPlayerId()) {
            case Constants.PLAYER_ID_SKIN_2:
            case Constants.PLAYER_ID_SKIN_3:
                exoPlay.setRatioLand(7);
                exoPlay.setRatioPort(5);
                exoPause.setRatioLand(7);
                exoPause.setRatioPort(5);
                break;
        }
    }

    private void updatePositionOfProgressBar() {
        //LLog.d(TAG, "updatePositionOfProgressBar set progressBar center in parent");
        playerView.post(new Runnable() {
            @Override
            public void run() {
                int marginL = playerView.getMeasuredWidth() / 2 - progressBar.getMeasuredWidth() / 2;
                int marginT = playerView.getMeasuredHeight() / 2 - progressBar.getMeasuredHeight() / 2;
                //LLog.d(TAG, "updatePositionOfProgressBar " + marginL + "x" + marginT);
                LUIUtil.setMarginPx(progressBar, marginL, marginT, 0, 0);
            }
        });
    }

    private void findViews() {
        //LLog.d(TAG, "findViews");
        rlMsg = (RelativeLayout) findViewById(R.id.rl_msg);
        rlMsg.setOnClickListener(this);
        tvMsg = (TextView) findViewById(R.id.tv_msg);
        LUIUtil.setTextShadow(tvMsg);
        ivVideoCover = (ImageView) findViewById(R.id.iv_cover);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        llMid = (RelativeLayout) findViewById(R.id.ll_mid);
        llMidSub = (View) findViewById(R.id.ll_mid_sub);
        progressBar = (ProgressBar) findViewById(R.id.pb);
        LUIUtil.setColorProgressBar(progressBar, ContextCompat.getColor(activity, R.color.White));
        playerView = findViewById(R.id.player_view);

        updatePositionOfProgressBar();

        rlTimeBar = playerView.findViewById(R.id.rl_time_bar);
        previewTimeBar = playerView.findViewById(R.id.exo_progress);
        previewTimeBarLayout = playerView.findViewById(R.id.previewSeekBarLayout);
        previewTimeBarLayout.setTintColorResource(R.color.colorPrimary);
        previewTimeBar.addOnPreviewChangeListener(this);
        ivThumbnail = (ImageView) playerView.findViewById(R.id.image_view_thumnail);

        exoFullscreenIcon = (ImageButtonWithSize) playerView.findViewById(R.id.exo_fullscreen_toggle_icon);
        tvTitle = (TextView) playerView.findViewById(R.id.tv_title);
        exoPause = (ImageButtonWithSize) playerView.findViewById(R.id.exo_pause_uiza);
        exoPlay = (ImageButtonWithSize) playerView.findViewById(R.id.exo_play_uiza);
        exoPlay.setVisibility(GONE);
        exoRew = (ImageButtonWithSize) playerView.findViewById(R.id.exo_rew);
        exoFfwd = (ImageButtonWithSize) playerView.findViewById(R.id.exo_ffwd);
        exoBackScreen = (ImageButtonWithSize) playerView.findViewById(R.id.exo_back_screen);
        exoVolume = (ImageButtonWithSize) playerView.findViewById(R.id.exo_volume);
        exoSetting = (ImageButtonWithSize) playerView.findViewById(R.id.exo_setting);
        exoCc = (ImageButtonWithSize) playerView.findViewById(R.id.exo_cc);
        exoPlaylist = (ImageButtonWithSize) playerView.findViewById(R.id.exo_playlist);
        exoHearing = (ImageButtonWithSize) playerView.findViewById(R.id.exo_hearing);

        //TODO exoHearing works fine, but QC dont want to show it, fuck QC team
        exoHearing.setVisibility(GONE);

        exoPictureInPicture = (ImageButtonWithSize) playerView.findViewById(R.id.exo_picture_in_picture);
        exoShare = (ImageButtonWithSize) playerView.findViewById(R.id.exo_share);

        exoIvPreview = (ImageView) playerView.findViewById(R.id.exo_iv_preview);
        seekbarVolume = (VerticalSeekBar) playerView.findViewById(R.id.seekbar_volume);
        seekbarBirghtness = (VerticalSeekBar) playerView.findViewById(R.id.seekbar_birghtness);
        LUIUtil.setColorSeekBar(seekbarVolume, Color.TRANSPARENT);
        LUIUtil.setColorSeekBar(seekbarBirghtness, Color.TRANSPARENT);

        debugLayout = findViewById(R.id.debug_layout);
        debugRootView = findViewById(R.id.controls_root);
        debugTextView = findViewById(R.id.debug_text_view);

        if (Constants.IS_DEBUG) {
            //TODO revert is VISIBLE
            debugLayout.setVisibility(View.GONE);
        } else {
            debugLayout.setVisibility(View.GONE);
        }

        rlLiveInfo = (RelativeLayout) playerView.findViewById(R.id.rl_live_info);
        tvLiveView = (TextView) playerView.findViewById(R.id.tv_live_view);
        tvLiveTime = (TextView) playerView.findViewById(R.id.tv_live_time);

        //onclick
        exoFullscreenIcon.setOnClickListener(this);
        exoBackScreen.setOnClickListener(this);
        exoVolume.setOnClickListener(this);
        exoSetting.setOnClickListener(this);
        exoCc.setOnClickListener(this);
        exoPlaylist.setOnClickListener(this);
        exoHearing.setOnClickListener(this);
        exoPictureInPicture.setOnClickListener(this);
        exoShare.setOnClickListener(this);
        exoFfwd.setOnClickListener(this);
        exoRew.setOnClickListener(this);
        exoPlay.setOnClickListener(this);
        exoPause.setOnClickListener(this);

        //seekbar change
        seekbarVolume.setOnSeekBarChangeListener(this);
        seekbarBirghtness.setOnSeekBarChangeListener(this);

        addChromecastLayer();
    }

    //tự tạo layout chromecast và background đen
    private void addChromecastLayer() {
        rlChromeCast = new RelativeLayout(activity);
        RelativeLayout.LayoutParams rlChromeCastParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rlChromeCast.setLayoutParams(rlChromeCastParams);
        rlChromeCast.setVisibility(GONE);
        //rlChromeCast.setBackgroundColor(ContextCompat.getColor(activity, R.color.black_65));
        rlChromeCast.setBackgroundColor(ContextCompat.getColor(activity, R.color.Black));

        ibsCast = new ImageButtonWithSize(activity);
        ibsCast.setBackgroundColor(Color.TRANSPARENT);
        ibsCast.setImageResource(R.drawable.cast);
        RelativeLayout.LayoutParams ibsCastParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ibsCastParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        ibsCast.setLayoutParams(ibsCastParams);
        ibsCast.setRatioPort(5);
        ibsCast.setRatioLand(5);
        ibsCast.setScaleType(ImageView.ScaleType.FIT_CENTER);
        ibsCast.setColorFilter(Color.WHITE);
        rlChromeCast.addView(ibsCast);
        rlChromeCast.setOnClickListener(this);

        if (llTop != null) {
            if (llTop.getParent() instanceof ViewGroup) {
                ((RelativeLayout) llTop.getParent()).addView(rlChromeCast, 0);
            }
        } else if (llMid != null) {
            if (llMid.getParent() instanceof ViewGroup) {
                ((RelativeLayout) llMid.getParent()).addView(rlChromeCast, 0);
            }
        } else if (rlLiveInfo != null) {
            if (rlLiveInfo.getParent() instanceof ViewGroup) {
                ((RelativeLayout) rlLiveInfo.getParent()).addView(rlChromeCast, 0);
            }
        }
    }

    private ProgressCallback progressCallback;

    public void setProgressCallback(ProgressCallback progressCallback) {
        this.progressCallback = progressCallback;
    }

    private void initDataSource(String linkPlay, String urlIMAAd, String
            urlThumnailsPreviewSeekbar, List<Subtitle> subtitleList) {
        LLog.d(TAG, "-------------------->initDataSource linkPlay " + linkPlay);
        uizaPlayerManagerV3 = new UizaPlayerManagerV3(this, linkPlay, urlIMAAd, urlThumnailsPreviewSeekbar, subtitleList);
        if (urlThumnailsPreviewSeekbar == null || urlThumnailsPreviewSeekbar.isEmpty()) {
            previewTimeBarLayout.setEnabled(false);
        } else {
            previewTimeBarLayout.setEnabled(true);
        }
        if (previewTimeBarLayout != null) {
            previewTimeBarLayout.setPreviewLoader(uizaPlayerManagerV3);
        }
        uizaPlayerManagerV3.setProgressCallback(new ProgressCallback() {
            @Override
            public void onAdProgress(float currentMls, int s, float duration, int percent) {
                //LLog.d(TAG, TAG + " ad progress currentMls: " + currentMls + ", s:" + s + ", duration: " + duration + ",percent: " + percent + "%");
                if (progressCallback != null) {
                    progressCallback.onAdProgress(currentMls, s, duration, percent);
                }
            }

            @Override
            public void onVideoProgress(float currentMls, int s, float duration, int percent) {
                //LLog.d(TAG, TAG + " onVideoProgress video progress currentMls: " + currentMls + ", s:" + s + ", duration: " + duration + ",percent: " + percent + "%");
                trackProgress(s, percent);
                if (progressCallback != null) {
                    progressCallback.onVideoProgress(currentMls, s, duration, percent);
                }
            }
        });
        uizaPlayerManagerV3.setDebugCallback(new UizaPlayerManagerV3.DebugCallback() {
            @Override
            public void onUpdateButtonVisibilities() {
                //LLog.d(TAG, "onUpdateButtonVisibilities");
                updateButtonVisibilities();
            }
        });

        //========================>>>>>start init seekbar
        isSetProgressSeekbarFirst = true;
        //set volume max in first play
        seekbarVolume.setMax(100);
        setProgressSeekbar(seekbarVolume, 99);
        exoVolume.setImageResource(R.drawable.ic_volume_up_black_48dp);

        //set bightness max in first play
        firstBrightness = LScreenUtil.getCurrentBrightness(getContext()) * 100 / 255 + 1;
        //LLog.d(TAG, "firstBrightness " + firstBrightness);
        seekbarBirghtness.setMax(100);
        setProgressSeekbar(seekbarBirghtness, firstBrightness);
        isSetProgressSeekbarFirst = false;
        //========================<<<<<end init seekbar
    }

    private boolean isSetProgressSeekbarFirst;
    private int oldPercent = Constants.NOT_FOUND;

    private void trackProgress(int s, int percent) {
        //track event view (after video is played 5s)
        //LLog.d(TAG, "onVideoProgress -> track view s: " + s + ", percent " + percent);
        if (s == (isLivestream ? 3 : 5)) {
            //LLog.d(TAG, "onVideoProgress -> track view s: " + s + ", percent " + percent);
            if (UizaTrackingUtil.isTrackedEventTypeView(activity)) {
                //da track roi ko can track nua
            } else {
                trackUiza(UizaDataV3.getInstance().createTrackingInputV3(activity, Constants.EVENT_TYPE_VIEW), new UizaTrackingUtil.UizaTrackingCallback() {
                    @Override
                    public void onTrackingSuccess() {
                        UizaTrackingUtil.setTrackingDoneWithEventTypeView(activity, true);
                    }
                });
            }
        }

        //dont track play_throught if entity is live
        if (isLivestream) {
            //LLog.d(TAG, "trackProgress percent: " + percent + "% -> dont track play_throught if entity is live");
            return;
        }
        if (oldPercent == percent) {
            return;
        }
        //LLog.d(TAG, "trackProgress percent: " + percent);
        oldPercent = percent;
        if (percent == Constants.PLAYTHROUGH_25) {
            if (UizaTrackingUtil.isTrackedEventTypePlayThrought25(activity)) {
                //da track roi ko can track nua
            } else {
                trackUiza(UizaDataV3.getInstance().createTrackingInputV3(activity, "25", Constants.EVENT_TYPE_PLAY_THROUGHT), new UizaTrackingUtil.UizaTrackingCallback() {
                    @Override
                    public void onTrackingSuccess() {
                        UizaTrackingUtil.setTrackingDoneWithEventTypePlayThrought25(activity, true);
                    }
                });
            }
        } else if (percent == Constants.PLAYTHROUGH_50) {
            if (UizaTrackingUtil.isTrackedEventTypePlayThrought50(activity)) {
                //da track roi ko can track nua
            } else {
                trackUiza(UizaDataV3.getInstance().createTrackingInputV3(activity, "50", Constants.EVENT_TYPE_PLAY_THROUGHT), new UizaTrackingUtil.UizaTrackingCallback() {
                    @Override
                    public void onTrackingSuccess() {
                        UizaTrackingUtil.setTrackingDoneWithEventTypePlayThrought50(activity, true);
                    }
                });
            }
        } else if (percent == Constants.PLAYTHROUGH_75) {
            if (UizaTrackingUtil.isTrackedEventTypePlayThrought75(activity)) {
                //da track roi ko can track nua
            } else {
                trackUiza(UizaDataV3.getInstance().createTrackingInputV3(activity, "75", Constants.EVENT_TYPE_PLAY_THROUGHT), new UizaTrackingUtil.UizaTrackingCallback() {
                    @Override
                    public void onTrackingSuccess() {
                        UizaTrackingUtil.setTrackingDoneWithEventTypePlayThrought75(activity, true);
                    }
                });
            }
        } else if (percent == Constants.PLAYTHROUGH_100) {
            if (UizaTrackingUtil.isTrackedEventTypePlayThrought100(activity)) {
                //da track roi ko can track nua
            } else {
                trackUiza(UizaDataV3.getInstance().createTrackingInputV3(activity, "100", Constants.EVENT_TYPE_PLAY_THROUGHT), new UizaTrackingUtil.UizaTrackingCallback() {
                    @Override
                    public void onTrackingSuccess() {
                        UizaTrackingUtil.setTrackingDoneWithEventTypePlayThrought100(activity, true);
                    }
                });
            }
        }
    }

    public void onStateReadyFirst() {
        //LLog.d(TAG, "onStateReadyFirst");
        if (callback != null) {
            callback.isInitResult(true, mResultGetLinkPlay, mData);
        }

        if (isCastingChromecast) {
            LLog.d(TAG, "onStateReadyFirst init new play check isCastingChromecast: " + isCastingChromecast);
            handleConnectedChromecast();
            showController();
        }

        if (UizaTrackingUtil.isTrackedEventTypeVideoStarts(activity)) {
            //da track roi ko can track nua
        } else {
            trackUiza(UizaDataV3.getInstance().createTrackingInputV3(activity, Constants.EVENT_TYPE_VIDEO_STARTS), new UizaTrackingUtil.UizaTrackingCallback() {
                @Override
                public void onTrackingSuccess() {
                    UizaTrackingUtil.setTrackingDoneWithEventTypeVideoStarts(activity, true);
                }
            });
        }
        UizaDataV3.getInstance().setSettingPlayer(false);
    }

    public void setProgressSeekbar(final VerticalSeekBar verticalSeekBar,
                                   final int progressSeekbar) {
        verticalSeekBar.setProgress(progressSeekbar);
        //LLog.d(TAG, "setProgressSeekbar " + progressSeekbar);
    }

    public void setProgressVolumeSeekbar(int progress) {
        setProgressSeekbar(seekbarVolume, progress);
    }

    public int getCurrentProgressSeekbarVolume() {
        if (seekbarVolume == null) {
            return 0;
        }
        return seekbarVolume.getProgress();
    }

    public void onDestroy() {
        if (firstBrightness != Constants.NOT_FOUND) {
            LScreenUtil.setBrightness(getContext(), firstBrightness);
        }
        if (uizaPlayerManagerV3 != null) {
            uizaPlayerManagerV3.release();
        }
        UizaDataV3.getInstance().setSettingPlayer(false);
        LDialogUtil.clearAll();
        activityIsPausing = true;
        isCastingChromecast = false;
        isCastPlayerPlayingFirst = false;
        //LLog.d(TAG, "onDestroy -> set activityIsPausing = true");
    }

    public void onResume() {
        if (isCastingChromecast) {
            LLog.d(TAG, "onResume isCastingChromecast true => return");
            //handleConnectedChromecast();
            return;
        }

        //LLog.d(TAG, "onResume");
        activityIsPausing = false;
        if (isExoShareClicked) {
            isExoShareClicked = false;

            if (uizaPlayerManagerV3 != null) {
                uizaPlayerManagerV3.resumeVideo();
            }
        } else {
            initUizaPlayerManagerV3();
        }
    }

    private void initUizaPlayerManagerV3() {
        if (uizaPlayerManagerV3 != null) {
            //LLog.d(TAG, "onResume uizaPlayerManagerV3 init");
            uizaPlayerManagerV3.init();
            if (isCalledFromConnectionEventBus) {
                uizaPlayerManagerV3.setRunnable();
                isCalledFromConnectionEventBus = false;
            }
        }
    }

    private boolean activityIsPausing = false;

    private boolean isActivityIsPausing() {
        return activityIsPausing;
    }

    public void onPause() {
        //LLog.d(TAG, "onPause " + isExoShareClicked);
        activityIsPausing = true;
        if (isExoShareClicked) {
            if (uizaPlayerManagerV3 != null) {
                uizaPlayerManagerV3.pauseVideo();
            }
        } else {
            if (uizaPlayerManagerV3 != null) {
                uizaPlayerManagerV3.reset();
            }
        }
    }

    public void onStart() {
        EventBus.getDefault().register(this);
    }

    public void onStop() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStartPreview(PreviewView previewView) {
        //LLog.d(TAG, "onStartPreview");
    }

    @Override
    public void onStopPreview(PreviewView previewView) {
        //LLog.d(TAG, "onStopPreview");
        uizaPlayerManagerV3.resumeVideo();
    }

    @Override
    public void onPreview(PreviewView previewView, int progress, boolean fromUser) {
        //LLog.d(TAG, "onPreview progress " + progress);
        if (isCastingChromecast) {
            UizaDataV3.getInstance().getCasty().getPlayer().seek(progress);
        }
    }

    private boolean isExoShareClicked;
    private boolean isExoVolumeClicked;

    public void toggleScreenOritation() {
        LActivityUtil.toggleScreenOritation(activity);
    }

    @Override
    public void onClick(View v) {
        if (v == rlMsg) {
            //do nothing
            //LLog.d(TAG, "onClick llMsg");
        } else if (v == exoFullscreenIcon) {
            toggleScreenOritation();
        } else if (v == exoBackScreen) {
            if (isLandscape) {
                exoFullscreenIcon.performClick();
            } else {
                if (callback != null) {
                    callback.onClickBack();
                }
            }
        } else if (v == exoVolume) {
            handleClickBtVolume();
        } else if (v == exoSetting) {
            View view = UizaUtil.getBtVideo(debugRootView);
            if (view != null) {
                UizaUtil.getBtVideo(debugRootView).performClick();
            }
        } else if (v == exoCc) {
            if (uizaPlayerManagerV3.getSubtitleList() == null || uizaPlayerManagerV3.getSubtitleList().isEmpty()) {
                UizaDialogInfo uizaDialogInfo = new UizaDialogInfo(activity, activity.getString(R.string.text), activity.getString(R.string.no_caption));
                UizaUtil.showUizaDialog(activity, uizaDialogInfo);
            } else {
                View view = UizaUtil.getBtText(debugRootView);
                if (view != null) {
                    UizaUtil.getBtText(debugRootView).performClick();
                }
            }
        } else if (v == exoPlaylist) {
            UizaDialogListEntityRelationV3 uizaDialogListEntityRelation = new UizaDialogListEntityRelationV3(activity, isLandscape, new PlayListCallbackV3() {
                @Override
                public void onClickItem(Item item, int position) {
                    //LLog.d(TAG, "onClickItem " + gson.toJson(item));
                    if (callback != null) {
                        callback.onClickListEntityRelation(item, position);
                    }
                }

                @Override
                public void onDismiss() {
                    //do nothing
                }
            });
            UizaUtil.showUizaDialog(activity, uizaDialogListEntityRelation);
        } else if (v == exoHearing) {
            View view = UizaUtil.getBtAudio(debugRootView);
            if (view != null) {
                UizaUtil.getBtAudio(debugRootView).performClick();
            }
        } else if (v == exoPictureInPicture) {
            clickPiP();
        } else if (v == exoShare) {
            LSocialUtil.share(activity, isLandscape);
            isExoShareClicked = true;
        } else if (v.getParent() == debugRootView) {
            MappingTrackSelector.MappedTrackInfo mappedTrackInfo = uizaPlayerManagerV3.getTrackSelector().getCurrentMappedTrackInfo();
            if (mappedTrackInfo != null && uizaPlayerManagerV3.getTrackSelectionHelper() != null) {
                uizaPlayerManagerV3.getTrackSelectionHelper().showSelectionDialog(activity, ((Button) v).getText(), mappedTrackInfo, (int) v.getTag());
            }
        } else if (v == rlChromeCast) {
            //dangerous to remove
            LLog.d(TAG, "do nothing click rl_chrome_cast");
        } else if (v == exoFfwd) {
            if (isCastingChromecast) {
                UizaDataV3.getInstance().getCasty().getPlayer().seekToForward(DEFAULT_VALUE_BACKWARD_FORWARD);
            } else {
                uizaPlayerManagerV3.seekToForward(DEFAULT_VALUE_BACKWARD_FORWARD);
            }
        } else if (v == exoRew) {
            if (isCastingChromecast) {
                UizaDataV3.getInstance().getCasty().getPlayer().seekToBackward(DEFAULT_VALUE_BACKWARD_FORWARD);
            } else {
                uizaPlayerManagerV3.seekToBackward(DEFAULT_VALUE_BACKWARD_FORWARD);
            }
        } else if (v == exoPause) {
            if (isCastingChromecast) {
                UizaDataV3.getInstance().getCasty().getPlayer().pause();
            } else {
                uizaPlayerManagerV3.pauseVideo();
            }
            exoPause.setVisibility(GONE);
            exoPlay.setVisibility(VISIBLE);
        } else if (v == exoPlay) {
            if (isCastingChromecast) {
                UizaDataV3.getInstance().getCasty().getPlayer().play();
            } else {
                uizaPlayerManagerV3.resumeVideo();
            }
            exoPlay.setVisibility(GONE);
            exoPause.setVisibility(VISIBLE);
        }
    }

    //current screen is landscape or portrait
    private boolean isLandscape;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (activity != null) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                LScreenUtil.hideDefaultControls(activity);
                isLandscape = true;
                UizaUtil.setUIFullScreenIcon(getContext(), exoFullscreenIcon, true);
                if (isTablet) {
                    exoPictureInPicture.setVisibility(GONE);
                }
            } else {
                LScreenUtil.showDefaultControls(activity);
                isLandscape = false;
                UizaUtil.setUIFullScreenIcon(getContext(), exoFullscreenIcon, false);
                if (isTablet) {
                    exoPictureInPicture.setVisibility(VISIBLE);
                }
            }
        }
        setMarginPreviewTimeBarLayout();
        setMarginRlLiveInfo();
        UizaUtil.resizeLayout(rootView, llMid, ivVideoCover);
        updatePositionOfProgressBar();
    }

    private void setMarginPreviewTimeBarLayout() {
        if (isLandscape) {
            LUIUtil.setMarginDimen(previewTimeBarLayout, 24, 0, 24, 0);
        } else {
            LUIUtil.setMarginDimen(previewTimeBarLayout, 15, 0, 15, 0);
        }
    }

    private void setMarginRlLiveInfo() {
        if (isLandscape) {
            LUIUtil.setMarginDimen(rlLiveInfo, 50, 0, 50, 0);
        } else {
            LUIUtil.setMarginDimen(rlLiveInfo, 5, 0, 5, 0);
        }
    }

    public void setTitle() {
        tvTitle.setText(UizaDataV3.getInstance().getEntityName());
    }

    private void updateUI() {
        //LLog.d(TAG, "updateUI isTablet " + isTablet);
        if (isTablet) {
            exoPictureInPicture.setVisibility(VISIBLE);
        } else {
            exoPictureInPicture.setVisibility(GONE);
        }
        if (isLivestream) {
            exoPlaylist.setVisibility(GONE);
            exoCc.setVisibility(GONE);
            rlTimeBar.setVisibility(GONE);

            //TODO why set gone not work?
            //exoRew.setVisibility(GONE);
            //exoFfwd.setVisibility(GONE);

            changeVisibilitiesOfButton(exoRew, false, 0);
            changeVisibilitiesOfButton(exoFfwd, false, 0);

            rlLiveInfo.setVisibility(VISIBLE);
        } else {
            exoPlaylist.setVisibility(VISIBLE);
            exoCc.setVisibility(VISIBLE);
            rlTimeBar.setVisibility(VISIBLE);

            //TODO why set visible not work?
            //exoRew.setVisibility(VISIBLE);
            //exoFfwd.setVisibility(VISIBLE);

            changeVisibilitiesOfButton(exoRew, true, R.drawable.ic_fast_rewind_black_48dp);
            changeVisibilitiesOfButton(exoFfwd, true, R.drawable.ic_fast_forward_black_48dp);

            rlLiveInfo.setVisibility(GONE);
        }
    }

    //trick to gone view
    private void changeVisibilitiesOfButton(ImageButtonWithSize imageButtonWithSize,
                                            boolean isVisible, int res) {
        if (imageButtonWithSize == null) {
            return;
        }
        imageButtonWithSize.setClickable(isVisible);
        imageButtonWithSize.setImageResource(res);
    }

    public void updateButtonVisibilities() {
        debugRootView.removeAllViews();
        if (uizaPlayerManagerV3.getPlayer() == null) {
            return;
        }
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = uizaPlayerManagerV3.getTrackSelector().getCurrentMappedTrackInfo();
        if (mappedTrackInfo == null) {
            return;
        }
        for (int i = 0; i < mappedTrackInfo.length; i++) {
            TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(i);
            if (trackGroups.length != 0) {
                Button button = new Button(activity);
                int label;
                switch (uizaPlayerManagerV3.getPlayer().getRendererType(i)) {
                    case C.TRACK_TYPE_AUDIO:
                        label = R.string.audio;
                        break;
                    case C.TRACK_TYPE_VIDEO:
                        label = R.string.video;
                        break;
                    case C.TRACK_TYPE_TEXT:
                        label = R.string.text;
                        break;
                    default:
                        continue;
                }
                button.setText(label);
                button.setTag(i);
                button.setOnClickListener(this);
                debugRootView.addView(button);
            }
        }
    }

    //on seekbar change
    @Override
    public void onProgressChanged(final SeekBar seekBar, int progress, boolean fromUser) {
        //LLog.d(TAG, "onProgressChanged progress: " + progress);
        if (seekBar == null || !isLandscape) {
            if (isExoVolumeClicked) {
                //LLog.d(TAG, "onProgressChanged !isExoVolumeClicked ctn");
            } else {
                //LLog.d(TAG, "onProgressChanged !isExoVolumeClicked return");
                return;
            }
        }
        if (seekBar == seekbarVolume) {
            if (isSetProgressSeekbarFirst || isExoVolumeClicked) {
                exoIvPreview.setVisibility(INVISIBLE);
            } else {
                if (progress >= 66) {
                    exoIvPreview.setImageResource(R.drawable.ic_volume_up_black_48dp);
                } else if (progress >= 33) {
                    exoIvPreview.setImageResource(R.drawable.ic_volume_down_black_48dp);
                } else {
                    exoIvPreview.setImageResource(R.drawable.ic_volume_mute_black_48dp);
                }
            }
            //LLog.d(TAG, "seekbarVolume onProgressChanged " + progress + " -> " + ((float) progress / 100));
            if (progress == 0) {
                exoVolume.setImageResource(R.drawable.ic_volume_off_black_48dp);
            } else {
                exoVolume.setImageResource(R.drawable.ic_volume_up_black_48dp);
            }
            uizaPlayerManagerV3.setVolume(((float) progress / 100));
            if (isExoVolumeClicked) {
                isExoVolumeClicked = false;
            }
        } else if (seekBar == seekbarBirghtness) {
            //LLog.d(TAG, "seekbarBirghtness onProgressChanged " + progress);
            if (isSetProgressSeekbarFirst) {
                exoIvPreview.setVisibility(INVISIBLE);
            } else {
                if (progress >= 85) {
                    exoIvPreview.setImageResource(R.drawable.ic_brightness_7_black_48dp);
                } else if (progress >= 71) {
                    exoIvPreview.setImageResource(R.drawable.ic_brightness_6_black_48dp);
                } else if (progress >= 57) {
                    exoIvPreview.setImageResource(R.drawable.ic_brightness_5_black_48dp);
                } else if (progress >= 42) {
                    exoIvPreview.setImageResource(R.drawable.ic_brightness_4_black_48dp);
                } else if (progress >= 28) {
                    exoIvPreview.setImageResource(R.drawable.ic_brightness_3_black_48dp);
                } else if (progress >= 14) {
                    exoIvPreview.setImageResource(R.drawable.ic_brightness_2_black_48dp);
                } else {
                    exoIvPreview.setImageResource(R.drawable.ic_brightness_1_black_48dp);
                }
            }
            LScreenUtil.setBrightness(activity, progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //LLog.d(TAG, "onStartTrackingTouch");
        if (!isLandscape) {
            return;
        }
        LUIUtil.setTintSeekbar(seekBar, Color.WHITE);
        exoIvPreview.setVisibility(VISIBLE);
        if (llMidSub != null) {
            llMidSub.setVisibility(INVISIBLE);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //LLog.d(TAG, "onStopTrackingTouch");
        if (!isLandscape) {
            return;
        }
        LUIUtil.setTintSeekbar(seekBar, Color.TRANSPARENT);
        exoIvPreview.setVisibility(INVISIBLE);
        if (llMidSub != null) {
            llMidSub.setVisibility(VISIBLE);
        }
    }
    //end on seekbar change

    public static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 6969;

    private void clickPiP() {
        //LLog.d(TAG, "clickPiP");
        if (activity == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(activity)) {
            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            //LLog.d(TAG, "clickPiP if");
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            //LLog.d(TAG, "clickPiP else");
            initializePiP();
        }
    }

    public void initializePiP() {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, FloatingUizaVideoServiceV3.class);
        intent.putExtra(Constants.FLOAT_LINK_PLAY, uizaPlayerManagerV3.getLinkPlay());
        intent.putExtra(Constants.FLOAT_IS_LIVESTREAM, isLivestream);
        activity.startService(intent);
        if (callback != null) {
            callback.onClickPip(intent);
        }
    }

    public SimpleExoPlayer getPlayer() {
        if (uizaPlayerManagerV3 == null) {
            return null;
        }
        return uizaPlayerManagerV3.getPlayer();
    }

    private void getTokenStreaming() {
        UizaServiceV3 service = RestClientV3.createService(UizaServiceV3.class);
        SendGetTokenStreaming sendGetTokenStreaming = new SendGetTokenStreaming();
        sendGetTokenStreaming.setAppId(UizaDataV3.getInstance().getAppId());
        sendGetTokenStreaming.setEntityId(UizaDataV3.getInstance().getEntityId());
        sendGetTokenStreaming.setContentType(SendGetTokenStreaming.STREAM);
        activity.subscribe(service.getTokenStreaming(sendGetTokenStreaming), new ApiSubscriber<ResultGetTokenStreaming>() {
            @Override
            public void onSuccess(ResultGetTokenStreaming result) {
                //LLog.d(TAG, "getTokenStreaming onSuccess: " + gson.toJson(result));
                if (result == null || result.getData() == null || result.getData().getToken() == null || result.getData().getToken().isEmpty()) {
                    LDialogUtil.showDialog1Immersive(activity, activity.getString(R.string.no_token_streaming), new LDialogUtil.Callback1() {
                        @Override
                        public void onClick1() {
                            handleError(new Exception(activity.getString(R.string.no_token_streaming)));
                        }

                        @Override
                        public void onCancel() {
                            handleError(new Exception(activity.getString(R.string.no_token_streaming)));
                        }
                    });
                    return;
                }
                String tokenStreaming = result.getData().getToken();
                LLog.d(TAG, "getTokenStreaming onSuccess: " + tokenStreaming);
                getLinkPlay(tokenStreaming);
            }

            @Override
            public void onFail(Throwable e) {
                LLog.e(TAG, "getTokenStreaming onFail " + e.getMessage());
                final String msg = Constants.IS_DEBUG ? activity.getString(R.string.no_token_streaming) + "\n" + e.getMessage() : activity.getString(R.string.no_token_streaming);
                LDialogUtil.showDialog1Immersive(activity, msg, new LDialogUtil.Callback1() {
                    @Override
                    public void onClick1() {
                        handleError(new Exception(msg));
                    }

                    @Override
                    public void onCancel() {
                        handleError(new Exception(msg));
                    }
                });
            }
        });
    }

    private void getLinkPlay(String tokenStreaming) {
        //LLog.d(TAG, "getLinkPlay isLivestream " + isLivestream);
        RestClientV3GetLinkPlay.addAuthorization(tokenStreaming);
        UizaServiceV3 service = RestClientV3GetLinkPlay.createService(UizaServiceV3.class);
        if (isLivestream) {
            String appId = UizaDataV3.getInstance().getAppId();
            String channelName = UizaDataV3.getInstance().getChannelName();
            //LLog.d(TAG, "===================================");
            //LLog.d(TAG, "========name: " + channelName);
            //LLog.d(TAG, "========appId: " + appId);
            //LLog.d(TAG, "===================================");
            activity.subscribe(service.getLinkPlayLive(appId, channelName), new ApiSubscriber<ResultGetLinkPlay>() {
                @Override
                public void onSuccess(ResultGetLinkPlay result) {
                    LLog.d(TAG, "getLinkPlayLive onSuccess: " + gson.toJson(result));
                    mResultGetLinkPlay = result;
                    isResultGetLinkPlayDone = true;
                    checkToSetUp();
                }

                @Override
                public void onFail(Throwable e) {
                    if (e == null) {
                        LLog.d(TAG, "getLinkPlay LIVE onFail");
                        return;
                    }
                    LLog.e(TAG, "getLinkPlayLive LIVE onFail " + e.getMessage());
                    final String msg = Constants.IS_DEBUG ? activity.getString(R.string.no_link_play) + "\n" + e.getMessage() : activity.getString(R.string.no_link_play);
                    LDialogUtil.showDialog1Immersive(activity, msg, new LDialogUtil.Callback1() {
                        @Override
                        public void onClick1() {
                            handleError(new Exception(msg));
                        }

                        @Override
                        public void onCancel() {
                            handleError(new Exception(msg));
                        }
                    });
                }
            });
        } else {
            String appId = UizaDataV3.getInstance().getAppId();
            String entityId = UizaDataV3.getInstance().getEntityId();
            String typeContent = SendGetTokenStreaming.STREAM;
            //LLog.d(TAG, "===================================");
            //LLog.d(TAG, "========tokenStreaming: " + tokenStreaming);
            //LLog.d(TAG, "========appId: " + appId);
            //LLog.d(TAG, "========entityId: " + entityId);
            //LLog.d(TAG, "===================================");
            activity.subscribe(service.getLinkPlay(appId, entityId, typeContent), new ApiSubscriber<ResultGetLinkPlay>() {
                @Override
                public void onSuccess(ResultGetLinkPlay result) {
                    LLog.d(TAG, "getLinkPlayVOD onSuccess: " + gson.toJson(result));
                    mResultGetLinkPlay = result;
                    isResultGetLinkPlayDone = true;
                    checkToSetUp();
                }

                @Override
                public void onFail(Throwable e) {
                    if (e == null) {
                        LLog.d(TAG, "getLinkPlay VOD onFail");
                        return;
                    }
                    LLog.e(TAG, "getLinkPlay VOD onFail " + e.getMessage());
                    final String msg = Constants.IS_DEBUG ? activity.getString(R.string.no_link_play) + "\n" + e.getMessage() : activity.getString(R.string.no_link_play);
                    LDialogUtil.showDialog1Immersive(activity, msg, new LDialogUtil.Callback1() {
                        @Override
                        public void onClick1() {
                            handleError(new Exception(msg));
                        }

                        @Override
                        public void onCancel() {
                            handleError(new Exception(msg));
                        }
                    });
                }
            });
        }
    }

    public interface Callback {
        //when video init done with result
        public void isInitResult(boolean isInitSuccess, ResultGetLinkPlay resultGetLinkPlay, Data data);

        //user click an item in entity relation
        public void onClickListEntityRelation(Item item, int position);

        //user click button back in controller
        public void onClickBack();

        //user click button pip in controller
        public void onClickPip(Intent intent);

        //when pip video is inited success
        public void onClickPipVideoInitSuccess(boolean isInitSuccess);

        //when uiimavideo had an error
        public void onError(Exception e);

    }

    private Callback callback;

    private void trackUiza(final UizaTracking uizaTracking, final UizaTrackingUtil.UizaTrackingCallback uizaTrackingCallback) {
        UizaServiceV2 service = RestClientTracking.createService(UizaServiceV2.class);
        activity.subscribe(service.track(uizaTracking), new ApiSubscriber<Object>() {
            @Override
            public void onSuccess(Object tracking) {
                //LLog.d(TAG, "<------------------------trackUiza noPiP getEventType: " + uizaTracking.getEventType() + ", getEntityName:" + uizaTracking.getEntityName() + ", getPlayThrough: " + uizaTracking.getPlayThrough() + " ==> " + gson.toJson(tracking));
                LLog.d(TAG, "Track success! " + uizaTracking.getEntityName() + " : " + uizaTracking.getEventType() + " : " + uizaTracking.getPlayThrough());
                if (Constants.IS_DEBUG) {
                    LToast.show(getContext(), "Track success!\n" + uizaTracking.getEntityName() + "\n" + uizaTracking.getEventType() + "\n" + uizaTracking.getPlayThrough());
                }
                if (uizaTrackingCallback != null) {
                    uizaTrackingCallback.onTrackingSuccess();
                }
            }

            @Override
            public void onFail(Throwable e) {
                //TODO iplm if track fail
                LLog.e(TAG, "trackUiza onFail " + e.toString() + "\n->>>" + uizaTracking.getEntityName() + ", getEventType: " + uizaTracking.getEventType() + ", getPlayThrough: " + uizaTracking.getPlayThrough());
            }
        });
    }

    public void seekTo(long positionMs) {
        if (uizaPlayerManagerV3 != null) {
            //LLog.d(TAG, "seekTo positionMs: " + positionMs);
            uizaPlayerManagerV3.seekTo(positionMs);
        }
    }

    private boolean isCalledFromConnectionEventBus = false;

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusData.ConnectEvent event) {
        if (event != null) {
            //LLog.d(TAG, "onMessageEventConnectEvent isConnected: " + event.isConnected());
            if (event.isConnected()) {
                if (uizaPlayerManagerV3 != null) {
                    LDialogUtil.clearAll();
                    //hideLLMsg();
                    if (uizaPlayerManagerV3.getExoPlaybackException() == null) {
                        //LLog.d(TAG, "onMessageEventConnectEvent do nothing");
                    } else {
                        isCalledFromConnectionEventBus = true;
                        uizaPlayerManagerV3.setResumeIfConnectionError();
                        //LLog.d(TAG, "onMessageEventConnectEvent activityIsPausing " + activityIsPausing);
                        if (!activityIsPausing) {
                            uizaPlayerManagerV3.init();
                            if (isCalledFromConnectionEventBus) {
                                uizaPlayerManagerV3.setRunnable();
                                isCalledFromConnectionEventBus = false;
                            }
                        } else {
                            //LLog.d(TAG, "onMessageEventConnectEvent auto call onResume() again");
                        }
                    }
                }
            } else {
                showTvMsg(activity.getString(R.string.err_no_internet));
                //if current screen is portrait -> do nothing
                //else current screen is landscape -> change screen to portrait
                LActivityUtil.changeScreenPortrait(activity);
            }
        }
    }

    //listen msg from service
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ComunicateMng.MsgFromService msg) {
        //LLog.d(TAG, "get event from service");
        if (msg == null) {
            return;
        }
        //when pip float view init success
        if (callback != null && msg instanceof ComunicateMng.MsgFromServiceIsInitSuccess) {
            //LLog.d(TAG, "get event from service isInitSuccess: " + ((ComunicateMng.MsgFromServiceIsInitSuccess) msg).isInitSuccess());

            ComunicateMng.MsgFromActivityPosition msgFromActivityPosition = new ComunicateMng.MsgFromActivityPosition(null);
            msgFromActivityPosition.setPosition(uizaPlayerManagerV3.getCurrentPosition());
            ComunicateMng.postFromActivity(msgFromActivityPosition);

            callback.onClickPipVideoInitSuccess(((ComunicateMng.MsgFromServiceIsInitSuccess) msg).isInitSuccess());
        } else if (msg instanceof ComunicateMng.MsgFromServicePosition) {
            //LLog.d(TAG, "seek to: " + ((ComunicateMng.MsgFromServicePosition) msg).getPosition());
            if (uizaPlayerManagerV3 != null) {
                uizaPlayerManagerV3.seekTo(((ComunicateMng.MsgFromServicePosition) msg).getPosition());
            }
        }
    }

    public void showController() {
        playerView.showController();
    }

    public void hideController() {
        playerView.hideController();
    }

    public void hideControllerOnTouch(boolean isHide) {
        playerView.setControllerHideOnTouch(isHide);
    }

    private void showTvMsg(String msg) {
        //LLog.d(TAG, "showTvMsg " + msg);
        tvMsg.setText(msg);
        showLLMsg();
    }

    private void showLLMsg() {
        if (rlMsg.getVisibility() != VISIBLE) {
            rlMsg.setVisibility(VISIBLE);
        }
        hideController();
    }

    public void hideLLMsg() {
        if (rlMsg.getVisibility() != GONE) {
            rlMsg.setVisibility(GONE);
        }
    }

    private void updateLiveInfoCurrentView(final int durationDelay) {
        if (!isLivestream) {
            return;
        }
        LUIUtil.setDelay(durationDelay, new LUIUtil.DelayCallback() {
            @Override
            public void doAfter(int mls) {
                //LLog.d(TAG, "updateLiveInfoCurrentView " + System.currentTimeMillis());
                if (!isLivestream) {
                    return;
                }
                if (uizaPlayerManagerV3 != null && playerView != null && (playerView.isControllerVisible() || durationDelay == DELAY_FIRST_TO_GET_LIVE_INFORMATION)) {
                    //LLog.d(TAG, "updateLiveInfoCurrentView isShowing");
                    UizaServiceV3 service = RestClientV3.createService(UizaServiceV3.class);
                    String id = UizaDataV3.getInstance().getEntityId();
                    activity.subscribe(service.getViewALiveFeed(id), new ApiSubscriber<ResultGetViewALiveFeed>() {
                        @Override
                        public void onSuccess(ResultGetViewALiveFeed result) {
                            //LLog.d(TAG, "getViewALiveFeed onSuccess: " + gson.toJson(result));
                            if (result != null && result.getData() != null) {
                                tvLiveView.setText(result.getData().getWatchnow() + "");
                            }
                            updateLiveInfoCurrentView(DELAY_TO_GET_LIVE_INFORMATION);
                        }

                        @Override
                        public void onFail(Throwable e) {
                            LLog.e(TAG, "getViewALiveFeed onFail " + e.getMessage());
                            updateLiveInfoCurrentView(DELAY_TO_GET_LIVE_INFORMATION);
                        }
                    });
                } else {
                    //LLog.d(TAG, "updateLiveInfoCurrentView !isShowing");
                    updateLiveInfoCurrentView(DELAY_TO_GET_LIVE_INFORMATION);
                }
            }
        });
    }

    private void updateLiveInfoTimeStartLive(final int durationDelay) {
        if (!isLivestream) {
            return;
        }
        LUIUtil.setDelay(durationDelay, new LUIUtil.DelayCallback() {

            @Override
            public void doAfter(int mls) {
                if (!isLivestream) {
                    return;
                }
                if (uizaPlayerManagerV3 != null && playerView != null && (playerView.isControllerVisible() || durationDelay == DELAY_FIRST_TO_GET_LIVE_INFORMATION)) {
                    UizaServiceV3 service = RestClientV3.createService(UizaServiceV3.class);
                    String entityId = UizaDataV3.getInstance().getEntityId();
                    String feedId = UizaDataV3.getInstance().getLastFeedId();
                    activity.subscribe(service.getTimeStartLive(entityId, feedId), new ApiSubscriber<ResultTimeStartLive>() {
                        @Override
                        public void onSuccess(ResultTimeStartLive result) {
                            //LLog.d(TAG, "getTimeStartLive onSuccess: " + gson.toJson(result));
                            if (result != null && result.getData() != null && result.getData().getStartTime() != null) {
                                //LLog.d(TAG, "startTime " + result.getData().getStartTime());
                                long startTime = LDateUtils.convertDateToTimeStamp(result.getData().getStartTime(), LDateUtils.FORMAT_1);
                                //LLog.d(TAG, "startTime " + startTime);
                                long now = System.currentTimeMillis();
                                //LLog.d(TAG, "now: " + now);
                                long duration = now - startTime;
                                //LLog.d(TAG, "duration " + duration);
                                String s = LDateUtils.convertMlscondsToHMmSs(duration);
                                //LLog.d(TAG, "s " + s);
                                tvLiveTime.setText(s);
                            }
                            updateLiveInfoTimeStartLive(DELAY_TO_GET_LIVE_INFORMATION);
                        }

                        @Override
                        public void onFail(Throwable e) {
                            LLog.e(TAG, "getTimeStartLive onFail " + e.getMessage());
                            updateLiveInfoTimeStartLive(DELAY_TO_GET_LIVE_INFORMATION);
                        }
                    });
                } else {
                    updateLiveInfoTimeStartLive(DELAY_TO_GET_LIVE_INFORMATION);
                }
            }
        });
    }

    /*Kiểm tra xem nếu activity được tạo thành công nếu user click vào pip thì sẽ bắn 1 eventbus báo rằng đã init success
     * receiver FloatingUizaVideoServiceV3 để truyền current position */
    public void setEventBusMsgFromActivityIsInitSuccess() {
        if (LPref.getClickedPip(activity)) {
            ComunicateMng.MsgFromActivityIsInitSuccess msgFromActivityIsInitSuccess = new ComunicateMng.MsgFromActivityIsInitSuccess(null);
            msgFromActivityIsInitSuccess.setInitSuccess(true);
            ComunicateMng.postFromActivity(msgFromActivityIsInitSuccess);
        }
    }

    /*START CHROMECAST*/
    @UiThread
    private void setUpMediaRouteButton() {
        UizaDataV3.getInstance().getCasty().setUpMediaRouteButton(mediaRouteButton);
        UizaDataV3.getInstance().getCasty().setOnConnectChangeListener(new Casty.OnConnectChangeListener() {
            @Override
            public void onConnected() {
                //LLog.d(TAG, "setUpMediaRouteButton setOnConnectChangeListener onConnected");
                handleConnectedChromecast();
            }

            @Override
            public void onDisconnected() {
                //LLog.d(TAG, "setUpMediaRouteButton setOnConnectChangeListener onDisconnected");
                handleDisconnectedChromecast();
            }
        });
        /*casty.setOnCastSessionUpdatedListener(new Casty.OnCastSessionUpdatedListener() {
            @Override
            public void onCastSessionUpdated(CastSession castSession) {
                if (castSession != null) {
                    LLog.d(TAG, "onCastSessionUpdated " + castSession.getSessionRemainingTimeMs());
                    RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
                }
            }
        });*/
    }

    private void handleConnectedChromecast() {
        isCastingChromecast = true;
        isCastPlayerPlayingFirst = false;
        playChromecast();
        updateUIChromecast();
    }

    private void handleDisconnectedChromecast() {
        isCastingChromecast = false;
        isCastPlayerPlayingFirst = false;
        updateUIChromecast();
    }

    private boolean isCastingChromecast;

    public boolean isCastingChromecast() {
        return isCastingChromecast;
    }

    private void playChromecast() {
        if (mData == null || uizaPlayerManagerV3 == null || uizaPlayerManagerV3.getPlayer() == null) {
            return;
        }
        LUIUtil.showProgressBar(progressBar);
        final long lastCurrentPosition = uizaPlayerManagerV3.getCurrentPosition();
        LLog.d(TAG, "playChromecast exo stop lastCurrentPosition: " + lastCurrentPosition);

        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);

        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, mData.getDescription());
        movieMetadata.putString(MediaMetadata.KEY_TITLE, mData.getEntityName());
        movieMetadata.addImage(new WebImage(Uri.parse(mData.getThumbnail())));

        //TODO add subtile vtt to chromecast
        List<MediaTrack> mediaTrackList = new ArrayList<>();
        /*MediaTrack mediaTrack = UizaDataV3.getInstance().buildTrack(
                1,
                "text",
                "captions",
                "http://112.78.4.162/sub.vtt",
                "English Subtitle",
                "en-US");
        mediaTrackList.add(mediaTrack);
        mediaTrack = UizaDataV3.getInstance().buildTrack(
                2,
                "text",
                "captions",
                "http://112.78.4.162/sub.vtt",
                "XYZ Subtitle",
                "en-US");
        mediaTrackList.add(mediaTrack);*/

        long duration = uizaPlayerManagerV3.getPlayer().getDuration();
        //LLog.d(TAG, "duration " + duration);
        if (duration < 0) {
            LLog.e(TAG, "invalid duration -> cannot play chromecast");
            return;
        }

        MediaInfo mediaInfo = new MediaInfo.Builder(
                uizaPlayerManagerV3.getLinkPlay())
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType("videos/mp4")
                .setMetadata(movieMetadata)
                .setMediaTracks(mediaTrackList)
                .setStreamDuration(duration)
                .build();

        //play chromecast with full screen control
        //UizaDataV3.getInstance().getCasty().getPlayer().loadMediaAndPlay(mediaInfo, true, lastCurrentPosition);

        //play chromecast without screen control
        UizaDataV3.getInstance().getCasty().getPlayer().loadMediaAndPlayInBackground(mediaInfo, true, lastCurrentPosition);

        UizaDataV3.getInstance().getCasty().getPlayer().getRemoteMediaClient().addProgressListener(new RemoteMediaClient.ProgressListener() {
            @Override
            public void onProgressUpdated(long currentPosition, long duration) {
                //LLog.d(TAG, "onProgressUpdated " + currentPosition + " - " + duration + " >>> max " + previewTimeBar.getMax());
                if (currentPosition >= lastCurrentPosition && !isCastPlayerPlayingFirst) {
                    //LLog.d(TAG, "onProgressUpdated PLAYING FIRST");
                    uizaPlayerManagerV3.hideProgress();
                    //UizaDataV3.getInstance().getCasty().setVolume(0.99f);
                    isCastPlayerPlayingFirst = true;
                }

                if (currentPosition > 0) {
                    uizaPlayerManagerV3.seekTo(currentPosition);
                    //previewTimeBar.setPosition(currentPosition);
                }
            }
        }, 1000);

    }

    private boolean isCastPlayerPlayingFirst;

    /*STOP CHROMECAST*/

    /*khi click vào biểu tượng casting
     * thì sẽ pause local player và bắt đầu loading lên cast player
     * khi disconnet thì local player sẽ resume*/
    private void updateUIChromecast() {
        if (uizaPlayerManagerV3 == null || rlChromeCast == null) {
            return;
        }
        if (isCastingChromecast) {
            uizaPlayerManagerV3.pauseVideo();
            uizaPlayerManagerV3.setVolume(0f);
            rlChromeCast.setVisibility(VISIBLE);
            exoSetting.setVisibility(GONE);
            exoCc.setVisibility(GONE);
            llMid.setVisibility(GONE);

            exoPlay.setVisibility(GONE);
            exoPause.setVisibility(VISIBLE);

            exoVolume.setVisibility(GONE);

            //casting player luôn play first với volume not mute
            //exoVolume.setImageResource(R.drawable.ic_volume_up_black_48dp);
            //UizaDataV3.getInstance().getCasty().setVolume(0.99);

            //double volumeOfExoPlayer = uizaPlayerManagerV3.getVolume();
            //LLog.d(TAG, "volumeOfExoPlayer " + volumeOfExoPlayer);
            //UizaDataV3.getInstance().getCasty().setVolume(volumeOfExoPlayer);
        } else {
            uizaPlayerManagerV3.resumeVideo();
            uizaPlayerManagerV3.setVolume(0.99f);
            rlChromeCast.setVisibility(GONE);
            exoSetting.setVisibility(VISIBLE);
            exoCc.setVisibility(VISIBLE);
            llMid.setVisibility(VISIBLE);

            exoPlay.setVisibility(GONE);
            exoPause.setVisibility(VISIBLE);

            //TODO iplm volume mute on/off o cast player
            exoVolume.setVisibility(VISIBLE);
            //khi quay lại exoplayer từ cast player thì mặc định sẽ bật lại âm thanh (dù cast player đang mute hay !mute)
            //exoVolume.setImageResource(R.drawable.ic_volume_up_black_48dp);
            //uizaPlayerManagerV3.setVolume(0.99f);

            /*double volumeOfCastPlayer = UizaDataV3.getInstance().getCasty().getVolume();
            LLog.d(TAG, "volumeOfCastPlayer " + volumeOfCastPlayer);
            uizaPlayerManagerV3.setVolume((float) volumeOfCastPlayer);*/
        }
    }

    /*Nếu đang casting thì button này sẽ handle volume on/off ở cast player
     * Ngược lại, sẽ handle volume on/off ở exo player*/
    private void handleClickBtVolume() {
        if (isCastingChromecast) {
            LLog.d(TAG, "handleClickBtVolume isCastingChromecast");
            boolean isMute = UizaDataV3.getInstance().getCasty().toggleMuteVolume();
            if (isMute) {
                exoVolume.setImageResource(R.drawable.ic_volume_off_black_48dp);
            } else {
                exoVolume.setImageResource(R.drawable.ic_volume_up_black_48dp);
            }
        } else {
            LLog.d(TAG, "handleClickBtVolume !isCastingChromecast");
            if (uizaPlayerManagerV3 != null) {
                isExoVolumeClicked = true;
                uizaPlayerManagerV3.toggleVolumeMute(exoVolume);
            }
        }
    }
}