package com.example.nguyenthanhxuan.appcaro.caroclient;

import com.example.nguyenthanhxuan.appcaro.data.ItemCaro;

import java.util.List;

/**
 * Created by Nguyen Thanh Xuan on 4/11/2018.
 */

public class CheckWin {
    public static Boolean isWin(int pos,Character c,List<ItemCaro> mList)
    {
        if(checkHorizontion(pos, c, mList))
            return true;
        if(checkVertical(pos, c, mList))
            return true;
        if(checkCross1(pos, c, mList))
            return true;
        if(checkCross2(pos, c, mList))
            return true;
        return false;
    }
    private static Boolean checkHorizontion(int pos,Character c,List<ItemCaro> mList)
    {
        int count=0;
        pos = pos % 8;// chia lay phan du
        for(int i=0;i<8;i++)
        {
            if(mList.get(i*8+pos).getIsYou() == c)
            {
                count++;
                if(count ==5)
                    return true;
            }
            else
                count=0;
        }
        return false;
    }
    private static Boolean checkVertical(int pos,Character c,List<ItemCaro> mList)
    {
        int count=0;
        pos = pos / 8;//chia lay phan nguyen
        for(int i=0;i<8;i++)
        {
            if(mList.get(pos*8+i).getIsYou() == c)
            {
                count++;
                if(count ==5)
                    return true;
            }
            else
                count=0;
        }
        return false;
    }
    private static Boolean checkCross2(int pos,Character c,List<ItemCaro> mList)
    {
        int count = 0;
        while( pos > 8 && pos % 8 < 8 )
            pos -=7;
        while(pos % 8 !=0 && pos / 8 < 8)
        {
            if(mList.get(pos).getIsYou() == c)
            {
                count++;
                if(count == 5)
                    return true;
            }
            else
                count = 0;
            pos +=7;
        }
        return false;
    }
    private static Boolean checkCross1(int pos,Character c,List<ItemCaro> mList)
    {
        int count = 0;
        while( pos > 8 && pos % 8 !=0 )
            pos -=9;
        while(pos / 8 < 8 && pos % 8 < 8)
        {
            if(mList.get(pos).getIsYou() == c)
            {
                count++;
                if(count == 5)
                    return true;
            }
            else
                count = 0;
            pos +=9;
        }
        return false;
    }
}
