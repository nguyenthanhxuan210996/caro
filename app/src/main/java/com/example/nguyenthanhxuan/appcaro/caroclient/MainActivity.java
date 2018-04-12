package com.example.nguyenthanhxuan.appcaro.caroclient;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nguyenthanhxuan.appcaro.Adapter.CaroAdapter;
import com.example.nguyenthanhxuan.appcaro.Adapter.PlayerAdapter;
import com.example.nguyenthanhxuan.appcaro.R;
import com.example.nguyenthanhxuan.appcaro.data.DataKetNoi;
import com.example.nguyenthanhxuan.appcaro.data.ItemCaro;
import com.example.nguyenthanhxuan.appcaro.data.ParseString;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Boolean isYourTurn;//luot cua ban
    private Boolean isPlaying = false;//dang choi
    private Boolean isConnect = false;//ket noi
    private Character cYour;

    private Handler mHandler;
    private ThreadBackgound mThreadBg;//nen chu de
    private ThreadReceive mThreadReceive;
    private List<String> mListPlayer;
    private List<ItemCaro> mListCaro;
    private PlayerAdapter mAdapter;
    private CaroAdapter mCaroAdapter;
    private Dialog mDalogWait;
    private TextView lablePlayer1;
    private TextView lablePlayer2;
    private TextView lableScore1;
    private TextView lableScore2;
    private EditText txtName;
    private ListView ListViewPlayer;
    private GridView GridViewCaro;
    private LinearLayout layoutLBtnExit;
    private LinearLayout layoutRePlayer;
    private LinearLayout layoutLBottom;
    private LinearLayout layoutLCaro;
    private ImageButton btnConnect;
    private ImageButton btnDisConnect;
    private ImageButton btnBack;
    private ImageButton btnNewGame;
    private ImageButton btnMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intnDialogWait();
        mHandler = new Handler();
        mListPlayer = new ArrayList<String>();
        lablePlayer1 = (TextView) findViewById(R.id.lablePlayer1);
        lablePlayer2 = (TextView) findViewById(R.id.lablePlayer2);
        txtName = (EditText) findViewById(R.id.txt_name);
        btnConnect = (ImageButton) findViewById(R.id.btn_connect);
        btnDisConnect = (ImageButton) findViewById(R.id.btn_cancel);
        btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnNewGame = (ImageButton) findViewById(R.id.btn_newGame);
        btnMore = (ImageButton) findViewById(R.id.btn_More);
        layoutLBtnExit = (LinearLayout) findViewById(R.id.layoutLBtnExit);
        layoutRePlayer = (LinearLayout) findViewById(R.id.layoutLePlayer);
        layoutLBottom = (LinearLayout) findViewById(R.id.layoutLBottom);
        layoutLCaro = (LinearLayout) findViewById(R.id.layoutLCaro);
        ListViewPlayer = (ListView) findViewById(R.id.listViewPlayer);
        GridViewCaro = (GridView) findViewById(R.id.gridViewCaro);
        GridViewCaro.setVerticalScrollBarEnabled(false);

        mListCaro = new ArrayList<ItemCaro>();
        mCaroAdapter = new CaroAdapter(this, mListCaro);
        GridViewCaro.setAdapter(mCaroAdapter);
        layoutRePlayer.setOnClickListener(ClickAction);
        btnConnect.setOnClickListener(ClickAction);
        btnDisConnect.setOnClickListener(ClickAction);
        btnBack.setOnClickListener(ClickAction);
        btnMore.setOnClickListener(ClickAction);
        btnNewGame.setOnClickListener(ClickAction);
        GridViewCaro.setOnItemClickListener(ClickItemAction);
        ListViewPlayer.setOnItemClickListener(ClickItemAction2);
    }
    private AdapterView.OnItemClickListener ClickItemAction2 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            mThreadBg.setData(ParseString.strInvite(DataKetNoi.strCurrPlayer, mListPlayer.get(arg2)));
            mDalogWait.show();
        }
    };
    private AdapterView.OnItemClickListener ClickItemAction = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                long arg3) {
            if (isYourTurn && mListCaro.get(pos).getIsYou() == 'n') {
                mListCaro.get(pos).setIsYou(cYour);
                isYourTurn = false;
                FunNotifyCaro();
                mThreadBg.setData(ParseString.strTic(DataKetNoi.strPlayer2, pos + "", cYour));
                FunWin(pos, cYour, mListCaro);
            }
        }
    };
    private View.OnClickListener ClickAction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.btn_connect:
                    mThreadBg = new ThreadBackgound();
                    mThreadReceive = new ThreadReceive();
                    mThreadBg.start();

                    String strPlayer = txtName.getText().toString();
                    if (strPlayer.equals(""))
                        Toast.makeText(getBaseContext(),
                                getText(R.string.toastPlayerExit),
                                Toast.LENGTH_LONG).show();
                    else {
                        DataKetNoi.strCurrPlayer = strPlayer;
                        mThreadBg.setData(ParseString.strConnect(strPlayer));
                    }
                    break;
                case R.id.btn_cancel:
                    FunExit();
                    break;
                case R.id.layoutLePlayer:
                    FunDisconnect();
                    break;
                case R.id.btn_back:
                    FunBack(isConnect, isPlaying);
                    break;
                case R.id.btn_newGame:
                    if (isPlaying)
                        FunNewCaro();
                    break;
            }
        }
    };
    private class ThreadBackgound extends Thread {
        private Boolean isStopAfterExe = false;
        private String strData = null;
        private Boolean isRunBg = true;
        private Socket soc;

        public void mStop() {
            this.isRunBg = false;
            try {
                this.soc.close();
            } catch (Exception e) {
            }
        }

        public void setData(String str) {
            this.strData = str;
        }
        public void run() {
            try {
                soc = new Socket(DataKetNoi.strHost, DataKetNoi.intPort);
                PrintWriter pwOut = new PrintWriter(soc.getOutputStream(), true);
                InputStreamReader inStream = new InputStreamReader(
                        soc.getInputStream());
                BufferedReader buff = new BufferedReader(inStream);
                mThreadReceive.create(buff);
                mThreadReceive.start();
                while (isRunBg) {
                    sleep(200);
                    if (strData != null) {
                        pwOut.println(strData);
                        strData = null;
                        if (isStopAfterExe)
                            this.isRunBg = false;
                    }
                }
            } catch (Exception exx) {
                Log.d("thread bg error", exx.toString());
            }
        }
    }
    private class ThreadReceive extends Thread {
        private BufferedReader buff;
        private Boolean isRunBg = true;

        public void mStop() {
            this.isRunBg = false;
        }

        public void create(BufferedReader buff) {
            this.buff = buff;
        }

        public void run() {
            try {
                String valReceive = null;
                while (isRunBg) {
                    valReceive = buff.readLine();
                    if (valReceive != null) {
                        // Log.d("result receive",valReceive);
                        FunProcResult(valReceive);
                    }
                    if (valReceive == null && isConnect) {
                        FunConnectError();
                        this.isRunBg = false;
                        mThreadBg.mStop();
                    }
                }
            } catch (Exception ex) {
                Log.d("thread receive error", ex.toString());
            }
        }
    }
    public void FunWin(final int pos, final Character c,final List<ItemCaro> mList) {
        new Runnable() {
            @Override
            public void run() {
                if (CheckWin.isWin(pos, c, mList)) {
                    FunDialogWin();
                    mThreadBg.setData(ParseString.strWin(DataKetNoi.strPlayer2));
                }
            }
        }.run();
    }
    public void FunNotifyCaro() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                GridViewCaro.setAdapter(mCaroAdapter);
            }
        });
    }

    public void FunConnectError() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(),
                        getText(R.string.toastConnectError), Toast.LENGTH_SHORT)
                        .show();
                FunExit();
            }
        });
    }
    public void FunProcResult(String result) {
        final String[] arr = result.split("-");
        final int action = Integer.parseInt(arr[0]);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (action) {
                    case 0:// set player
                        isConnect = true;
                        FunSetListPlayer(arr[1]);
                        break;
                    case 1:// update player
                        FunUpdateListPlayer(false, arr[1]);
                        break;
                    case 2: // update player (remove)
                        FunUpdateListPlayer(true, arr[1]);
                        break;
                    case 3: // name exit
                        FunConnectFail();
                        break;
                    case 4:
                        FunShowInvite(arr[1]);
                        break;
                    case 5:
                        FunResultInvite(arr[1]);
                        break;
                    case 6:
                        FunTic(arr[1]);
                        break;
                    case 7:
                        FunResetCaro(true);
                        break;
                    case 8:
                        FunExitCaro(false,R.string.toastEndGame);
                        break;
                    case 9:
                        FunShowWin();
                        break;
                    case 10:
                        FunResetCaro(false);
                        mDalogWait.dismiss();
                        break;
                    case 11:
                        FunNoContinue(false);
                        break;
                }
            }
        });
    }
    private void intnDataCaro(Boolean isTurn, String player2, Character cYour) {
        isYourTurn = isTurn;
        DataKetNoi.strPlayer2 = player2;
        this.cYour = cYour;
    }

    private void intnStartCaro(Boolean isCreate) {
        isPlaying = true;
        mListCaro.clear();
        for (int i = 0; i < 100; i++)
            mListCaro.add(new ItemCaro('n'));
        FunNotifyCaro();
        if (isCreate) {
            FunNotifyCaro();
            FunViewInput(false);
            FunViewListPlayer(false);
            FunViewCaro(true);
        }
    }
    private void intnDialogWait() {
        LayoutInflater inflate = LayoutInflater.from(MainActivity.this);
        View view = inflate.inflate(R.layout.layout_dialog_wait, null);
        mDalogWait = new Dialog(MainActivity.this, R.style.AppTheme);
        mDalogWait.setContentView(view);
    }
    private void FunResetCaro(Boolean isShowMes) {
        if (isShowMes) {
            Toast.makeText(getBaseContext(), getText(R.string.toastReset),
                    Toast.LENGTH_SHORT).show();
            FunUpdatScore(false);
        } else
            FunUpdatScore(true);
        isYourTurn = false;
        intnStartCaro(false);
    }
    private void FunNextCaro() {
        isYourTurn = true;
        mThreadBg.setData(ParseString.strNextCaro(DataKetNoi.strPlayer2));
        intnStartCaro(false);
        FunUpdatScore(false);
    }
    private void FunNewCaro() {
        isYourTurn = true;
        mThreadBg.setData(ParseString.strNewCaro(DataKetNoi.strPlayer2));
        intnStartCaro(false);
        FunUpdatScore(true);
    }
    private void FunNoContinue(Boolean isRequest) {
        FunRemoveInfo();
        isPlaying = false;
        FunViewCaro(false);
        FunViewListPlayer(true);
        FunClearScore();
        if (isRequest)
            mThreadBg.setData(ParseString.strNoContinue(DataKetNoi.strPlayer2));
        else
        {
            mDalogWait.dismiss();
            Toast.makeText(getBaseContext(), getText(R.string.toastNoContinue),Toast.LENGTH_SHORT).show();
        }
        DataKetNoi.strPlayer2 = null;
    }
    private void FunExitCaro(Boolean isRequest,int iMess) {
        FunRemoveInfo();
        isPlaying = false;
        FunViewCaro(false);
        FunViewListPlayer(true);
        FunClearScore();
        if (isRequest)
            mThreadBg.setData(ParseString.strEndCaro(DataKetNoi.strPlayer2));
        else
            Toast.makeText(getBaseContext(), getText(iMess),
                    Toast.LENGTH_SHORT).show();
        DataKetNoi.strPlayer2 = null;
    }
    private void FunBack(Boolean isConnect, Boolean isPlaying) {
        if (isConnect && isPlaying) {
            FunExitCaro(true,R.string.toastEndGame);
            return;
        }
        if (isConnect) {
            FunDisconnect();
            return;
        }
    }

    private void FunExit() {
        this.finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void FunShowWin() {
        final Dialog dialog = new Dialog(MainActivity.this, R.style.AppTheme);
        LayoutInflater inflate = LayoutInflater.from(MainActivity.this);
        View view = inflate.inflate(R.layout.layout_dialog_win, null);
        dialog.setContentView(view);
        ImageButton btnOK = (ImageButton) view.findViewById(R.id.ibtnOkDialog);
        ImageButton btnCannel = (ImageButton) view
                .findViewById(R.id.ibtnCannelDialog);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunNextCaro();
                dialog.dismiss();
            }
        });
        btnCannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                FunNoContinue(true);
            }
        });
        dialog.show();

    }
    private void FunShowInvite(final String Invitefrom) {
        final Dialog dilog = new Dialog(MainActivity.this,
                R.style.AppTheme);
        LayoutInflater inflate = LayoutInflater.from(MainActivity.this);
        View view = inflate.inflate(R.layout.layout_dialog2, null);
        dilog.setContentView(view);
        TextView text = (TextView) view.findViewById(R.id.lablePlayerDialog);
        ImageButton btnOK = (ImageButton) view.findViewById(R.id.ibtnOkDialog);
        ImageButton btnCannel = (ImageButton) view
                .findViewById(R.id.ibtnCannelDialog);
        text.setText(Invitefrom);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThreadBg.setData(ParseString.strReplyInvite("2", Invitefrom,
                        DataKetNoi.strCurrPlayer));
                dilog.dismiss();
                intnDataCaro(false, Invitefrom, 'o');
                intnStartCaro(true);
                FunSetInfo();
            }
        });
        btnCannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThreadBg.setData(ParseString.strReplyInvite("1", Invitefrom,
                        " "));
                dilog.dismiss();
            }
        });
        dilog.show();
    }
    private void FunDialogWin() {
        mDalogWait = new Dialog(MainActivity.this, R.style.AppTheme);
        LayoutInflater inflate = LayoutInflater.from(MainActivity.this);
        View view = inflate.inflate(R.layout.layout_dialog_wait, null);
        mDalogWait.setContentView(view);
        TextView lable = (TextView) view.findViewById(R.id.lableWaitDialog);
        lable.setText(R.string.strDialogWin);
        mDalogWait.show();
    }

    private void FunResultInvite(String result) {
        mDalogWait.dismiss();
        String[] arr = result.split(":");
        if (arr[0].equals("0")) {
            Toast.makeText(getBaseContext(), R.string.toastBusy,
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (arr[0].equals("1")) {
            Toast.makeText(getBaseContext(), R.string.toastDennie,
                    Toast.LENGTH_LONG).show();
            return;
        }
        intnDataCaro(true, arr[1], 'x');
        intnStartCaro(true);
        FunSetInfo();
    }

    private void FunTic(String result) {
        isYourTurn = true;
        String[] arr = result.split(":");
        int pos = Integer.parseInt(arr[0]);
        if (pos < 100) {
            mListCaro.get(pos).setIsYou(arr[1].charAt(0));
            FunNotifyCaro();
        }
    }

    private void FunDisconnect() {
        isConnect = false;
        mThreadBg.setData(ParseString.strDisConnect(DataKetNoi.strCurrPlayer));
        DataKetNoi.strCurrPlayer = null;
        FunViewInput(true);
        FunViewListPlayer(false);
        try {
            mThreadBg.isStopAfterExe = true;
            mThreadReceive.mStop();
        } catch (Exception e) {
        }
    }

    private void FunConnectFail() {
        DataKetNoi.strCurrPlayer = null;
        Toast.makeText(getBaseContext(), getText(R.string.toastPlayerExit),
                Toast.LENGTH_LONG).show();
    }

    private void FunSetListPlayer(String result) {
        FunViewListPlayer(true);
        FunViewInput(false);
        mListPlayer = ParseString.getListPlayer(result);
        mAdapter = new PlayerAdapter(MainActivity.this, mListPlayer);
        ListViewPlayer.setAdapter(mAdapter);
    }

    private void FunUpdateListPlayer(Boolean isRemove, String result) {
        if (isRemove)
            mListPlayer.remove(result);
        else
            mListPlayer.add(result);
        mAdapter.notifyDataSetChanged();
    }

    private void FunUpdatScore(Boolean isPlayer1) {
        int score;
        if (isPlayer1) {
            score = Integer.parseInt(lableScore1.getText().toString());
            lableScore1.setText((score + 1) + "");
            return;
        }
        score = Integer.parseInt(lableScore2.getText().toString());
        lableScore2.setText((score + 1) + "");
    }

    private void FunClearScore() {
        lableScore1.setText("-");
        lableScore2.setText("-");
    }

    private void FunRemoveInfo() {
        FunClearScore();
        lablePlayer1.setText("");
        lablePlayer2.setText("");
    }

    private void FunSetInfo() {

        lablePlayer1.setText(DataKetNoi.strCurrPlayer);
        lablePlayer2.setText(DataKetNoi.strPlayer2);
        lableScore1.setText("0");
        lableScore2.setText("0");
    }


    private void FunViewCaro(Boolean isShow) {
        Animation anim;
        if (isShow) {
            if (layoutLCaro.getVisibility() == View.GONE) {
                anim = AnimationUtils.loadAnimation(MainActivity.this,
                        R.anim.game_in);
                layoutLCaro.startAnimation(anim);
                layoutLCaro.setVisibility(View.VISIBLE);
            }
        } else if (layoutLCaro.getVisibility() == View.VISIBLE) {
            anim = AnimationUtils.loadAnimation(MainActivity.this,
                    R.anim.game_out);
            layoutLCaro.startAnimation(anim);
            layoutLCaro.setVisibility(View.GONE);
        }
    }

    private void FunViewListPlayer(Boolean isShow) {
        // Animation anim;
        if (isShow) {
            if (layoutRePlayer.getVisibility() == View.GONE) {
                layoutRePlayer.setVisibility(View.VISIBLE);
            }
        } else {
            if (layoutRePlayer.getVisibility() == View.VISIBLE) {
                layoutRePlayer.setVisibility(View.GONE);
            }
        }
    }

    private void FunViewInput(Boolean isShow) {
        Animation anim;
        if (isShow) {
            if (layoutLBottom.getVisibility() == View.GONE) {
                anim = AnimationUtils.loadAnimation(MainActivity.this,
                        R.anim.bottom_in);
                layoutLBottom.setAnimation(anim);
                layoutLBottom.setVisibility(View.VISIBLE);
            }
        } else {
            if (layoutLBottom.getVisibility() == View.VISIBLE) {
                anim = AnimationUtils.loadAnimation(MainActivity.this,
                        R.anim.bottom_out);
                layoutLBottom.startAnimation(anim);
                layoutLBottom.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        FunDisconnect();
        super.onDestroy();
    }

}
