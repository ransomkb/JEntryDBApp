/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jentrydbapp;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ransomkb
 */
public class JEntry
{
    private boolean bKanji; //Kanji value of an entry exists
    private boolean bRead; //Reading value of an entry exists
    private boolean bMean; //English meaning of an entry exists
    private boolean bExamp; //Example of an entry exists
    
    public KanjiElem kElem;
    public ReadingElem rElem;
    public SenseContainer sCont;

    public JEntry()
    {
        kElem = new KanjiElem();
        rElem = new ReadingElem();
        sCont = new SenseContainer();
        
        bKanji = false;
        bRead = false;
        bMean = false;
        bExamp = false;
    }
    
    public boolean isbKanji()
    {
        return bKanji;
    }

    public void setbKanji(boolean bKanji)
    {
        this.bKanji = bKanji;
    }
    
    public boolean isbRead()
    {
        return bRead;
    }

    public void setbRead(boolean bRead)
    {
        this.bRead = bRead;
    }

    public boolean isbMean()
    {
        return bMean;
    }

    public void setbMean(boolean bMean)
    {
        this.bMean = bMean;
    }

    public boolean isbExamp()
    {
        return bExamp;
    }
    
    public void setbExamp(boolean bExamp)
    {
        this.bExamp = bExamp;
    }
    
}
