package com.example.nguyenthanhxuan.appcaro.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nguyen Thanh Xuan on 4/11/2018.
 */

public class ParseString {
    public static String strNoContinue(String player2)
    {
        return "10-"+player2;
    }
    public static String strSendMess(String player2,String mes)
    {
        return "9-"+player2+":"+mes;
    }
    public static String strNextCaro(String player2)
    {
        return "8-"+player2;
    }
    public static String strWin(String player2)
    {
        return "7-"+player2;
    }
    public static String strEndCaro(String player2)
    {
        return "6-"+player2;
    }
    public static String strNewCaro(String player2)
    {
        return "5-"+player2;
    }
    public static String strTic(String toPlayer,String xy,Character c)
    {
        // xy > 99 máº¥t lÆ°á»£t
        return "4-"+toPlayer+":"+xy+":"+c;
    }
    public static String strReplyInvite(String result,String player1,String player2)
    {
        return "3-"+result+":"+player1+":"+player2;
    }
    public static String strInvite(String you,String player2)
    {
        return "2-"+you+":"+player2;
    }
    public static String strDisConnect(String currPlayer)
    {
        return "1-"+currPlayer;
    }
    public static String strConnect(String player)
    {
        return "0-"+player;
    }
    public static List<String> getListPlayer(String strResult)
    {

        List<String> result = new ArrayList<String>();
        if(strResult.equals("0"))
            return result;
        String[] arr = strResult.split(":");
        for(int i=0;i<arr.length;i++)
            result.add(arr[i]);
        return result;
    }
}
